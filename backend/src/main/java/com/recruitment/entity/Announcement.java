package com.recruitment.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(
   name = "announcement"
)
public class Announcement {
   @Id
   @GeneratedValue(
      strategy = GenerationType.IDENTITY
   )
   private Long id;
   @Column(
      name = "title",
      length = 100
   )
   private String title;
   @Column(
      name = "content",
      length = 2000
   )
   private String content;
   @Column(
      name = "author",
      length = 50
   )
   private String author;
   @Column(
      name = "is_published"
   )
   private Boolean published = false;
   @Column(
      name = "created_at"
   )
   private LocalDateTime createdAt;
   @Column(
      name = "updated_at"
   )
   private LocalDateTime updatedAt;

   @PrePersist
   protected void onCreate() {
      this.createdAt = LocalDateTime.now();
      this.updatedAt = LocalDateTime.now();
   }

   @PreUpdate
   protected void onUpdate() {
      this.updatedAt = LocalDateTime.now();
   }

   public Long getId() {
      return this.id;
   }

   public void setId(Long id) {
      this.id = id;
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

   public String getAuthor() {
      return this.author;
   }

   public void setAuthor(String author) {
      this.author = author;
   }

   public Boolean getPublished() {
      return this.published;
   }

   public void setPublished(Boolean published) {
      this.published = published;
   }

   public LocalDateTime getCreatedAt() {
      return this.createdAt;
   }

   public void setCreatedAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
   }

   public LocalDateTime getUpdatedAt() {
      return this.updatedAt;
   }

   public void setUpdatedAt(LocalDateTime updatedAt) {
      this.updatedAt = updatedAt;
   }
}
