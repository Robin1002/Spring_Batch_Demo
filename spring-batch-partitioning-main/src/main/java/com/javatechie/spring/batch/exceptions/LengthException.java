package com.javatechie.spring.batch.exceptions;

import lombok.Data;

@Data
public class LengthException extends Exception{

	private static final long serialVersionUID = 1L;
	private String message;
	public LengthException(String message) {
		super();
		this.message = message;
	}
}
