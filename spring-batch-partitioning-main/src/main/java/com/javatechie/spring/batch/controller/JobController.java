package com.javatechie.spring.batch.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class JobController {

	@Autowired
	JobLauncher jobLauncher; 

	@Autowired
	Job runjob; 
	
	@GetMapping
	public BatchStatus load() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
			JobRestartException, JobInstanceAlreadyCompleteException, Exception {

		Map<String, JobParameter> maps = new HashMap<>();
		maps.put("time", new JobParameter(System.currentTimeMillis()));
		JobParameters parameters = new JobParameters(maps);
		JobExecution jobExecution = jobLauncher.run(runjob, parameters);

		System.out.println("JobExecution: " + jobExecution.getStatus());

		System.out.println("Batch is Running...");
		while (jobExecution.isRunning()) {
			System.out.println("...");
		}
		System.out.println("Batch process completed...");
		
		System.out.println("JobExecution: " + jobExecution.getStatus());
		StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
		System.out.println("ReadCount = " + stepExecution.getReadCount());
		System.out.println("ReadSkipCount = " + stepExecution.getReadSkipCount());
		System.out.println("WriteCount = " + stepExecution.getWriteCount());
		System.out.println("WriteSkipCount = " + stepExecution.getWriteSkipCount());
		System.out.println("ProcessSkipCount = " + stepExecution.getProcessSkipCount());
		System.out.println("CommitCount = " + stepExecution.getCommitCount());
		System.out.println("RollbackCount = " + stepExecution.getRollbackCount());
		System.out.println("FailureExceptions = " + stepExecution.getFailureExceptions());
		System.out.println("JobStartTime: " + jobExecution.getStartTime()+"---end time---"+jobExecution.getEndTime()+"---diff--"+(jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime()));
		System.out.println("Batch process completed...");
		
		return jobExecution.getStatus();
	}
}
