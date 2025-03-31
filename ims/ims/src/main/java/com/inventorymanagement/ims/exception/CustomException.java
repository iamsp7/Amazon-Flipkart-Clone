package com.inventorymanagement.ims.exception;

public class CustomException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
  
	
	public CustomException(String Message) {
		
		super(Message);
	}
}
