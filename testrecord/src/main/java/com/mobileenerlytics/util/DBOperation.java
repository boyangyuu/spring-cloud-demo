package com.mobileenerlytics.util;

public class DBOperation {
//    private static String dbDir;
//    private static Map<String, DBOperation> dbOperation = new HashMap<>();
//    private File persistentSummaryDb;
//    private static Logger logger = LoggerFactory.getLogger(DBOperation.class);
//    private MongoDatabase database = ProHibernateUtil.getInstance().getDatabase();
//
//    // LOGNAME string must match with the TesterApp
//    private static final String LOGNAME = "tester_logname";
//
//    public DBOperation() {
//
//    }
//
//    public static <T> T getSingleResult(TypedQuery<T> query) {
//        try {
//            T result = query.getSingleResult();
//            return result;
//        } catch(NoResultException | EntityNotFoundException e) {
//            return null;
//        }
//    }
//
//    class JsonOutput {
//        String taskName;
//        int taskId;
//        int parentTaskId;
//        PerComponentEnergy perComponentEnergy = new PerComponentEnergy();
//        class PerComponentEnergy {
//            double CPU;
//            double GPU;
//        }
//    }
//
//    public TestRecord updateDbFromJson(File jsonFile, String pkgName, String deviceName, Commit commit, String testcaseName)
//            throws Exception {
//        logger.debug("updateDbFromJson:{} pkg:{} version:{} test:{}", jsonFile.getAbsolutePath(), pkgName,
//                commit.getHash(), testcaseName);
//        Set<ThreadCompEnergy> threadEnergies = new HashSet<>();
//        try (FileReader jsonReader = new FileReader(jsonFile)) {
//            Type listType = new TypeToken<List<JsonOutput>>() {
//            }.getType();
//            List<JsonOutput> jsonOutputs = new Gson().fromJson(jsonReader, listType);
//            JsonOutput jsonPhone = jsonOutputs.get(0);
//            double gpuSum = jsonPhone.perComponentEnergy.GPU;
//            int pid = -1;
//            for (JsonOutput jsonOutput : jsonOutputs) {
//                if (pkgName.equals(jsonOutput.taskName)) {
//                    pid = jsonOutput.taskId; // ?
//                    break;
//                }
//            }
//
//            JsonOutput gpuJsonOutout = new JsonOutput();
//            gpuJsonOutout.perComponentEnergy.GPU = gpuSum;
//            gpuJsonOutout.parentTaskId = pid;
//            gpuJsonOutout.taskName = "GPU-" + pkgName;
//            gpuJsonOutout.taskId = 32678*4; // random id
//            jsonOutputs.add(gpuJsonOutout);
//            logger.info("Inserting data for pid {}", pid);
//
//            for (JsonOutput jsonOutput : jsonOutputs) {
//                if (jsonOutput.taskId != pid && jsonOutput.parentTaskId != pid) continue;
//                threadEnergies.add(new ThreadCompEnergy(jsonOutput.taskName, "CPU", jsonOutput.perComponentEnergy.CPU, commit.getUpdatedMs()));
//                threadEnergies.add(new ThreadCompEnergy(jsonOutput.taskName, "GPU", jsonOutput.perComponentEnergy.GPU, commit.getUpdatedMs()));
//            }
//            return addToDb(commit, testcaseName, threadEnergies);
//        }
//    }
//
//    @VisibleForTesting
//    public TestRecord addToDb(Commit commit, String testcaseName, Set<ThreadCompEnergy> threadEnergies) throws
//            Exception {
//        TransactionManager tm = ProHibernateUtil.getInstance().getTransactionManager();
//        tm.begin();
//        EntityManager em = ProHibernateUtil.getInstance().createEntityManager();
//
//        TestRecord testRecord = TestRecord.queryTestRecord(em, testcaseName, commit);
//        if(testRecord == null) {
//            testRecord = new TestRecord(testcaseName, commit, new Date(), threadEnergies);
//            em.persist(testRecord);
//        } else {
//            throw new Exception("duplicate test record with commit id " + commit.getId());
//        }
//        em.flush();
//        em.close();
//        tm.commit();
//        return testRecord;
//    }
//
//
//    public Runnable getUploadedTraceZipHandler(final File uploadedFile, final String pkgName, final String deviceName,
//                                               final Commit commit, final DBOperation dbOperation) throws SystemException, NotSupportedException {
//        return new Runnable() {
//            @Override
//            public void run() {
//                Date proccessed = startProcessing();
//                unzipAndProcessEnergy();
//                endProcessing(proccessed);
//            }
//
//            private Date startProcessing() {
//                Date proccessed = new Date();
//                Document oneAndUpdate = null;
//                try {
//                    MongoCollection<Document> collection = database.getCollection("Commit");
//                    Document filter = new Document("_id", commit.getId());
//                    oneAndUpdate = collection.findOneAndUpdate(
//                            filter,
//                            new BasicDBObject("$set", new BasicDBObject("jobStatus", Constants.COMMIT_STATUS_PROCESSING)),
//                            new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
//                    );
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return proccessed;
//            }
//
//            private void unzipAndProcessEnergy() {
//                String parentDir = uploadedFile.getParent();
//                String zipFileName = uploadedFile.getName();
//                int dotIndex = zipFileName.lastIndexOf(".zip");
//                String dirName = zipFileName.substring(0, dotIndex);
//                File unzipDir = new File(parentDir + File.separator + dirName);
//                unzipDir.mkdirs();
//                ZipUtil.unpack(uploadedFile, unzipDir);
//
//                // Find and process all trace folders
//                File[] traceDirs = unzipDir.listFiles();
//                for (File traceDir : traceDirs) {
//                    assert traceDir.isDirectory();
//                    try {
//                        //Extract testcase name from build.prop file
//                        String testcaseName = null;
//                        File buildPropFile = traceDir.toPath().resolve("build.prop").toFile();
//                        BufferedReader bufferedReader = new BufferedReader(new FileReader(buildPropFile));
//                        String line;
//                        while ((line = bufferedReader.readLine()) != null) {
//                            String[] tokens = line.split("=");
//                            if (tokens.length == 2 && tokens[0].equals(LOGNAME)) {
//                                testcaseName = tokens[1];
//                                break;
//                            }
//                        }
//                        if (testcaseName == null)
//                            throw new RuntimeException("test case name not set in the trace");
//
//                        logger.info("Processing " + traceDir.getAbsolutePath());
//                        RunEprof runEprof = new RunEprof(traceDir.getAbsolutePath());
//                        runEprof.run();
//                        if (!runEprof.isSuccess())
//                            throw new RuntimeException("RunEprof unsuccessful");
//
//                        File eprofOutDir = traceDir.toPath().resolve("eprof.files").toFile().getAbsoluteFile();
//                        logger.info("Updating database from processed traces at " + traceDir.getAbsolutePath());
//                        // update database from json output by eprof
//                        File jsonFile = eprofOutDir.toPath().resolve("app.json").toFile();
//                        TestRecord testRecord = dbOperation.updateDbFromJson(jsonFile, pkgName, deviceName, commit, testcaseName);
//                        logger.info("Uploading files to S3 " + eprofOutDir);
//                        TestRecordService.uploadFolder("" + testRecord.getId(), eprofOutDir);
//                    } catch (Exception e) {
//                        logger.error("Failed to process " + traceDir.getAbsolutePath());
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            private void endProcessing(Date proccessed) {
//                Document oneAndUpdate;
//                long duration = new Date().getTime() - proccessed.getTime();
//                try {
//                    MongoCollection<Document> collection = database.getCollection("Commit");
//                    Document filter = new Document("_id", commit.getId());
//                    oneAndUpdate = collection.findOneAndUpdate(
//                            filter,
//                            new BasicDBObject("$set", new BasicDBObject("jobStatus", Constants.COMMIT_STATUS_DONE).append("jobDurationMs", duration)),
//                            new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
//                    );
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//    }
//
//    //todo why?
//    public static DBOperation getInstance(String user) throws IOException {
//        if (!dbOperation.containsKey(user)) {
//            logger.debug("db dir: " + dbDir);
//            //dbOperation.put(user, new DBOperation(userDbFile));
//            dbOperation.put(user, new DBOperation());
//        }
//        return dbOperation.get(user);
//    }
//
//    public static String getDbDir(File summaryDbFile) {
//        Path summaryPath = summaryDbFile.toPath().getParent();
//        File dbDir = summaryPath.resolve("user-summaries").toFile();
//        dbDir.mkdirs();
//        return dbDir.getAbsolutePath();
//    }
}
