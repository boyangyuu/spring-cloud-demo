package com.mobileenerlytics.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;

public class RunEprof implements Runnable {
    private final String traceDir;
    private static final Logger logger = LoggerFactory.getLogger("RUNEPROF");
    private boolean success = false;

    public RunEprof(String traceDir) {
        this.traceDir = traceDir;
    }

    public boolean isSuccess() {
        return success;
    }

    public void run() {
        String javaExecutable = new File(System.getProperty("java.home"), "bin/java").getAbsolutePath();
        URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        URL[] urls = classLoader.getURLs();

        StringBuilder cpBuilder = new StringBuilder();
        for (URL url : urls) {
            cpBuilder.append(url.getPath());
            cpBuilder.append(":");
        }
        cpBuilder.deleteCharAt(cpBuilder.lastIndexOf(":"));
        String classPath = cpBuilder.toString();

        String extDirs = "-Djava.ext.dirs=" + System.getProperty("java.ext.dirs");

        String[] cmdArray = new String[]{javaExecutable, "-classpath", classPath, extDirs,
                "com.mobileenerlytics.eprof.stream.Main", traceDir};

        // For debugging only
        StringBuilder cmdBuilder = new StringBuilder();
        for(String cmd : cmdArray) {
            cmdBuilder.append(cmd);
            cmdBuilder.append(' ');
        }
        logger.debug("Running {}", cmdBuilder.toString());

        ProcessBuilder processBuilder = new ProcessBuilder(cmdArray);
        processBuilder.redirectErrorStream(true);
        try {
            Process pr = processBuilder.start();
            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line = null;
            while ((line = buf.readLine()) != null) {
                logger.debug(line);
            }
            pr.waitFor();
            success = true;
        } catch (Exception e) {
            logger.error("Failed to process trace. Detailed logs in " + traceDir
                    + File.separator + "run.out");
            e.printStackTrace();
        }
    }

}
