package com.inventorymanagement.ims.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventorymanagement.ims.entity.ProductEntity;


@Repository
public interface ProductRepository extends JpaRepository<ProductEntity,Long> {
	
	
	public ProductEntity findByproductIdAndDeletedFalse(Long productId);
	
	
	public List<ProductEntity> findByDeletedFalse();
	
	List<ProductEntity> findByDeletedTrueAndDeletedAtBefore(LocalDateTime dateTime);

	
	
	

}
