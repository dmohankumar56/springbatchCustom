package com.batch.customSpringBatch.batch;

import com.batch.customSpringBatch.dto.IncomingFileDto;
import com.batch.customSpringBatch.dto.OutgoingFileDto;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final CustomDBWriter customDBWriter;
    private final CustomProcessor customProcessor;
    private final CustomFileWriter customFileWriter;
    private final EntityManagerFactory entityManagerFactory;
    private final DetailsFileReader detailsFileReader;
    @Autowired
    public BatchConfig(
            CustomDBWriter customDBWriter,
            CustomProcessor customProcessor,
            CustomFileWriter customFileWriter,
             EntityManagerFactory entityManagerFactory, DetailsFileReader detailsFileReader) {
        this.customDBWriter = customDBWriter;
        this.customProcessor = customProcessor;
        this.customFileWriter = customFileWriter;
        this.entityManagerFactory = entityManagerFactory;
        this.detailsFileReader = detailsFileReader;
    }

    @Bean
    public Step processFileStep(JobRepository jobRepository, PlatformTransactionManager  transactionManager) throws IOException {
        return new StepBuilder("processFileStep", jobRepository)
                .<IncomingFileDto, OutgoingFileDto>chunk(1, transactionManager)
                .reader(detailsFileReader)
                .processor(customProcessor)
                .writer(compositeItemWriter(customFileWriter, customDBWriter))
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        // Retrieve the job parameters
                        String filePath = stepExecution.getJobParameters().getString("fileName");
                        try {
                            detailsFileReader.setFilePath(filePath); // Set the file path for the reader
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to set file path: " + e.getMessage());
                        }
                    }
                })
                .allowStartIfComplete(true)
                .transactionManager(transactionManager)
                .build();
    }


    @Bean
    public CompositeItemWriter<OutgoingFileDto> compositeItemWriter(
            CustomFileWriter customFileWriter,
            CustomDBWriter customDBWriter) {

        CompositeItemWriter<OutgoingFileDto> compositeItemWriter = new CompositeItemWriter<>();
        List<ItemWriter<? super OutgoingFileDto>> writers = new ArrayList<>();

        writers.add(customDBWriter);  // First write to DB
        writers.add(customFileWriter);  // Then write to File

        compositeItemWriter.setDelegates(writers);
        return compositeItemWriter;
    }

    @Bean
    public Job processFileJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                              Step headerStep ,
                              Step processFileStep
    ) throws IOException {
        Path errorFolderPath = Paths.get("C:\\Users\\mohan\\OneDrive\\Documents\\walbatch\\badfile\\");
        return new JobBuilder("processFileJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(processFileStep)
                .listener(new FileMoveJobExecutionListener(errorFolderPath))
                .build();
    }

    @Bean
    public JobRepository jobRepository(DataSource dataSource) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager());
        factory.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
        factory.afterPropertiesSet();
        return factory.getObject();
    }


    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory);
    }

   /* @Bean
    public JobLauncher getJobLauncher(DataSource dataSource) throws Exception {
        TaskExecutorJobLauncher taskExecutorJobLauncher = new TaskExecutorJobLauncher();
        taskExecutorJobLauncher.setJobRepository(jobRepository(dataSource));
        taskExecutorJobLauncher.afterPropertiesSet();
        return taskExecutorJobLauncher;
    }*/


}

