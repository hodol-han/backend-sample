package com.hodol.han.samples.backend.shop.repository;

import com.hodol.han.samples.backend.shop.entity.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
  Optional<Cart> findByUserId(Long userId);
}
