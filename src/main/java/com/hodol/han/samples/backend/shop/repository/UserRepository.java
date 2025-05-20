package com.hodol.han.samples.backend.shop.repository;

import com.hodol.han.samples.backend.shop.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
}
