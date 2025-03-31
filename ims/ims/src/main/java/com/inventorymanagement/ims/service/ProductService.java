package com.inventorymanagement.ims.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventorymanagement.ims.constants.InventoryConstants;
import com.inventorymanagement.ims.entity.ProductEntity;
import com.inventorymanagement.ims.exception.ProductNotFoundException;
import com.inventorymanagement.ims.exception.ProductUpdateException;
import com.inventorymanagement.ims.repository.ProductRepository;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository inventoryRepository;

    /**
     * Creates a new product in the inventory.
     */
    public ProductEntity createProduct(ProductEntity productEntity) {
        try {
            ProductEntity savedProduct = inventoryRepository.save(productEntity);
            logger.info("Product created successfully with ID: {}", savedProduct.getProductId());
            return savedProduct;
        } catch (Exception e) {
            logger.error("Error creating product: {}", e.getMessage(), e);
            throw new RuntimeException("Something went wrong while creating product data.");
        }
    }

    /**
     * Reads a product by ID
     */
    public ProductEntity readProduct(Long productId) {
        logger.info("Fetching product with ID: {}", productId);

        ProductEntity product = inventoryRepository.findByproductIdAndDeletedFalse(productId);
        if (product == null) {
            logger.error("Product not found with ID: {}", productId);
            throw new ProductNotFoundException("Product not found with ID: " + productId);
        }

        logger.info("Product retrieved successfully: {}", product.getProductName());
        return product;
    }

    /**
     * Retrieves all active 
     */
    public List<ProductEntity> getAllProducts() {
        try {
            List<ProductEntity> products = inventoryRepository.findByDeletedFalse();
            logger.info("Retrieved {} products successfully", products.size());
            return products;
        } catch (Exception e) {
            logger.error("Error retrieving product data: {}", e.getMessage(), e);
            throw new RuntimeException("Something went wrong while fetching all products.");
        }
    }

    /**
     * Soft deletes 
     */
    private static final int DELETE_AFTER_DAYS = 1; 

    @Transactional
    public ProductEntity deleteProduct(Long productId) {
        logger.info("Attempting to soft delete product with ID: {}", productId);

        ProductEntity product = inventoryRepository.findByproductIdAndDeletedFalse(productId);
        if (product == null) {
            logger.error(InventoryConstants.ERROR_PRODUCT_NOT_FOUND + ": {}", productId);
            throw new ProductNotFoundException(InventoryConstants.ERROR_PRODUCT_NOT_FOUND + ": " + productId);
        }

        product.setDeleted(true);
        product.setDeletedAt(LocalDateTime.now()); 
        ProductEntity updatedProduct = inventoryRepository.save(product);
        logger.info("Product successfully soft deleted with ID: {}", productId);
        
        return updatedProduct;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void hardDeleteOldProducts() {
        logger.info("Checking for products eligible for hard deletion...");

        LocalDateTime deleteThreshold = LocalDateTime.now().minusDays(DELETE_AFTER_DAYS);
        List<ProductEntity> productsToDelete = inventoryRepository.findByDeletedTrueAndDeletedAtBefore(deleteThreshold);

        if (!productsToDelete.isEmpty()) {
            inventoryRepository.deleteAll(productsToDelete);
            logger.info("Deleted {} products permanently", productsToDelete.size());
        } else {
            logger.info("No products found for permanent deletion.");
        }
    }
    /**
     * Updates product 
     */
    @Transactional
    public ProductEntity updateProduct(Long productId, ProductEntity updatedProduct) {
        try {
            ProductEntity existingProduct = inventoryRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

            // Only update fields that are not null
            if (updatedProduct.getProductName() != null) {
                existingProduct.setProductName(updatedProduct.getProductName());
            }
            if (updatedProduct.getProductPrice() != null) {
                existingProduct.setProductPrice(updatedProduct.getProductPrice());
            }
            if (updatedProduct.getProductStock() != null) {
                existingProduct.setProductStock(updatedProduct.getProductStock());
            }
            if (updatedProduct.getProductCategory() != null) {
                existingProduct.setProductCategory(updatedProduct.getProductCategory());
            }

            ProductEntity savedProduct = inventoryRepository.save(existingProduct);
            logger.info("Product updated successfully with ID: {}", productId);
            return savedProduct;
        } catch (ProductNotFoundException e) {
            logger.error("Product not found: {}", productId);
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error while updating product ID {}: {}", productId, e.getMessage(), e);
            throw new ProductUpdateException("Database error occurred while updating product with ID: " + productId);
        } catch (Exception e) {
            logger.error("Unexpected error while updating product ID {}: {}", productId, e.getMessage(), e);
            throw new ProductUpdateException("An unexpected error occurred while updating product with ID: " + productId);
        }
    }
    
    
//    /**
//     * Checks stock availability for a given product.
//     */
//    public Integer checkStock(Long productId) {
//        logger.info("Checking stock availability for product ID: {}", productId);
//
//        try {
//            // Fetch product by ID
//            ProductEntity product = inventoryRepository.findById(productId)
//                    .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
//
//            logger.info("Product {} has {} items in stock", product.getProductName(), product.getProductStock());
//            return product.getProductStock();
//        } catch (ProductNotFoundException e) {
//            logger.error("Error: {}", e.getMessage());
//            throw e;
//        } catch (Exception e) {
//            logger.error("Unexpected error while checking stock for product ID {}: {}", productId, e.getMessage(), e);
//            throw new RuntimeException("An unexpected error occurred while checking stock.");
//        }
//    }
}
