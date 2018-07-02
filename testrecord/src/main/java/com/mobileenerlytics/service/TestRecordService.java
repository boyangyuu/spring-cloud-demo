package com.mobileenerlytics.service;

public class TestRecordService {
//    private MongoDatabase database = ProHibernateUtil.getInstance().getDatabase();
//    private static Logger logger = LoggerFactory.getLogger(TestRecordService.class);
//    private static AmazonS3 s3 = SDKProvider.createAWSSDK();
//    private static String bucketName = PropProvider.getConfigProperties().getProperty("aws_s3_bucket_name");
//    private static String dir = PropProvider.getConfigProperties().getProperty("outputDir");
//    private static long chunkSize = 1024 * 1024;
//    @Context
//    HttpHeaders httpHeaders;
//
//    @GET
//    @Path("/{id}/{path: .*}/")
//    public Response getFiles(
//            @PathParam("id") String id,
//            @PathParam("path") String path,
//            @HeaderParam("Range") String range) {
//        String uri = id + "/" + path;
//        logger.info("Downloading an object, with the path:" + uri);
//        CacheControl control = new CacheControl();
//        control.setMaxAge(60*10);   // 10 minutes
//        try {
//            if (s3 == null) {
//                String desPath = dir + uri;
//                File file = new File(desPath);
//                logger.info("Content "  +file);
//                return Response.ok(new FileInputStream(file)).cacheControl(control).build();
//            } else {
//                GetObjectRequest objectRequest = new GetObjectRequest(bucketName, uri);
//
//                //Partial Content
//                if (range != null) {
//                    String[] ranges = range.split("=")[1].split("-");
//                    long from = Long.parseLong(ranges[0]);
//                    long to = from + chunkSize;
//                    if (ranges.length == 2) {
//                        to = Long.parseLong(ranges[1]);
//                    }
//                    objectRequest.setRange(from, to);
//                    S3Object object = s3.getObject(objectRequest);
//                    ObjectMetadata meta = object.getObjectMetadata();
//                    String contentRange = (String) meta.getRawMetadata().get(Headers.CONTENT_RANGE);
//                    return Response.ok(object.getObjectContent())
//                            .header("Accept-Ranges", "bytes")
//                            .header("Content-Range", contentRange)
//                            .header("Content-Length", meta.getContentLength())
//                            .header("Last-Modified", meta.getLastModified())
//                            .status(Response.Status.PARTIAL_CONTENT)
//                            .cacheControl(control)
//                            .build();
//
//                } else {
//                    S3Object object = s3.getObject(objectRequest);
//                    return Response.ok(object.getObjectContent()).cacheControl(control).build();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new NotFoundException(Response
//                    .status(Response.Status.NOT_FOUND)
//                    .entity("path not found in S3 bucket: " + path)
//                    .build());
//        }
//    }
//
//    //        TestRecordService.uploadFolder("testrecordId", "/Users/boyang/Documents/work/server/trial/src/main/resources/output/sample/eprof.files");
//    public static void uploadFolder(String testrecordId, File folder) throws IOException {
//        if (s3 == null) {
//            logger.info("uploadingFolder to local path:" + testrecordId + "/ "+ folder);
//            FileUtils.copyDirectory(folder, new File(dir + testrecordId));
//        } else {
//            logger.info("uploadingFolder to s3 path:" + testrecordId + "/ "+ folder);
//            File[] files = folder.listFiles();
//            uploadFiles(files, testrecordId);
//        }
//    }
//
//    private static void uploadFiles(File[] files, String pathFile) throws IOException {
//        for (File file : files) {
//            if (file.isDirectory()) {
//                String folderName = file.getName();
//                logger.info("Directory: " + folderName);
//                uploadFiles(file.listFiles(), pathFile + "/" + folderName);
//
//            } else {
//                logger.info("uploading name: " + file.getName());
//                String path = pathFile + "/" + file.getName();
//                s3.putObject(new PutObjectRequest(bucketName, path, file));
//            }
//        }
//    }

}
