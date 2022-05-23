package com.javatechie.spring.batch.listeners;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Value;

import com.javatechie.spring.batch.validators.FileValidators;

public class JobListener implements JobExecutionListener {

	Logger logger = LoggerFactory.getLogger(JobListener.class);

	@Value("${FilePath}")
	private String filePath;

	public static JobExecution jobExecution;

	public void beforeJob(JobExecution jobExecution) {

		JobListener.jobExecution = jobExecution;
		logger.info("BEFORE BATCH JOB STARTS");
		boolean lisFileExist = false, lisFileValid = false;
		try {
			File sourceFolder = new File(filePath);
			String fileExt = "csv";
			System.out.println("-----total files in the specified path---->" + sourceFolder.listFiles().length);
			if (sourceFolder.listFiles().length > 0) {
				for (File sourceFile : sourceFolder.listFiles()) {
					String fileName = sourceFile.getName();
					System.out.println("----Reading file with name-->" + fileName);
					if (fileName.endsWith(fileExt)) {
						lisFileExist = FileValidators.isFileExist();
						if (lisFileExist)
							lisFileValid = FileValidators.isFileValid(fileName.substring(0, fileName.lastIndexOf(".")),
									fileName);

						if (lisFileExist && lisFileValid)
							System.out.println("Good to go");
						else
							System.out.println("Failed");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			logger.info("BATCH JOB COMPLETED SUCCESSFULLY");
		} else if (jobExecution.getStatus() == BatchStatus.FAILED) {
			logger.info("BATCH JOB FAILED");
		}
	}

}
