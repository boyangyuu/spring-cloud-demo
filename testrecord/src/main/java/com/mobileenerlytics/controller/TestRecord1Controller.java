package com.mobileenerlytics.controller;

import com.mobileenerlytics.entity.*;
import com.mobileenerlytics.repository.*;
import com.mobileenerlytics.util.DBOperation;
import com.mongodb.client.MongoDatabase;
import com.sun.jersey.core.header.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;

@RestController
@RequestMapping("api/upload")
public class TestRecord1Controller {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private ContributorRepository contributorRepository;

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private TestRecordRepository testRecordRepository;

    @Autowired
    private DBOperation dbOperation;

    @Autowired
    private MongoDatabase mongoDatabase;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRecord1Controller.class);

    @Autowired
    private RestTemplate restTemplate; // consume other api.

    @PostMapping
    public ResponseEntity<Object> signup(
            @RequestParam("file") InputStream fileInputStream,
            @RequestParam("file") FormDataContentDisposition contentDispositionHeader,
            @RequestParam("device") String deviceName,
            @RequestParam("author_name") String authorName,
            @RequestParam("author_email") String authorEmail,
            @RequestParam("commit") String commitHash,
            @RequestParam("branch") String branchName,
            @RequestParam("project_name") String projectName,
            @RequestParam("pkg") String pkgName) throws IOException {

        // project
        String userId = "Allmusicapps_3Mins";
//        String hash = "1:$pandora"; // ?
//        String desc = "desc";
//        String testName = "testName11";
        Project project = projectRepository.findBy(projectName, userId);
        System.out.println(project);
        if (project == null) project = projectRepository.save(new Project(projectName, userId));

        // branch
        Branch branch = branchRepository.findBy(branchName, project.getId());
        if (branch == null) branch = branchRepository.save(new Branch(branchName, project.getId()));

        // contributor
        Contributor contributor = contributorRepository.findBy(authorEmail, authorName);
        if (contributor == null) contributor = contributorRepository.save(new Contributor(authorEmail, authorName));

        // commit
        Commit commit = commitRepository.findBy(commitHash, branch.getId());
        if (commit == null) {
            commit = commitRepository.save(new Commit(commitHash, branch, new Date(), "desc", contributor.getEmail()));
            branch.getCommits().add(commit.getId());
            branchRepository.save(branch);
//            commit.setJobStatus(Constants.COMMIT_STATUS_QUEUED);
        }

        commitRepository.save(commit);
        branchRepository.save(branch);

        // input log, output record, mark commit as done
        // testRecords todo , call post processing, get a testrecord
//        TestRecord record = testRecordRepository.findBy(testName, commit.getId());
//        if (record != null) throw new RuntimeException("testrecord is already exist" + "testName: " + testName + "commit of " + commit.getId());
//        Set<ThreadCompEnergy> threadCompEnergySet = new HashSet<>();
//        record = testRecordRepository.save(new TestRecord(testName, commit.getId(), threadCompEnergySet));
//        branch.addTest(record.getTestName());
//        commit.addTestRecord(record.getId());
//        commitRepository.save(commit);
//        branchRepository.save(branch);

//        File file = new File("/Users/boyang/Documents/work/spring-cloud-demo/testrecord/src/test/resources/eagle-2479786998251.zip");

        // todo change to another micro
        String fileName = contentDispositionHeader.getFileName();
        File file = File.createTempFile("tmp-", fileName);
        Files.copy(fileInputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Runnable runnable = dbOperation.getUploadedTraceZipHandler(file, pkgName, "devicename", commit.getId(),
                dbOperation);
        runnable.run();
        return ResponseEntity.ok().build();
    }


}
