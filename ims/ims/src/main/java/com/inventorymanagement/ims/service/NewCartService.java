package com.inventorymanagement.ims.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventorymanagement.ims.constants.InventoryConstants;
import com.inventorymanagement.ims.entity.CartItem;
import com.inventorymanagement.ims.entity.ProductEntity;
import com.inventorymanagement.ims.exception.CustomException;
import com.inventorymanagement.ims.exception.ProductNotFoundException;
import com.inventorymanagement.ims.repository.CartItemRepository;
import com.inventorymanagement.ims.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class NewCartService {

	
	 private static final Logger logger = LoggerFactory.getLogger(NewCartService.class);
	
	@Autowired
	private CartItemRepository cartRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
//	 private static final double DISCOUNT_THRESHOLD = 1000.0;
//	    private static final double DISCOUNT_PERCENTAGE = 10.0;
//	
//	
//	/**
//     * Updates the quantity of an item in the cart.
//     */
//    @Transactional
//    public CartItem updateCartQuantity(Long cartId, Integer quantity) {
//        logger.info("Updating quantity for cart ID: {}", cartId);
//
//        // Fetch cart item
//        CartItem cartItem = cartRepository.findById(cartId)
//                .orElseThrow(() -> new CustomException(InventoryConstants.CART_ITEM_NOT_FOUND + cartId));
//
//        // Fetch product details
//        ProductEntity product = productRepository.findById(cartItem.getProduct().getProductId())
//                .orElseThrow(() -> new CustomException(InventoryConstants.ERROR_PRODUCT_NOT_FOUND+ cartItem.getProduct().getProductId()));
//
//        // Validate stock availability
//        if (quantity > product.getProductStock()) {
//            throw new CustomException(InventoryConstants.INSUFFICIENT_STOCK + product.getProductName());
//        }
//
//        // Update cart quantity
//        cartItem.setQuantity(quantity);
//        CartItem updatedCart = cartRepository.save(cartItem);
//
//        logger.info("Cart updated successfully. New quantity: {}", quantity);
//        return updatedCart;
//    }
//    /**
//     * Checks stock availability for a given product.
//     */
//    public Integer checkStock(Long productId) {
//        logger.info("Checking stock availability for product ID: {}", productId);
//
//        try {
//           
//            ProductEntity product = productRepository.findById(productId)
//                    .orElseThrow(() -> new CustomException(InventoryConstants.ERROR_PRODUCT_NOT_FOUND + productId));
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
//
//    
//    /**
//     * Handles the checkout process: verifies stock, updates inventory, and clears the cart.
//     */
//    @Transactional
//    public String checkoutCart() {
//        logger.info("Initiating checkout process...");
//
//        try {
//            List<CartItem> cartItems = cartRepository.findAll();
//
//            if (cartItems.isEmpty()) {
//                logger.warn("Checkout failed: Cart is empty.");
//                return "Cart is empty. Please add items before checkout.";
//            }
//
//            // Validate stock and update inventory
//            for (CartItem cartItem : cartItems) {
//                ProductEntity product = productRepository.findById(cartItem.getProduct().getProductId())
//                        .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + cartItem.getProduct().getProductId()));
//
//                if (product.getProductStock() < cartItem.getQuantity()) {
//                    logger.error("Insufficient stock for product: {}", product.getProductName());
//                    throw new CustomException(InventoryConstants.INSUFFICIENT_STOCK + product.getProductName());
//                }
//
//                // Deduct stock
//                product.setProductStock(product.getProductStock() - cartItem.getQuantity());
//                productRepository.save(product);
//            }
//
//            // Clear the cart
//            cartRepository.deleteAll();
//            logger.info("Checkout successful: Stock updated, and cart cleared.");
//
//            return "Checkout successful! Inventory updated and cart cleared.";
//            
//        } catch (ProductNotFoundException e) {
//            logger.error("Product not found during checkout: {}", e.getMessage());
//            return "Error: " + e.getMessage();
//            
//        } catch (CustomException e) {
//            logger.error("Stock unavailable during checkout: {}", e.getMessage());
//            return "Error: " + e.getMessage();
//            
//        } catch (Exception e) {
//            logger.error("Unexpected error during checkout: {}", e.getMessage(), e);
//            return "Error: An unexpected error occurred during checkout.";
//        }
//    }
//
//    
//    /**
//     * Retrieves discounts for each cart item (10% discount on items over 1000).
//     */
//    public Map<String, Double> getCartDiscounts() {
//        logger.info("Fetching discounts for cart items...");
//
//        Map<String, Double> discountMap = new HashMap<>();
//
//        try {
//            List<CartItem> cartItems = cartRepository.findAll();
//
//            if (cartItems.isEmpty()) {
//                logger.warn("No items in the cart.");
//                discountMap.put("message", 0.0);
//                return discountMap;
//            }
//
//            for (CartItem cartItem : cartItems) {
//                double originalPrice = cartItem.getProduct().getProductPrice() * cartItem.getQuantity();
//                double discount = (originalPrice > DISCOUNT_THRESHOLD) ? (originalPrice * (DISCOUNT_PERCENTAGE / 100)) : 0.0;
//                double finalPrice = originalPrice - discount;
//
//                discountMap.put(cartItem.getProduct().getProductName(), finalPrice);
//            }
//
//            logger.info("Discounts successfully calculated.");
//            return discountMap;
//
//        } catch (Exception e) {
//            logger.error("Error while calculating discounts: {}", e.getMessage(), e);
//            throw new RuntimeException("An unexpected error occurred while calculating discounts.");
//        }
//    }
//    /**
//     * Retrieves the total item count in the cart.
//     */
//    public int getCartItemCount() {
//        logger.info("Fetching total item count in cart...");
//
//        try {
//            List<CartItem> cartItems = cartRepository.findAll();
//
//            if (cartItems.isEmpty()) {
//                logger.warn("Cart is empty.");
//                return 0;
//            }
//
//            int totalItemCount = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
//            logger.info("Total items in cart: {}", totalItemCount);
//
//            return totalItemCount;
//
//        } catch (Exception e) {
//            logger.error("Error while fetching cart item count: {}", e.getMessage(), e);
//            throw new RuntimeException("An unexpected error occurred while fetching cart item count.");
//        }
//    }
}
