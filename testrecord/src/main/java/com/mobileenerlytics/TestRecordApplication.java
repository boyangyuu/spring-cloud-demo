package com.mobileenerlytics;


import com.mobileenerlytics.entity.Branch;
import com.mobileenerlytics.entity.Commit;
import com.mobileenerlytics.entity.Contributor;
import com.mobileenerlytics.entity.Project;
import com.mobileenerlytics.repository.*;
import com.mobileenerlytics.util.DBOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.io.File;
import java.util.Date;

@EnableEurekaClient
@SpringBootApplication
public class TestRecordApplication implements CommandLineRunner {

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


    public static void main(String[] args) {
        SpringApplication.run(TestRecordApplication.class, args);
        System.out.println("testrecord microservice is running");
    }

    @Override
    public void run(String... strings) throws Exception {
        // https://spring.io/guides/tutorials/bookmarks/
        String projectName = "default";
        String userId = "Allmusicapps_3Mins";
        String branchName = "origin/master";

        String authorEmail = "boyang@gmail.com";
        String authorName = "Boyang";

        String hash = "1:$pandora";
        String desc = "desc";

        String testName = "testName11";
        branchRepository.deleteAll();
        testRecordRepository.deleteAll();
        commitRepository.deleteAll();
        projectRepository.deleteAll();

        // project
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
        Commit commit = commitRepository.findBy(hash, branch.getId());
        if (commit == null) {
            commit = commitRepository.save(new Commit(hash, branch, new Date(), desc, contributor.getEmail()));
            branch.getCommits().add(commit.getId());
            branchRepository.save(branch);
//            commit.setJobStatus(Constants.COMMIT_STATUS_QUEUED);
        }
        commitRepository.save(commit);
        branchRepository.save(branch);

        // testrecord

        // input log, output record, mark commit as done
        // testRecords todo , call post processing, get a testrecord
//        TestRecord record = testRecordRepository.findBy(testName, commit.getId());
//        if (record != null) throw new RuntimeException("testrecord is already exist" + "testName: " + testName + "commit of " + commit.getId());
//        Set<ThreadCompEnergy> threadCompEnergySet = new HashSet<>();
//        record = testRecordRepository.save(new TestRecord(testName, commit.getId(), threadCompEnergySet));
//        branch.addTest(record.getTestName());
//        commit.addTestRecord(record.getId());


        // processing
//        if (1 == 1) return;
        File file = new File("/Users/boyang/Documents/work/spring-cloud-demo/testrecord/src/test/resources/eagle-2479786998251.zip");
        String pkgName = "com.tencent.qqmusic";
        Runnable runnable = dbOperation.getUploadedTraceZipHandler(file, pkgName, "devicename", commit.getId(),
                dbOperation);
        runnable.run();
        // todo is it ok?

        // todo call email alerting


        //todo test with the old data, crud
        // todo add unit test https://spring.io/guides/gs/testing-web/
    }
}
