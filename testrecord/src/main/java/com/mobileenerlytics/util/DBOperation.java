package com.mobileenerlytics.util;

//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobileenerlytics.entity.Commit;
import com.mobileenerlytics.entity.TestRecord;
import com.mobileenerlytics.entity.ThreadCompEnergy;
import com.mobileenerlytics.repository.TestRecordRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.zeroturnaround.zip.ZipUtil;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class DBOperation {
    private static String dbDir;
    private static Map<String, DBOperation> dbOperation = new HashMap<>();
    private File persistentSummaryDb;
    private static Logger logger = LoggerFactory.getLogger(DBOperation.class);
    private static final String LOGNAME = "tester_logname";//??

    @Autowired
    private MongoDatabase database;

    @Autowired
    private TestRecordRepository testRecordRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(DBOperation.class);
    class JsonOutput {
        String taskName;
        int taskId;
        int parentTaskId;
        PerComponentEnergy perComponentEnergy = new PerComponentEnergy();
        class PerComponentEnergy {
            double CPU;
            double GPU;
        }
    }

    public TestRecord updateDbFromJson(File jsonFile, String pkgName, String deviceName, Commit commit, String testcaseName)
            throws Exception {
        logger.debug("updateDbFromJson:{} pkg:{} version:{} test:{}", jsonFile.getAbsolutePath(), pkgName,
                commit.getHash(), testcaseName);
        Set<ThreadCompEnergy> threadEnergies = new HashSet<>();

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<JsonOutput>> mapType = new TypeReference<List<JsonOutput>>() {};
        InputStream is = TypeReference.class.getResourceAsStream(jsonFile.getAbsolutePath());
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
                threadEnergies.add(new ThreadCompEnergy(jsonOutput.taskName, "CPU", jsonOutput.perComponentEnergy.CPU, commit.getUpdatedMs()));
                threadEnergies.add(new ThreadCompEnergy(jsonOutput.taskName, "GPU", jsonOutput.perComponentEnergy.GPU, commit.getUpdatedMs()));
            }
            return addToDb(commit.getId(), testcaseName, threadEnergies);
        } catch (IOException e) {LOGGER.info("", e.getMessage());}
        return null;
    }

    public TestRecord addToDb(String commitId, String testcaseName, Set<ThreadCompEnergy> threadEnergies){

        //todo save record
        TestRecord testRecord = testRecordRepository.findBy(testcaseName, commitId);
        if(testRecord == null) {
            testRecord = new TestRecord(testcaseName, commitId, threadEnergies);
        } else {
            throw new RuntimeException("duplicate test record with commit id " + commitId);
        }
        testRecord = testRecordRepository.save(testRecord);
        return testRecord;
    }

    public Runnable getUploadedTraceZipHandler(final File uploadedFile, final String pkgName, final String deviceName,
                                               final Commit commit, final DBOperation dbOperation) {
        return new Runnable() {
            @Override
            public void run() {
                Date proccessed = startProcessing();
                unzipAndProcessEnergy();
                endProcessing(proccessed);
            }

            private Date startProcessing() {
                Date proccessed = new Date();
                Document oneAndUpdate = null;
                try {
                    MongoCollection<Document> collection = database.getCollection("Commit");
                    Document filter = new Document("_id", commit.getId());
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

            private void unzipAndProcessEnergy() {
                String parentDir = uploadedFile.getParent();
                String zipFileName = uploadedFile.getName();
                int dotIndex = zipFileName.lastIndexOf(".zip");
                String dirName = zipFileName.substring(0, dotIndex);
                File unzipDir = new File(parentDir + File.separator + dirName);
                unzipDir.mkdirs();
                ZipUtil.unpack(uploadedFile, unzipDir);

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
                        TestRecord testRecord = dbOperation.updateDbFromJson(jsonFile, pkgName, deviceName, commit, testcaseName);
                        logger.info("Uploading files to S3 " + eprofOutDir);

                        //todo
//                        TestRecordService.uploadFolder("" + testRecord.getId(), eprofOutDir);
                    } catch (Exception e) {
                        logger.error("Failed to process " + traceDir.getAbsolutePath());
                        e.printStackTrace();
                    }
                }
            }

            private void endProcessing(Date proccessed) {
                Document oneAndUpdate;
                long duration = new Date().getTime() - proccessed.getTime();
                try {
                    MongoCollection<Document> collection = database.getCollection("Commit");
                    Document filter = new Document("_id", commit.getId());
                    oneAndUpdate = collection.findOneAndUpdate(
                            filter,
                            new BasicDBObject("$set", new BasicDBObject("jobStatus", Constants.COMMIT_STATUS_DONE).append("jobDurationMs", duration)),
                            new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    //todo why?
    public static DBOperation getInstance(String user) throws IOException {
        if (!dbOperation.containsKey(user)) {
            logger.debug("db dir: " + dbDir);
            //dbOperation.put(user, new DBOperation(userDbFile));
            dbOperation.put(user, new DBOperation());
        }
        return dbOperation.get(user);
    }

    public static String getDbDir(File summaryDbFile) {
        Path summaryPath = summaryDbFile.toPath().getParent();
        File dbDir = summaryPath.resolve("user-summaries").toFile();
        dbDir.mkdirs();
        return dbDir.getAbsolutePath();
    }
}
