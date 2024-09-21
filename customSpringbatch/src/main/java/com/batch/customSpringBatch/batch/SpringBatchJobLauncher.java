package com.batch.customSpringBatch.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
@Configuration
public class SpringBatchJobLauncher implements CommandLineRunner {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job processFileJob;


    @Override
    public void run(String... args) throws Exception {
        JobExecution execution = jobLauncher.run(processFileJob, new JobParametersBuilder()
                .addString("run.id", String.valueOf(System.currentTimeMillis())).toJobParameters());
        System.out.println("Job Execution Status: " + execution.getStatus());

    }
}
