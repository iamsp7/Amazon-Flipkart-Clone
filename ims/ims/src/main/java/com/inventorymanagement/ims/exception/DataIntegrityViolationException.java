package com.inventorymanagement.ims.exception;



public class DataIntegrityViolationException  extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DataIntegrityViolationException(String Message) {
		super(Message);
	}

}
