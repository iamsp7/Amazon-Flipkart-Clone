package com.inventorymanagement.ims.service;

import com.inventorymanagement.ims.entity.CartItem;  
import com.inventorymanagement.ims.entity.ProductEntity;
import com.inventorymanagement.ims.exception.CustomException;
import com.inventorymanagement.ims.exception.ProductNotFoundException;
import com.inventorymanagement.ims.repository.CartItemRepository;
import com.inventorymanagement.ims.repository.ProductRepository;

import jakarta.transaction.Transactional;

import com.inventorymanagement.ims.constants.InventoryConstants;
import com.inventorymanagement.ims.dto.CartItemDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductService productService; 


    @Transactional
    public void addToCart(CartItemDto cartItemDTO) {
        try {
            // Fetch existing product from the database
            ProductEntity product = productRepository.findById(cartItemDTO.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(InventoryConstants.PRODUCT_NOT_FOUND));

            // Check stock availability
            if (product.getProductStock() < cartItemDTO.getQuantity()) {
                throw new CustomException(InventoryConstants.INSUFFICIENT_STOCK);
            }

            // Check if product is already in cart
            Optional<CartItem> existingCartItem = cartItemRepository.findByProduct(product);
            if (existingCartItem.isPresent()) {
                CartItem cartItem = existingCartItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + cartItemDTO.getQuantity());
                cartItemRepository.save(cartItem);
                logger.info(InventoryConstants.PRODUCT_ALREADY_IN_CART);
            } else {
                CartItem newCartItem = new CartItem(product, cartItemDTO.getQuantity());
                cartItemRepository.save(newCartItem);
                logger.info(InventoryConstants.PRODUCT_ADDED_TO_CART);
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            logger.error(InventoryConstants.ERROR_WHILE_ADDING_TO_CART + ": {}", e.getMessage());
            throw new CustomException(InventoryConstants	.ERROR_WHILE_ADDING_TO_CART);
        }
    }





	    public List<CartItem> getAllCartItems() {
	        return cartItemRepository.findAll();
	    }
	
	    public Double getTotalCartCost() {
	        return cartItemRepository.findAll().stream()
	                .filter(item -> item.getProduct() != null)
	                .mapToDouble(item -> item.getProduct().getProductPrice() * item.getQuantity())
	                .sum();
	    }
	
	    @Transactional
	    public void purchaseCart() {
	        List<CartItem> cartItems = cartItemRepository.findAll();
	        
	        for (CartItem item : cartItems) {
	            ProductEntity product = productRepository.findByproductIdAndDeletedFalse(item.getProduct().getProductId());

	            if (product == null) {  
	                throw new CustomException(InventoryConstants.PRODUCT_NOT_FOUND);
	            }

	            int newStock = product.getProductStock() - item.getQuantity();

	            if (newStock < 0) {
	                throw new CustomException(InventoryConstants.INSUFFICIENT_STOCK);
	            }

	            product.setProductStock(newStock);
	            productRepository.save(product);
	            cartItemRepository.delete(item);
	        }
	    }



	    public void removeFromCart(CartItemDto cartItemDto) {
	        try {
	            Long productId = cartItemDto.getProductId();
	            int quantity = cartItemDto.getQuantity();

	            logger.info("Removing product (ID: {}) from cart, quantity: {}", productId, quantity);

	            ProductEntity product = productRepository.findById(productId)
	                    .orElseThrow(() -> new ProductNotFoundException(InventoryConstants.ERROR_PRODUCT_NOT_FOUND));

	            CartItem cartItem = cartItemRepository.findByProduct(product)
	                    .orElseThrow(() -> new CustomException(InventoryConstants.CART_ITEM_NOT_FOUND));

	            if (cartItem.getQuantity() > quantity) {
	                cartItem.setQuantity(cartItem.getQuantity() - quantity);
	                cartItemRepository.save(cartItem);
	            } else {
	                cartItemRepository.delete(cartItem);
	            }

	            logger.info(InventoryConstants.PRODUCT_REMOVED_FROM_CART);
	        } catch (ProductNotFoundException | CustomException e) {
	            logger.error("{} {}", InventoryConstants.ERROR, e.getMessage());
	            throw e;
	        } catch (Exception e) {
	            logger.error(InventoryConstants.UNEXPECTED_ERROR, e);
	            throw new RuntimeException(InventoryConstants.ERROR_WHILE_REMOVING_FROM_CART);
	        }
	    }
	    
	    
	    private static final double DISCOUNT_THRESHOLD = 1000.0;
	    private static final double DISCOUNT_PERCENTAGE = 10.0;
	
	
	    @Transactional
	    public CartItem updateCartQuantity(Long cartId, CartItemDto cartItemDto) {
	        logger.info("Updating quantity for cart ID: {}", cartId);

	        CartItem cartItem = cartItemRepository.findById(cartId)
	                .orElseThrow(() -> new CustomException(InventoryConstants.CART_ITEM_NOT_FOUND + cartId));

	        ProductEntity product = productRepository.findById(cartItemDto.getProductId())
	                .orElseThrow(() -> new CustomException(InventoryConstants.ERROR_PRODUCT_NOT_FOUND + cartItemDto.getProductId()));

	        if (cartItemDto.getQuantity() > product.getProductStock()) {
	            throw new CustomException(InventoryConstants.INSUFFICIENT_STOCK + product.getProductName());
	        }

	        cartItem.setQuantity(cartItemDto.getQuantity());
	        CartItem updatedCart = cartItemRepository.save(cartItem);
	        logger.info("Cart updated successfully. New quantity: {}", cartItemDto.getQuantity());
	        return updatedCart;
	    }

	    public Integer checkStock(Long productId) {
	        logger.info("Checking stock availability for product ID: {}", productId);
	        ProductEntity product = productRepository.findById(productId)
	                .orElseThrow(() -> new CustomException(InventoryConstants.ERROR_PRODUCT_NOT_FOUND + productId));

	        logger.info("Product {} has {} items in stock", product.getProductName(), product.getProductStock());
	        return product.getProductStock();
	    }

	    @Transactional
	    public String checkoutCart() {
	        logger.info("Initiating checkout process...");
	        List<CartItem> cartItems = cartItemRepository.findAll();

	        if (cartItems.isEmpty()) {
	            logger.warn("Checkout failed: Cart is empty.");
	            return "Cart is empty. Please add items before checkout.";
	        }

	        for (CartItem cartItem : cartItems) {
	            ProductEntity product = productRepository.findById(cartItem.getProduct().getProductId())
	                    .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + cartItem.getProduct().getProductId()));

	            if (product.getProductStock() < cartItem.getQuantity()) {
	                logger.error("Insufficient stock for product: {}", product.getProductName());
	                throw new CustomException(InventoryConstants.INSUFFICIENT_STOCK + product.getProductName());
	            }

	            product.setProductStock(product.getProductStock() - cartItem.getQuantity());
	            productRepository.save(product);
	        }

	        cartItemRepository.deleteAll();
	        logger.info("Checkout successful: Stock updated, and cart cleared.");
	        return "Checkout successful! Inventory updated and cart cleared.";
	    }

	    public Map<String, Double> getCartDiscounts() {
	        logger.info("Fetching discounts for cart items...");
	        Map<String, Double> discountMap = new HashMap<>();
	        List<CartItem> cartItems = cartItemRepository.findAll();

	        if (cartItems.isEmpty()) {
	            logger.warn("No items in the cart.");
	            discountMap.put("message", 0.0);
	            return discountMap;
	        }

	        for (CartItem cartItem : cartItems) {
	            double originalPrice = cartItem.getProduct().getProductPrice() * cartItem.getQuantity();
	            double discount = (originalPrice > DISCOUNT_THRESHOLD) ? (originalPrice * (DISCOUNT_PERCENTAGE / 100)) : 0.0;
	            double finalPrice = originalPrice - discount;
	            discountMap.put(cartItem.getProduct().getProductName(), finalPrice);
	        }

	        logger.info("Discounts successfully calculated.");
	        return discountMap;
	    }

	    public int getCartItemCount() {
	        logger.info("Fetching total item count in cart...");
	        List<CartItem> cartItems = cartItemRepository.findAll();

	        if (cartItems.isEmpty()) {
	            logger.warn("Cart is empty.");
	            return 0;
	        }

	        int totalItemCount = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
	        logger.info("Total items in cart: {}", totalItemCount);
	        return totalItemCount;
	    }
}

