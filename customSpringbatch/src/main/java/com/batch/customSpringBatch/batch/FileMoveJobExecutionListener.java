package com.batch.customSpringBatch.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.item.ExecutionContext;

import java.nio.file.*;

public class FileMoveJobExecutionListener implements JobExecutionListener {

    private final Path errorFolderPath;

    public FileMoveJobExecutionListener(Path errorFolderPath) {
        this.errorFolderPath = errorFolderPath;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        // No action needed before the job starts
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            // Get the file path from the Job ExecutionContext
            ExecutionContext jobContext = jobExecution.getExecutionContext();
            String sourceFilePath = "C:\\Users\\mohan\\OneDrive\\Documents\\walbatch\\inbound\\incoming1.txt";

            if (sourceFilePath != null) {
                try {
                    // Move the file to the error folder
                    Path sourcePath = Paths.get(sourceFilePath);
                    Path errorFilePath = errorFolderPath.resolve(sourcePath.getFileName());
                    Files.move(sourcePath, errorFilePath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("File moved to error folder: " + errorFilePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("sourceFilePath is null in the JobExecutionContext.");
            }
        }
    }
}

