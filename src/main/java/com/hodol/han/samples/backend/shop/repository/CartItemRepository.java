package com.hodol.han.samples.backend.shop.repository;

import com.hodol.han.samples.backend.shop.entity.CartItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}
