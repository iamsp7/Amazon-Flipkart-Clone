package com.inventorymanagement.ims.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

	
	
	 private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	    @ExceptionHandler(ProductNotFoundException.class)
	    public ResponseEntity<String> handleProductNotFoundException(ProductNotFoundException ex) {
	        logger.error("ProductNotFoundException: " + ex.getMessage(), ex);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
	    }

	    @ExceptionHandler(ProductUpdateException.class)
	    public ResponseEntity<String> handleProductUpdateException(ProductUpdateException ex) {
	        logger.error("ProductUpdateException: " + ex.getMessage(), ex);
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	    }
	    @ExceptionHandler(CustomException.class)
	    public ResponseEntity<String> handleCustomException(CustomException ex) {
	        logger.error("CustomException: " + ex.getMessage(), ex);
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	    }
	    @ExceptionHandler(DataIntegrityViolationException.class)
	    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
	        logger.error("DataIntegrityViolationException: " + ex.getMessage(), ex);
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	    }
}
