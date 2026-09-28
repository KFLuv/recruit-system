package com.recruitment.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(
   name = "notification"
)
public class Notification {
   @Id
   @GeneratedValue(
      strategy = GenerationType.IDENTITY
   )
   private Long id;
   @Column(
      name = "user_id"
   )
   private Long userId;
   @Column(
      name = "title",
      length = 100
   )
   private String title;
   @Column(
      name = "content",
      length = 500
   )
   private String content;
   @Column(
      name = "type",
      length = 20
   )
   private String type;
   @Column(
      name = "related_id"
   )
   private Long relatedId;
   @Column(
      name = "is_read"
   )
   private Boolean read = false;
   @Column(
      name = "created_at"
   )
   private LocalDateTime createdAt;

   @PrePersist
   protected void onCreate() {
      this.createdAt = LocalDateTime.now();
   }

   public Long getId() {
      return this.id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public Long getUserId() {
      return this.userId;
   }

   public void setUserId(Long userId) {
      this.userId = userId;
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getContent() {
      return this.content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public String getType() {
      return this.type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public Long getRelatedId() {
      return this.relatedId;
   }

   public void setRelatedId(Long relatedId) {
      this.relatedId = relatedId;
   }

   public Boolean getRead() {
      return this.read;
   }

   public void setRead(Boolean read) {
      this.read = read;
   }

   public LocalDateTime getCreatedAt() {
      return this.createdAt;
   }

   public void setCreatedAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
   }
}
