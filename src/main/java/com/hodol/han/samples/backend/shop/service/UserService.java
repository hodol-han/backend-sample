package com.hodol.han.samples.backend.shop.service;

import com.hodol.han.samples.backend.shop.dto.UserSignupRequest;
import com.hodol.han.samples.backend.shop.entity.User;
import com.hodol.han.samples.backend.shop.exception.DuplicateUserException;
import com.hodol.han.samples.backend.shop.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.Collections;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User signup(@Valid UserSignupRequest request) {
    String username = request.getUsername();

    if (userRepository.findByUsername(username).isPresent()) {
      throw new DuplicateUserException(username);
    }
    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRoles(Collections.singleton("USER"));
    return userRepository.save(user);
  }
}
