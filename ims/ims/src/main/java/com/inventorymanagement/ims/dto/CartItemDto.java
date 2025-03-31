package com.inventorymanagement.ims.dto;

import com.inventorymanagement.ims.constants.InventoryConstants;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CartItemDto {

	 @NotNull(message = InventoryConstants.INVALID_PRODUCT_ID)
	    private Long productId;

	    @NotNull(message = InventoryConstants.QUANTITY_REQUIRED)
	    @Min(value = 1, message = InventoryConstants.QUANTITY_MINIMUM)
	    private Integer quantity;

	    public CartItemDto() {}

	    public CartItemDto(Long productId, Integer quantity) {
	        this.productId = productId;
	        this.quantity = quantity;
	    }

	    public Long getProductId() {
	        return productId;
	    }

	    public void setProductId(Long productId) {
	        this.productId = productId;
	    }

	    public Integer getQuantity() {
	        return quantity;
	    }

	    public void setQuantity(Integer quantity) {
	        this.quantity = quantity;
	    }
	
}
