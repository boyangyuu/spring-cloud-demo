package com.mobileenerlytics.util;

public class Constants {
    // commit
    public static final String COMMIT_STATUS_QUEUED = "Queued";
    public static final String COMMIT_STATUS_PROCESSING = "Processing";
    public static final String COMMIT_STATUS_DONE = "Done";

    // user
    public final static String USER_ROLE_MANUAL = "manual";
    public final static String USER_ROLE_DEFAULT = "default";
    public final static String USER_ROLE_DEMO = "demo";

    // uploading
    public final static String BRANCH_MANUAL = "manual-test-branch";

    public static final int ALERT_MINIMUM_COMMITS_NUMBER = 20; // nums of commits to check
    public static final int ALERT_END_COMMITS_INDEX = 10; //assume previous 20("ALERT_MINIMUM_COMMITS_NUMBER") commits, if the coming commit's updateMS is before 4th/20 "ALERT_END_COMMITS_INDEX", then this commit will not invoke alert.
    public static final int ALERT_MINIMUM_TESTS_NUMBER = 10; // for specific test, the minimum common test's amount should in these commits.
    public static final double ALRRT_OFFSET_PERCENTAGE = 0.10;// after comparison, if difference is over 10%(ALRRT_OFFSET_PERCENTAGE), then should alert.


}


