package com.javatechie.spring.batch.config;

import com.javatechie.spring.batch.entity.Customer;
import com.javatechie.spring.batch.listeners.JobListener;
import com.javatechie.spring.batch.listeners.SpringBatchStepListener;
import com.javatechie.spring.batch.partition.ColumnRangePartitioner;
import com.javatechie.spring.batch.validators.FileValidators;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

	@Autowired
	JobBuilderFactory jobBuilderFactory;

	@Autowired
	StepBuilderFactory stepBuilderFactory;

	@Autowired
	CustomerWriter customerWriter;
	
	@Value("${dataFilePath}")
	 private String dataFilePath;

	@Bean
	public FlatFileItemReader<Customer> reader() {
		FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
		itemReader.setResource(new FileSystemResource(dataFilePath));
		itemReader.setName("csvReader");
		itemReader.setLinesToSkip(1);
		itemReader.setLineMapper(lineMapper());
		return itemReader;
	}

	private LineMapper<Customer> lineMapper() {
		DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("issuerMemberId", "oldPan", "oldExpirationDate", "newPan", "newExpirationDate",
				"reasonCode", "effectiveDate");

		BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Customer.class);

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		return lineMapper;

	}

	@Bean
	public CustomerProcessor processor() {
		return new CustomerProcessor();
	}

	@Bean
	public ColumnRangePartitioner partitioner() {
		return new ColumnRangePartitioner();
	}

	@Bean
	public PartitionHandler partitionHandler() {
		TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
		taskExecutorPartitionHandler.setGridSize(4);
		taskExecutorPartitionHandler.setStep(slaveStep());
		return taskExecutorPartitionHandler;
	}

//	@Bean
//	public Step slaveStep() {
//		return stepBuilderFactory.get("slaveStep").listener(new SpringBatchStepListener()).<Customer, Customer>chunk(50)
//				.reader(reader()).processor(processor()).writer(customerWriter).faultTolerant().skipPolicy(skipPolicy())
//				.build();
//	}
	
	
	@Bean
	public Step slaveStep() {
		return stepBuilderFactory.get("slaveStep").listener(new SpringBatchStepListener()).<Customer, Customer>chunk(50)
				.reader(reader()).processor(processor()).writer(customerWriter).startLimit(1)
				.build();
	}

	@Bean
	public Step masterStep() {
		return stepBuilderFactory.get("masterSTep").partitioner(slaveStep().getName(), partitioner())
				.partitionHandler(partitionHandler()).build();
	}

	@Bean
	public Job runJob() {
		return jobBuilderFactory.get("importCustomers").listener(jobListener()).flow(masterStep()).end().build();

	}

	@Bean
	public JobSkipPolicy skipPolicy() {
		return new JobSkipPolicy();
	}

	@Bean
	public FileValidators fileValidators() {
		return new FileValidators();
	}

	@Bean
    public JobListener jobListener() {
		return new JobListener();
    }
}
