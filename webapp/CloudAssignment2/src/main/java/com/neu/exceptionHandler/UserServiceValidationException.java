package com.neu.exceptionHandler;

public class UserServiceValidationException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public UserServiceValidationException(String message) {
		super(message);
	}

}
