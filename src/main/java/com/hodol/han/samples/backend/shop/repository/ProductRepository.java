package com.hodol.han.samples.backend.shop.repository;

import com.hodol.han.samples.backend.shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {}
