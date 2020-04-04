package com.neu.exceptionHandler;

import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UserServiceException extends Exception{

	public UserServiceException(String message) {
		super(message);
	}
}
