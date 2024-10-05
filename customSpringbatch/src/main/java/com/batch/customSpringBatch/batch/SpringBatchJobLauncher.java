package com.batch.customSpringBatch.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SpringBatchJobLauncher implements CommandLineRunner {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job processFileJob;


    @Override
    public void run(String... args) throws Exception {
        // Specify your directory path here or take it from args
        String directoryPath = "C:\\Users\\mohan\\OneDrive\\Documents\\walbatch\\inbound\\"; // Modify as needed
        launchJobsForFiles(directoryPath);


    }

    public void launchJobsForFiles(String directoryPath) {
        List<String> fileNames = getFilesFromDirectory(directoryPath);
        for (String fileName : fileNames) {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fileName", fileName)
                    .addLong("time", System.currentTimeMillis()) // To ensure unique job instances
                    .toJobParameters();

            try {
               JobExecution execution = jobLauncher.run(processFileJob, jobParameters);
                System.out.println("Job Execution Status: " + execution.getStatus());
            } catch (Exception e) {
                System.out.println("Job failed for file: " + fileName + " due to: " + e.getMessage());
                // Log the error or handle accordingly
            }
        }
    }

    public List<String> getFilesFromDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt")); // Adjust extension as needed
        return Arrays.stream(files).map(File::getAbsolutePath).collect(Collectors.toList());
    }

}
