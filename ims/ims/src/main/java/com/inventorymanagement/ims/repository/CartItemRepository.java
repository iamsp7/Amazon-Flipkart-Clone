package com.inventorymanagement.ims.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventorymanagement.ims.entity.CartItem;
import com.inventorymanagement.ims.entity.ProductEntity;


@Repository
public interface CartItemRepository  extends JpaRepository<CartItem , Long>{
	
	//List<CartItem> findByCartId(Long cartId);
	
	Optional<CartItem> findByProduct(ProductEntity product);


}
