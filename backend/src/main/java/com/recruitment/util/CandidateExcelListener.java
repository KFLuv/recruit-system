package com.recruitment.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.recruitment.entity.Candidate;
import com.recruitment.repository.CandidateRepository;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CandidateExcelListener implements ReadListener<CandidateImportRow> {
   private final CandidateRepository candidateRepository;
   private final List<Candidate> batch = new ArrayList();
   private static final int BATCH_SIZE = 100;
   private int totalCount = 0;
   private int successCount = 0;
   private final StringBuilder errors = new StringBuilder();

   public CandidateExcelListener(CandidateRepository candidateRepository) {
      this.candidateRepository = candidateRepository;
   }

   public void invoke(CandidateImportRow row, AnalysisContext context) {
      ++this.totalCount;
      if (row.getRealName() != null && !row.getRealName().trim().isEmpty()) {
         Candidate c = new Candidate();
         c.setRealName(row.getRealName());
         if (row.getGender() != null && !row.getGender().trim().isEmpty()) {
            c.setGender(row.getGender());
         }

         if (row.getAge() != null) {
            c.setAge(row.getAge());
         }

         if (row.getPhone() != null && !row.getPhone().trim().isEmpty()) {
            c.setPhone(row.getPhone());
         }

         if (row.getEducation() != null && !row.getEducation().trim().isEmpty()) {
            c.setEducation(row.getEducation());
         }

         if (row.getUniversity() != null && !row.getUniversity().trim().isEmpty()) {
            c.setUniversity(row.getUniversity());
         }

         if (row.getMajor() != null && !row.getMajor().trim().isEmpty()) {
            c.setMajor(row.getMajor());
         }

         if (row.getAppliedPosition() != null && !row.getAppliedPosition().trim().isEmpty()) {
            c.setAppliedPosition(row.getAppliedPosition());
         }

         if (row.getEmail() != null && !row.getEmail().trim().isEmpty()) {
            c.setEmail(row.getEmail());
         }

         c.setStatus("new");
         String var10000 = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
         String no = "H" + var10000 + (int)(Math.random() * (double)100.0F);
         c.setCandidateNo(no);
         this.batch.add(c);
         if (this.batch.size() >= 100) {
            this.candidateRepository.saveAll(this.batch);
            this.successCount += this.batch.size();
            this.batch.clear();
         }

      } else {
         this.errors.append("第").append(this.totalCount + 1).append("行：姓名为空\n");
      }
   }

   public void doAfterAllAnalysed(AnalysisContext context) {
      if (!this.batch.isEmpty()) {
         this.candidateRepository.saveAll(this.batch);
         this.successCount += this.batch.size();
         this.batch.clear();
      }

   }

   public int getSuccessCount() {
      return this.successCount;
   }

   public String getErrors() {
      return this.errors.toString();
   }
}
