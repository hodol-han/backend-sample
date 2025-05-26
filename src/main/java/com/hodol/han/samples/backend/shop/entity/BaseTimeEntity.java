package com.hodol.han.samples.backend.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass // This class's fields will be mapped to the database tables of its subclasses.
@EntityListeners(AuditingEntityListener.class) // Enables JPA auditing for this class.
public abstract class BaseTimeEntity {

  @CreatedDate // Marks this field to store the creation timestamp.
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate // Marks this field to store the last update timestamp.
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  // Getter for createdAt
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  // Getter for updatedAt
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  // Setters are generally not needed for these fields as JPA manages them.
  // If you were using Lombok, you might have @Getter here.
}
