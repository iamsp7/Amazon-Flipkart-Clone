package com.inventorymanagement.ims.entity;

import java.time.LocalDateTime;

import com.inventorymanagement.ims.constants.InventoryConstants; 

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Entity
@Table(name = "products")
public class ProductEntity {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productId;
	
	//@NotBlank(message = InventoryConstants.PRODUCT_NAME_REQUIRED)
	private String productName;
	
	// @NotNull(message = InventoryConstants.PRICE_REQUIRED)
	// @Min(value = 1, message = InventoryConstants.PRICE_MINIMUM)
	private Integer productPrice;
	
	// @NotNull(message = InventoryConstants.STOCK_REQUIRED)
//	@Min(value = 1, message = InventoryConstants.STOCK_MINIMUM)
	private Integer productStock;
	
    //@NotBlank(message = InventoryConstants.CATEGORY_REQUIRED)
	private String productCategory;
	
	
	
	
	
	private boolean deleted = false;
	
	
	private LocalDateTime deletedAt;
	
	public ProductEntity() {}

	public ProductEntity(Long productId, String productName, Integer productPrice, Integer productStock,
			String productCategory, boolean deleted, LocalDateTime deletedAt) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.productPrice = productPrice;
		this.productStock = productStock;
		this.productCategory = productCategory;
		this.deleted = deleted;
		this.deletedAt = deletedAt;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Integer productPrice) {
		this.productPrice = productPrice;
	}

	public Integer getProductStock() {
		return productStock;
	}

	public void setProductStock(Integer productStock) {
		this.productStock = productStock;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}

	@Override
	public String toString() {
		return "ProductEntity [productId=" + productId + ", productName=" + productName + ", productPrice="
				+ productPrice + ", productStock=" + productStock + ", productCategory=" + productCategory
				+ ", deleted=" + deleted + ", deletedAt=" + deletedAt + "]";
	}


	
	


	
	
	

	

	
	
}
