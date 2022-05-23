package com.javatechie.spring.batch.validators;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;

import com.javatechie.spring.batch.exceptions.FileNotFoundException;

public class FileValidators {

	@Value("${dataFilePath}")
	private static String dataFilePath;

	public static boolean isFileExist() throws FileNotFoundException {

		boolean lIsFileExist = true;
		try {
			File file = new File(dataFilePath);
			String fileName = file.getName();

			if (!file.exists()) {
				lIsFileExist = false;
			} else {
				System.out.println("-------Files exists-------");
				lIsFileExist = isFileValid(fileName.substring(0, fileName.lastIndexOf(".")), fileName);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return lIsFileExist;
	}

	public static boolean isFileValid(String fileName, String fileNameWithExt) {
		System.out.println("Filename: " + fileName + " its length is: " + fileName.length());
		boolean lIsFileNameValid = true;

		String fileRegex = "^rtau_pulse_\\d{12}.csv$";
		Pattern ps = Pattern.compile(fileRegex, Pattern.CASE_INSENSITIVE);
		Matcher ms = ps.matcher(fileNameWithExt);

		System.out.println("---ms.matches()----" + ms.matches());
		if (fileName.length() != 23 || !ms.matches()) {
			lIsFileNameValid = false;
			if (fileName.length() != 23)
				System.out.println("Invalid File Length");
			else
				System.out.println("Invalid File Pattern");
		} else {
			System.out.println("File name is fine");
		}
		return lIsFileNameValid;

	}
}
