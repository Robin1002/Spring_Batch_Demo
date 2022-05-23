package com.javatechie.spring.batch.exceptions;

public class FileNotFoundException extends Exception{
	private static final long serialVersionUID = 1L;
	
	public FileNotFoundException() {
		super();
	}

	public FileNotFoundException(final String message) {
		super(message);
	}

}
