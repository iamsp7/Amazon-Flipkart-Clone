package com.inventorymanagement.ims.controller;

import com.inventorymanagement.ims.constants.InventoryConstants; 
import com.inventorymanagement.ims.dto.CartItemDto;
import com.inventorymanagement.ims.entity.CartItem; 
import com.inventorymanagement.ims.exception.CustomException;
import com.inventorymanagement.ims.exception.DataIntegrityViolationException;
import com.inventorymanagement.ims.exception.ProductNotFoundException;
import com.inventorymanagement.ims.service.CartService;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;
    

    
    
  
    //add product into cart

    @PostMapping("/addtocart")
    public ResponseEntity<?> addToCart(@RequestBody CartItemDto cartItemDTO) {
        try {
            cartService.addToCart(cartItemDTO);
            return ResponseEntity.ok(InventoryConstants.PRODUCT_ADDED_TO_CART);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(InventoryConstants.ERROR + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(InventoryConstants.ERROR_WHILE_ADDING_TO_CART + ": " + e.getMessage());
        }
    }


    //get all products available in cart

    @GetMapping("/getall")
    public ResponseEntity<?> getAllCartItems() {
        try {
            List<CartItem> cartItems = cartService.getAllCartItems();
            return ResponseEntity.ok(cartItems);
        } catch (Exception e) {
            logger.error("Error fetching cart items", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching cart items.");
        }
    }

    //calculate the total cost of products in cart
    @GetMapping("/total")
    public ResponseEntity<?> getTotalCartCost() {
        try {
            double totalCost = cartService.getTotalCartCost();
            return ResponseEntity.ok(totalCost);
        } catch (Exception e) {
            logger.error("Error calculating total cart cost", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error calculating total cost.");
        }
    }
    
    //purchase the porudcts from cart

    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseCart() {
        try {
            logger.info("Processing cart purchase...");
            cartService.purchaseCart();
            return ResponseEntity.ok("Purchase successful, items removed from inventory and cart.");
        } catch (CustomException e) {
            logger.error("Custom Exception occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            logger.error("Stock constraint violation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Stock error: Not enough inventory.");
        } catch (Exception e) {
            logger.error("An error occurred during purchase", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing purchase.");
        }
    }

    
    //remove products from cart

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(@RequestParam Long productId, @RequestParam int quantity) {
        try {
            logger.info("Received request: productId={}, quantity={}", productId, quantity);

            if (productId == null || quantity <= 0) {
                logger.error("Invalid request: productId={} or quantity={}", productId, quantity);
                return ResponseEntity.badRequest().body("Invalid request: productId and quantity must be provided and valid.");
            }

            CartItemDto cartItemDto = new CartItemDto(productId, quantity);
            cartService.removeFromCart(cartItemDto);

            return ResponseEntity.ok("Product removed from cart.");
        } catch (ProductNotFoundException e) {
            logger.error("Product not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Product not found in cart.");
        } catch (CustomException e) {
            logger.error("Cart item error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while removing product from cart", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing item from cart.");
        }
    }

    
    @PutMapping("/{cartId}/updatequantity")
    public ResponseEntity<?> updateCartQuantity(@PathVariable Long cartId, @RequestBody CartItemDto cartItemDto) {
        try {
            CartItem updatedCart = cartService.updateCartQuantity(cartId, cartItemDto);
            return ResponseEntity.ok(updatedCart);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating cart quantity.");
        }
    }

    
    /**
     * API to check stock availability for a given product.
     */
    @GetMapping("/checkstock/{productId}")
    public ResponseEntity<?> checkStock(@PathVariable Long productId) {
        logger.info("Received request to check stock for product ID: {}", productId);

        try {
            Integer stock = cartService.checkStock(productId);
            return ResponseEntity.ok("Product ID: " + productId + " has " + stock + " items in stock.");
        } catch (ProductNotFoundException e) {
            logger.error("Product not found: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while checking stock: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("An error occurred while checking stock.");
        }
    }
    
    
    /**
     * API endpoint to handle cart checkout process.
     */
    @PostMapping("/checkout")
    public ResponseEntity<?> checkoutCart() {
        logger.info("Received request to checkout cart.");

        try {
            String response = cartService.checkoutCart();
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            logger.error("Product not found during checkout: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (CustomException e) {
            logger.error("Stock unavailable during checkout: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during checkout: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred during checkout.");
        }
    }
    
    /**
     * Retrieves discounts for each cart item (10% discount on items over 1000).
     */
    @GetMapping("/discount")
    public ResponseEntity<?> getCartDiscounts() {
        logger.info("Request received to fetch cart item discounts.");

        try {
            Map<String, Double> discounts = cartService.getCartDiscounts();
            return ResponseEntity.ok(discounts);
        } catch (Exception e) {
            logger.error("Error occurred while fetching cart discounts: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("An error occurred while calculating discounts.");
        }
    }
    
    /**
     * Retrieves the total item count in the cart.
     */
    @GetMapping("/itemcount")
    public ResponseEntity<?> getCartItemCount() {
        logger.info("Request received to fetch total item count in cart.");

        try {
            int totalItemCount = cartService.getCartItemCount();
            return ResponseEntity.ok(totalItemCount);
        } catch (Exception e) {
            logger.error("Error occurred while fetching cart item count: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("An error occurred while fetching cart item count.");
        }
    }
    
}
