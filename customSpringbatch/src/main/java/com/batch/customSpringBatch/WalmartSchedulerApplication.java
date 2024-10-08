package com.batch.customSpringBatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class WalmartSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalmartSchedulerApplication.class, args);
	}

	/*@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job job;

	@PostConstruct
	public void runJob() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("paramName", "paramValue")
				.toJobParameters();
		jobLauncher.run(job, jobParameters);
	}*/

}
