package com.mobileenerlytics.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobileenerlytics.entity.TestRecord;
import com.mobileenerlytics.entity.ThreadCompEnergy;
import com.mobileenerlytics.repository.TestRecordRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DBOperation {
    private static Logger logger = LoggerFactory.getLogger(DBOperation.class);
    private static final String LOGNAME = "tester_logname";//??

    @Autowired
    private MongoDatabase database;

    @Autowired
    private TestRecordRepository testRecordRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(DBOperation.class);


    @JsonIgnoreProperties(ignoreUnknown=true)
    static class JsonOutput {
        String taskName;
        int taskId;
        boolean mIsProc;
        int parentTaskId;
        PerComponentEnergy perComponentEnergy = new PerComponentEnergy();

        @JsonCreator
        public JsonOutput() {}

        @JsonIgnoreProperties(ignoreUnknown=true)
        class PerComponentEnergy {
            @JsonCreator
            public PerComponentEnergy(){}
            double CPU;
            double GPU;
        }
    }

    public TestRecord updateDbFromJson(File jsonFile, String pkgName, String deviceName, ObjectId commitId, String testcaseName)
            throws Exception {
        logger.info("updateDbFromJson:{} pkg:{} version:{} test:{}", jsonFile.getAbsolutePath(), pkgName,
                commitId, testcaseName);
        Set<ThreadCompEnergy> threadEnergies = new HashSet<>();

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<JsonOutput>> mapType = new TypeReference<List<JsonOutput>>() {};
        InputStream is = new FileInputStream(jsonFile.getAbsolutePath());
        try {

            List<JsonOutput> jsonOutputs = mapper.readValue(is, mapType);
            System.out.println("States list saved successfully");
            JsonOutput jsonPhone = jsonOutputs.get(0);
            double gpuSum = jsonPhone.perComponentEnergy.GPU;
            int pid = -1;
            for (JsonOutput jsonOutput : jsonOutputs) {
                if (pkgName.equals(jsonOutput.taskName)) {
                    pid = jsonOutput.taskId; // ?
                    break;
                }
            }

            JsonOutput gpuJsonOutout = new JsonOutput();
            gpuJsonOutout.perComponentEnergy.GPU = gpuSum;
            gpuJsonOutout.parentTaskId = pid;
            gpuJsonOutout.taskName = "GPU-" + pkgName;
            gpuJsonOutout.taskId = 32678*4; // random id
            jsonOutputs.add(gpuJsonOutout);
            logger.info("Inserting data for pid {}", pid);

            for (JsonOutput jsonOutput : jsonOutputs) {
                if (jsonOutput.taskId != pid && jsonOutput.parentTaskId != pid) continue;
                threadEnergies.add(new ThreadCompEnergy(jsonOutput.taskName, "CPU", jsonOutput.perComponentEnergy.CPU, new Date()));
                threadEnergies.add(new ThreadCompEnergy(jsonOutput.taskName, "GPU", jsonOutput.perComponentEnergy.GPU, new Date()));
            }
            return addToDb(commitId, testcaseName, threadEnergies);
        } catch (IOException e) {LOGGER.info("", e.getMessage());}
        return null;
    }

    public TestRecord addToDb(ObjectId commitId, String testcaseName, Set<ThreadCompEnergy> threadEnergies){

        //todo save record
        TestRecord testRecord = testRecordRepository.findBy(testcaseName, commitId);
        if(testRecord == null) {
            testRecord = new TestRecord(testcaseName, commitId, threadEnergies);
        } else {
            throw new RuntimeException("duplicate test record with commit id " + commitId);
        }

        //todo , calling post-precessing api, get the testrecord, then save it in testRecordService
        testRecord = testRecordRepository.save(testRecord);
        return testRecord;
    }

    public Runnable getUploadedTraceZipHandler(final File uploadedFile, final String pkgName, final String deviceName, ObjectId commitId
                                               , final DBOperation dbOperation) {

        return new Runnable() {
            @Override
            public void run() {
                Date proccessed = startProcessing();
                TestRecord record = unzipAndProcessEnergy();
                endProcessing(proccessed, record);
            }

            private Date startProcessing() {
                Date proccessed = new Date();
                Document oneAndUpdate = null;
                try {

                    MongoCollection<Document> collection = database.getCollection("Commit");
                    Document filter = new Document("_id", commitId);
                    oneAndUpdate = collection.findOneAndUpdate(
                            filter,
                            new BasicDBObject("$set", new BasicDBObject("jobStatus", Constants.COMMIT_STATUS_PROCESSING)),
                            new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return proccessed;
            }

            private TestRecord unzipAndProcessEnergy() {
                TestRecord testRecord = null;
                String parentDir = uploadedFile.getParent();
                String zipFileName = uploadedFile.getName();
                int dotIndex = zipFileName.lastIndexOf(".zip");
                String dirName = zipFileName.substring(0, dotIndex);
                File unzipDir = new File(parentDir + File.separator + dirName);
//                unzipDir.mkdirs();
//                ZipUtil.unpack(uploadedFile, unzipDir);

                // Find and process all trace folders
                File[] traceDirs = unzipDir.listFiles();
                for (File traceDir : traceDirs) {

                    assert traceDir.isDirectory();
                    try {
                        //Extract testcase name from build.prop file
                        String testcaseName = null;
                        File buildPropFile = traceDir.toPath().resolve("build.prop").toFile();
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(buildPropFile));
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            String[] tokens = line.split("=");
                            if (tokens.length == 2 && tokens[0].equals(LOGNAME)) {
                                testcaseName = tokens[1];
                                break;
                            }
                        }
                        if (testcaseName == null)
                            throw new RuntimeException("test case name not set in the trace");

                        logger.info("Processing " + traceDir.getAbsolutePath());
                        RunEprof runEprof = new RunEprof(traceDir.getAbsolutePath());
                        runEprof.run();
                        if (!runEprof.isSuccess())
                            throw new RuntimeException("RunEprof unsuccessful");

                        File eprofOutDir = traceDir.toPath().resolve("eprof.files").toFile().getAbsoluteFile();
                        logger.info("Updating database from processed traces at " + traceDir.getAbsolutePath());
                        // update database from json output by eprof
                        File jsonFile = eprofOutDir.toPath().resolve("app.json").toFile();
                        testRecord = dbOperation.updateDbFromJson(jsonFile, pkgName, deviceName, commitId, testcaseName);


                        logger.info("Uploading files to S3 " + eprofOutDir);

                        //todo
//                        TestRecordService.uploadFolder("" + testRecord.getId(), eprofOutDir);

                    } catch (Exception e) {
                        logger.error("Failed to process " + traceDir.getAbsolutePath());
                        e.printStackTrace();
                    }
                }
                return testRecord;
            }

            private void endProcessing(Date proccessed, TestRecord testRecord) {
                Document oneAndUpdate;
                long duration = new Date().getTime() - proccessed.getTime();
                try {
                    // commit
                    MongoCollection<Document> commitCollection = database.getCollection("Commit");
                    Document filter = new Document("_id", commitId);
                    oneAndUpdate = commitCollection.findOneAndUpdate(
                            filter,
                            new BasicDBObject("$set", new BasicDBObject("jobStatus", Constants.COMMIT_STATUS_DONE).append("jobDurationMs", duration))
                                    .append("$push", new BasicDBObject("testRecords", testRecord.getId())),
                            new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
                    );
                    //branch
                    MongoCollection<Document> branchCollection = database.getCollection("Branch");
                    ObjectId branchId = (ObjectId) oneAndUpdate.get("branch_id");
                    Document branch = branchCollection.findOneAndUpdate(
                            new Document("_id", branchId),
                            new BasicDBObject("$push", new BasicDBObject("tests", testRecord.getTestName())),
                            new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

}
