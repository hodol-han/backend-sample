package com.hodol.han.samples.backend.shop.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a user entity in the system.
 *
 * <p>Note: The {@code toString} method intentionally excludes the password field to prevent
 * accidental exposure of sensitive information in logs or outputs.
 */
@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "role")
  private Set<String> roles;

  // Default constructor required by JPA
  public User() {}

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<String> getRoles() {
    return roles;
  }

  public void setRoles(Set<String> roles) {
    this.roles = roles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    if (id == null) {
      // For transient instances, fallback to super.equals() if id is null for both.
      return user.id == null && super.equals(o);
    }
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    // Use a consistent fallback for transient instances based on unique fields like username.
    return id != null ? Objects.hash(id) : Objects.hash(username);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("User{");
    sb.append("id=")
        .append(id)
        .append(", username='")
        .append(username)
        .append('\'')
        // Do not include password in toString for security reasons
        .append(", roles=")
        .append(roles)
        // Removed cartId from toString
        .append(", createdAt=")
        .append(getCreatedAt() != null ? getCreatedAt().toString() : "null")
        .append(", updatedAt=")
        .append(getUpdatedAt() != null ? getUpdatedAt().toString() : "null")
        .append('}');
    return sb.toString();
  }
}
