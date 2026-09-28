package com.recruitment.util;

import com.alibaba.excel.annotation.ExcelProperty;

public class CandidateImportRow {
   @ExcelProperty({"姓名"})
   private String realName;
   @ExcelProperty({"性别"})
   private String gender;
   @ExcelProperty({"年龄"})
   private Integer age;
   @ExcelProperty({"手机号"})
   private String phone;
   @ExcelProperty({"学历"})
   private String education;
   @ExcelProperty({"毕业院校"})
   private String university;
   @ExcelProperty({"专业"})
   private String major;
   @ExcelProperty({"应聘岗位"})
   private String appliedPosition;
   @ExcelProperty({"邮箱"})
   private String email;

   public String getRealName() {
      return this.realName;
   }

   public void setRealName(String realName) {
      this.realName = realName;
   }

   public String getGender() {
      return this.gender;
   }

   public void setGender(String gender) {
      this.gender = gender;
   }

   public Integer getAge() {
      return this.age;
   }

   public void setAge(Integer age) {
      this.age = age;
   }

   public String getPhone() {
      return this.phone;
   }

   public void setPhone(String phone) {
      this.phone = phone;
   }

   public String getEducation() {
      return this.education;
   }

   public void setEducation(String education) {
      this.education = education;
   }

   public String getUniversity() {
      return this.university;
   }

   public void setUniversity(String university) {
      this.university = university;
   }

   public String getMajor() {
      return this.major;
   }

   public void setMajor(String major) {
      this.major = major;
   }

   public String getAppliedPosition() {
      return this.appliedPosition;
   }

   public void setAppliedPosition(String appliedPosition) {
      this.appliedPosition = appliedPosition;
   }

   public String getEmail() {
      return this.email;
   }

   public void setEmail(String email) {
      this.email = email;
   }
}
