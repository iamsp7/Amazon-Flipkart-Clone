package com.inventorymanagement.ims.controller;

import org.slf4j.Logger;    
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.inventorymanagement.ims.constants.InventoryConstants.*;

import java.util.List;

import com.inventorymanagement.ims.entity.ProductEntity;
import com.inventorymanagement.ims.exception.ProductNotFoundException;
import com.inventorymanagement.ims.service.ProductService;

@RestController
@RequestMapping("/api")
public class ProductController {

	
	 private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
	
	@Autowired
	private ProductService inventoryService;
	
	@PostMapping("/product/addproduct")
	public ResponseEntity<String> createProduct(@RequestBody ProductEntity productEntity){
		
		try {
			
			ProductEntity createdProduct = inventoryService.createProduct(productEntity);
			logger.info("Product Has been created with userName : {}  ", createdProduct.getProductName() + " with ID : {} " , createdProduct.getProductId());
			return ResponseEntity.ok(PRODUCT_CREATED_SUCCESS);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR CREATING PRODUCT DATA"  + e.getMessage());
		}
}
	
	@PutMapping("/product/updateproduct/{productId}")
	public ResponseEntity<String> updateProduct(@PathVariable Long productId, @RequestBody ProductEntity productEntity) {
	    try {
	        ProductEntity updatedProduct = inventoryService.updateProduct(productId, productEntity);
	        
	        logger.info("Product has been updated successfully with ProductName: {} and ID: {}", 
	                    updatedProduct.getProductName(), updatedProduct.getProductId());

	        return ResponseEntity.ok(PRODUCT_UPDATED_SUCCESS);
	        
	    } catch (ProductNotFoundException e) {
	        logger.error("Product not found with ID: {}", productId, e);
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found with ID: " + productId);
	        
	    } catch (Exception e) {
	        logger.error("An unexpected error occurred while updating product with ID: {}", productId, e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating the product.");
	    }
	}

	@GetMapping("/product/getallproduct")
	public ResponseEntity<?> getAllProducts(){
		try {
			List<ProductEntity> getProduct = inventoryService.getAllProducts();
			logger.info("product Retrived Successfully" );
			return ResponseEntity.ok(getProduct);
		}catch(Exception e) {
			logger.error("Unable to get all Products");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while getting all products");
		}
		
		
	}
	//, @RequestBody ProductEntity productEntity
	@GetMapping("/product/get/{productId}")
	public ResponseEntity<?> readProduct(@PathVariable Long productId ){
		try {
			ProductEntity getProduct = inventoryService.readProduct(productId);
			logger.info("Product Retrived" , getProduct.getProductName());
			return ResponseEntity.ok(getProduct);
		}catch(ProductNotFoundException e) {
			logger.error("Product not found with this ID : {} ",productId ,e );
			return ResponseEntity.ok(ERROR_PRODUCT_NOT_FOUND);
		}catch(Exception e) {
			logger.error("something went wrong ");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(" ERROR " + e.getMessage());
		}
		
	}
	 @DeleteMapping("/delete/{productId}")
	    public ResponseEntity<String> hardDeleteProduct(@PathVariable Long productId) {
	        try {
	            inventoryService.hardDeleteOldProducts(); // Fixed method call
	            logger.info("Product permanently deleted with ID: {}", productId);
	            return ResponseEntity.ok(PRODUCT_DELETE_SUCCESS);

	        } catch (ProductNotFoundException e) {
	            logger.error("Product not found with ID: {}", productId, e);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ERROR_PRODUCT_NOT_FOUND);

	        } catch (Exception e) {
	            logger.error("Unexpected error while deleting product ID {}: {}", productId, e.getMessage(), e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Error occurred: " + e.getMessage());
	        }
	    }

	
	@DeleteMapping("/product/softdelete/{productId}")
	public ResponseEntity<String> softDeleteProduct(@PathVariable Long productId ){
		try {
			  inventoryService.deleteProduct(productId);
			  logger.info("Product Deleted with ID : {} " , productId);
			  return ResponseEntity.ok(PRODUCT_DELETE_SUCCESS);
		}catch(ProductNotFoundException e) {
			logger.error("Product Not found with ID : {}" , productId , e);
			return ResponseEntity.ok(ERROR_PRODUCT_NOT_FOUND);
		}catch(Exception e) {
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR " + e.getMessage());
		
	}
}
}
