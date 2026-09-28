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
   name = "system_log"
)
public class SystemLog {
   @Id
   @GeneratedValue(
      strategy = GenerationType.IDENTITY
   )
   private Long id;
   @Column(
      name = "username",
      length = 50
   )
   private String username;
   @Column(
      name = "user_role",
      length = 20
   )
   private String userRole;
   @Column(
      name = "module",
      length = 50
   )
   private String module;
   @Column(
      name = "description",
      length = 500
   )
   private String description;
   @Column(
      name = "method",
      length = 100
   )
   private String method;
   @Column(
      name = "request_url",
      length = 300
   )
   private String requestUrl;
   @Column(
      name = "request_params",
      length = 2000
   )
   private String requestParams;
   @Column(
      name = "ip",
      length = 50
   )
   private String ip;
   @Column(
      name = "duration"
   )
   private Long duration;
   @Column(
      name = "result",
      length = 10
   )
   private String result;
   @Column(
      name = "error_msg",
      length = 2000
   )
   private String errorMsg;
   @Column(
      name = "biz_no",
      length = 100
   )
   private String bizNo;
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

   public String getUsername() {
      return this.username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getUserRole() {
      return this.userRole;
   }

   public void setUserRole(String userRole) {
      this.userRole = userRole;
   }

   public String getModule() {
      return this.module;
   }

   public void setModule(String module) {
      this.module = module;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getMethod() {
      return this.method;
   }

   public void setMethod(String method) {
      this.method = method;
   }

   public String getRequestUrl() {
      return this.requestUrl;
   }

   public void setRequestUrl(String requestUrl) {
      this.requestUrl = requestUrl;
   }

   public String getRequestParams() {
      return this.requestParams;
   }

   public void setRequestParams(String requestParams) {
      this.requestParams = requestParams;
   }

   public String getIp() {
      return this.ip;
   }

   public void setIp(String ip) {
      this.ip = ip;
   }

   public Long getDuration() {
      return this.duration;
   }

   public void setDuration(Long duration) {
      this.duration = duration;
   }

   public String getResult() {
      return this.result;
   }

   public void setResult(String result) {
      this.result = result;
   }

   public String getErrorMsg() {
      return this.errorMsg;
   }

   public void setErrorMsg(String errorMsg) {
      this.errorMsg = errorMsg;
   }

   public String getBizNo() {
      return this.bizNo;
   }

   public void setBizNo(String bizNo) {
      this.bizNo = bizNo;
   }

   public LocalDateTime getCreatedAt() {
      return this.createdAt;
   }

   public void setCreatedAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
   }
}
