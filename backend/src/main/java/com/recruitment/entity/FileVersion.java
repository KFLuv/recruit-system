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
   name = "file_version"
)
public class FileVersion {
   @Id
   @GeneratedValue(
      strategy = GenerationType.IDENTITY
   )
   private Long id;
   @Column(
      name = "biz_type",
      length = 50
   )
   private String bizType;
   @Column(
      name = "biz_id"
   )
   private Long bizId;
   @Column(
      name = "version"
   )
   private Integer version;
   @Column(
      name = "file_name",
      length = 200
   )
   private String fileName;
   @Column(
      name = "file_path",
      length = 500
   )
   private String filePath;
   @Column(
      name = "file_size"
   )
   private Long fileSize;
   @Column(
      name = "uploader",
      length = 50
   )
   private String uploader;
   @Column(
      name = "remark",
      length = 200
   )
   private String remark;
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

   public String getBizType() {
      return this.bizType;
   }

   public void setBizType(String bizType) {
      this.bizType = bizType;
   }

   public Long getBizId() {
      return this.bizId;
   }

   public void setBizId(Long bizId) {
      this.bizId = bizId;
   }

   public Integer getVersion() {
      return this.version;
   }

   public void setVersion(Integer version) {
      this.version = version;
   }

   public String getFileName() {
      return this.fileName;
   }

   public void setFileName(String fileName) {
      this.fileName = fileName;
   }

   public String getFilePath() {
      return this.filePath;
   }

   public void setFilePath(String filePath) {
      this.filePath = filePath;
   }

   public Long getFileSize() {
      return this.fileSize;
   }

   public void setFileSize(Long fileSize) {
      this.fileSize = fileSize;
   }

   public String getUploader() {
      return this.uploader;
   }

   public void setUploader(String uploader) {
      this.uploader = uploader;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setRemark(String remark) {
      this.remark = remark;
   }

   public LocalDateTime getCreatedAt() {
      return this.createdAt;
   }

   public void setCreatedAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
   }
}
