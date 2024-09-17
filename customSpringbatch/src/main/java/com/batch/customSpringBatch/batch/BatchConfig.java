package com.batch.customSpringBatch.batch;

import com.batch.customSpringBatch.dto.IncomingDetailDto;
import com.batch.customSpringBatch.dto.OutgoingFileDto;
import com.batch.customSpringBatch.model.ControlLoadFile;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final CustomDBWriter customDBWriter;
    private final CustomProcessor customProcessor;
    private final CustomFileWriter customFileWriter;
    private final HeaderDBWriter headerDBWriter;

    @Autowired
    public BatchConfig(
            CustomDBWriter customDBWriter,
            CustomProcessor customProcessor,
            CustomFileWriter customFileWriter,HeaderDBWriter headerDBWriter) {
        this.customDBWriter = customDBWriter;
        this.customProcessor = customProcessor;
        this.customFileWriter = customFileWriter;
        this.headerDBWriter = headerDBWriter;
    }

    @Bean
    public ItemReader<IncomingDetailDto> detailReader() throws IOException {
        return new DetailsFileReader("C:\\Users\\mohan\\OneDrive\\Documents\\walbatch\\inbound\\incoming.txt");

    }

    @Bean
    public Step processFileStep(JobRepository jobRepository, PlatformTransactionManager  transactionManager) throws IOException {
        return new StepBuilder("processFileStep", jobRepository)
                .<IncomingDetailDto, OutgoingFileDto>chunk(999, transactionManager)
                .reader(detailReader())
                .processor(customProcessor)
                .writer(compositeItemWriter(customFileWriter, customDBWriter))
                .transactionManager(transactionManager)
                .build();
    }



    @Bean
    public FlatFileItemReader<ControlLoadFile> headerFileReader() throws IOException {
        return new FlatFileItemReaderBuilder<ControlLoadFile>()
                .name("headerFileReader")
                .resource(new FileSystemResource("C:\\Users\\mohan\\OneDrive\\Documents\\walbatch\\inbound\\incoming.txt"))
                .lineMapper(new LineMapper<ControlLoadFile>() {
                    @Override
                    public ControlLoadFile mapLine(String line, int lineNumber) throws Exception {
                        // Check if the line starts with 'H' (for header)
                        if (line.startsWith("H")) {
                            // Parse the dateTime and header data
                            LocalDateTime dateTime = getLocalDateTime(line);
                            return ControlLoadFile.builder()
                                    .fileName(line.substring(1, 21).trim())         // File name is 20 characters
                                    .fileUniqueId(line.substring(21, 57).trim())    // Unique ID is 36 characters
                                    .dateTime(dateTime)                             // Parsed dateTime
                                    .build();
                        } else {
                            // Return null if the line is not a header (Spring Batch will skip nulls)
                            return null;
                        }
                    }
                })
                .build();
    }


    private LocalDateTime getLocalDateTime(String line) {
        String dateTimeStr = line.substring(57, 73).trim(); // 2024061200:00:00
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH:mm:ss");
        return LocalDateTime.parse(dateTimeStr, formatter);
    }


    @Bean
    public Step headerStep(JobRepository jobRepository, PlatformTransactionManager  transactionManager) throws IOException {
        return new StepBuilder("headerStep", jobRepository)
                .<ControlLoadFile, ControlLoadFile>chunk(1,transactionManager)
                .reader(headerFileReader())
                .writer(headerDBWriter)
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
        return new JobBuilder("processFileJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(headerStep)
                .next(processFileStep)
                .build();
    }

    @Bean
    public JobRepository jobRepository(DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.setIsolationLevelForCreate("ISOLATION_DEFAULT");
        factory.afterPropertiesSet();
        return factory.getObject();
    }


    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

