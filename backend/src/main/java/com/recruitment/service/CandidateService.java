package com.recruitment.service;

import com.recruitment.dto.CandidateDTO;
import com.recruitment.dto.ProjectExperienceDTO;
import com.recruitment.dto.WorkExperienceDTO;
import com.recruitment.entity.Candidate;
import com.recruitment.entity.FileVersion;
import com.recruitment.repository.CandidateRepository;
import com.recruitment.repository.FileVersionRepository;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CandidateService {
   @Autowired
   private CandidateRepository candidateRepository;
   @Autowired
   private FileVersionRepository fileVersionRepository;

   public CandidateDTO createCandidate(Candidate candidate) {
      int attempts = 0;

      String candidateNo;
      do {
         String timestamp = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
         long ms = System.currentTimeMillis() % 1000L;
         int random = (int)(Math.random() * (double)100.0F);
         candidateNo = "H" + timestamp + String.format("%03d", ms) + random;
         ++attempts;
      } while(attempts < 5 && this.candidateRepository.findByCandidateNo(candidateNo) != null);

      candidate.setCandidateNo(candidateNo);
      if (candidate.getWorkExperiences() != null) {
         candidate.getWorkExperiences().forEach((w) -> w.setCandidate(candidate));
      }

      if (candidate.getProjectExperiences() != null) {
         candidate.getProjectExperiences().forEach((p) -> p.setCandidate(candidate));
      }

      Candidate saved = (Candidate)this.candidateRepository.save(candidate);
      if (!this.isNullOrEmpty(saved.getResumeFilePath())) {
         this.recordFileVersion(saved.getId(), "resume", saved.getResumeFilePath());
      }

      return this.convertToDTO(saved);
   }

   public List<CandidateDTO> getAllCandidates() {
      return (List)this.candidateRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
   }

   public List<CandidateDTO> getCandidatesByStatus(String status) {
      return (List)this.candidateRepository.findByStatus(status).stream().map(this::convertToDTO).collect(Collectors.toList());
   }

   public List<CandidateDTO> searchCandidates(String keyword) {
      return (List)this.candidateRepository.findByRealNameContaining(keyword).stream().map(this::convertToDTO).collect(Collectors.toList());
   }

   public CandidateDTO getCandidateById(Long id) {
      Candidate candidate = (Candidate)this.candidateRepository.findById(id).orElseThrow(() -> new RuntimeException("候选人不存在"));
      return this.convertToDTO(candidate);
   }

   public CandidateDTO updateCandidate(Long id, Candidate candidate) {
      Candidate existing = (Candidate)this.candidateRepository.findById(id).orElseThrow(() -> new RuntimeException("候选人不存在"));
      boolean resumeUpdated = !this.isNullOrEmpty(existing.getResumeFilePath()) && !this.isNullOrEmpty(candidate.getResumeFilePath()) && !existing.getResumeFilePath().equals(candidate.getResumeFilePath());
      existing.setRealName(candidate.getRealName());
      existing.setGender(candidate.getGender());
      existing.setAge(candidate.getAge());
      existing.setEducation(candidate.getEducation());
      existing.setUniversity(candidate.getUniversity());
      existing.setMajor(candidate.getMajor());
      existing.setPhone(candidate.getPhone());
      existing.setEmail(candidate.getEmail());
      existing.setAppliedPositionId(candidate.getAppliedPositionId());
      existing.setAppliedPosition(candidate.getAppliedPosition());
      existing.setResumeSource(candidate.getResumeSource());
      existing.setStatus(candidate.getStatus());
      // 仅在传入值非空时覆盖，避免编辑表单不带该字段时清空已有淘汰原因
      if (candidate.getRejectionReason() != null && !candidate.getRejectionReason().trim().isEmpty()) {
         existing.setRejectionReason(candidate.getRejectionReason());
      }
      existing.setResumeFilePath(candidate.getResumeFilePath());
      existing.setScoreWritten(candidate.getScoreWritten());
      existing.setScoreInterview(candidate.getScoreInterview());
      existing.setScoreTotal(candidate.getScoreTotal());
      existing.setRating(candidate.getRating());
      existing.setCommentWritten(candidate.getCommentWritten());
      existing.setCommentInterview(candidate.getCommentInterview());
      existing.setInterviewer(candidate.getInterviewer());
      existing.setRemark(candidate.getRemark());
      existing.setIdNumber(candidate.getIdNumber());
      existing.setDomicile(candidate.getDomicile());
      existing.setCurrentAddress(candidate.getCurrentAddress());
      existing.setPlanId(candidate.getPlanId());
      existing.setPlanName(candidate.getPlanName());
      existing.setOrgUnitName(candidate.getOrgUnitName());
      existing.setWorkYears(candidate.getWorkYears());
      existing.setExpectedSalary(candidate.getExpectedSalary());
      existing.setAvailableDate(candidate.getAvailableDate());
      existing.setEmergencyContact(candidate.getEmergencyContact());
      existing.setEmergencyPhone(candidate.getEmergencyPhone());
      existing.setScoreFirstInterview(candidate.getScoreFirstInterview());
      existing.setScoreSecondInterview(candidate.getScoreSecondInterview());
      existing.setInterviewerFirst(candidate.getInterviewerFirst());
      existing.setInterviewerSecond(candidate.getInterviewerSecond());
      existing.setCommentFirstInterview(candidate.getCommentFirstInterview());
      existing.setCommentSecondInterview(candidate.getCommentSecondInterview());
      existing.setScreeningResult(candidate.getScreeningResult());
      existing.setInterviewSchedule(candidate.getInterviewSchedule());
      existing.setMedicalResult(candidate.getMedicalResult());
      existing.setBackgroundCheckResult(candidate.getBackgroundCheckResult());
      existing.setHireConclusion(candidate.getHireConclusion());
      existing.setCertificates(candidate.getCertificates());
      existing.setAwards(candidate.getAwards());
      existing.setSelfEvaluation(candidate.getSelfEvaluation());
      if (candidate.getWorkExperiences() != null) {
         candidate.getWorkExperiences().forEach((w) -> w.setCandidate(existing));
      }

      existing.setWorkExperiences(candidate.getWorkExperiences());
      if (candidate.getProjectExperiences() != null) {
         candidate.getProjectExperiences().forEach((p) -> p.setCandidate(existing));
      }

      existing.setProjectExperiences(candidate.getProjectExperiences());
      Candidate updated = (Candidate)this.candidateRepository.save(existing);
      if (resumeUpdated) {
         this.recordFileVersion(updated.getId(), "resume", candidate.getResumeFilePath());
      }

      return this.convertToDTO(updated);
   }

   public void deleteCandidate(Long id) {
      this.candidateRepository.deleteById(id);
   }

   public void updateCandidateStatus(Long id, String status, String rejectionReason) {
      Candidate candidate = (Candidate)this.candidateRepository.findById(id).orElseThrow(() -> new RuntimeException("候选人不存在"));
      candidate.setStatus(status);
      if (rejectionReason != null && !rejectionReason.trim().isEmpty()) {
         candidate.setRejectionReason(rejectionReason);
      }
      this.candidateRepository.save(candidate);
   }

   public void recordScore(Long id, Double writtenScore, Double interviewScore, String rating) {
      Candidate candidate = (Candidate)this.candidateRepository.findById(id).orElseThrow(() -> new RuntimeException("候选人不存在"));
      candidate.setScoreWritten(writtenScore);
      candidate.setScoreInterview(interviewScore);
      if (writtenScore != null && interviewScore != null) {
         candidate.setScoreTotal((writtenScore + interviewScore) / (double)2.0F);
      }

      candidate.setRating(rating);
      this.candidateRepository.save(candidate);
   }

   public void recordFileVersion(Long bizId, String bizType, String filePath) {
      try {
         File file = this.resolveUploadFile(filePath);
         if (!file.exists()) {
            return;
         }

         List<FileVersion> existing = this.fileVersionRepository.findByBizTypeAndBizIdOrderByVersionDesc(bizType, bizId);
         int nextVersion = existing.isEmpty() ? 1 : ((FileVersion)existing.get(0)).getVersion() + 1;
         FileVersion version = new FileVersion();
         version.setBizType(bizType);
         version.setBizId(bizId);
         version.setVersion(nextVersion);
         version.setFileName(file.getName());
         version.setFilePath(filePath);
         version.setFileSize(file.length());
         version.setUploader("system");
         version.setRemark("简历上传");
         this.fileVersionRepository.save(version);
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public void recordFileVersionManually(Long candidateId) {
      Candidate candidate = (Candidate)this.candidateRepository.findById(candidateId).orElseThrow(() -> new RuntimeException("候选人不存在：" + candidateId));
      if (!this.isNullOrEmpty(candidate.getResumeFilePath())) {
         this.recordFileVersion(candidateId, "resume", candidate.getResumeFilePath());
      }

   }

   private boolean isNullOrEmpty(String str) {
      return str == null || str.trim().isEmpty();
   }

   /** 将 /uploads/... 相对路径解析为实际文件路径 */
   private File resolveUploadFile(String filePath) {
      if (filePath == null || filePath.trim().isEmpty()) return new File(filePath == null ? "" : filePath);
      String p = filePath.trim();
      if (p.startsWith("/uploads/")) {
         return new File(System.getProperty("user.dir") + File.separator + p.substring(1).replace("/", File.separator));
      }
      return new File(p);
   }

   private CandidateDTO convertToDTO(Candidate candidate) {
      CandidateDTO dto = new CandidateDTO();
      dto.setId(candidate.getId());
      dto.setCandidateNo(candidate.getCandidateNo());
      dto.setRealName(candidate.getRealName());
      dto.setGender(candidate.getGender());
      dto.setAge(candidate.getAge());
      dto.setEducation(candidate.getEducation());
      dto.setUniversity(candidate.getUniversity());
      dto.setMajor(candidate.getMajor());
      dto.setPhone(candidate.getPhone());
      dto.setEmail(candidate.getEmail());
      dto.setAppliedPositionId(candidate.getAppliedPositionId());
      dto.setAppliedPosition(candidate.getAppliedPosition());
      dto.setResumeSource(candidate.getResumeSource());
      dto.setStatus(candidate.getStatus());
      dto.setRejectionReason(candidate.getRejectionReason());
      dto.setCreatedAt(candidate.getCreatedAt());
      dto.setResumeFilePath(candidate.getResumeFilePath());
      dto.setScoreWritten(candidate.getScoreWritten());
      dto.setScoreInterview(candidate.getScoreInterview());
      dto.setScoreTotal(candidate.getScoreTotal());
      dto.setRating(candidate.getRating());
      dto.setCommentWritten(candidate.getCommentWritten());
      dto.setCommentInterview(candidate.getCommentInterview());
      dto.setInterviewer(candidate.getInterviewer());
      dto.setRemark(candidate.getRemark());
      dto.setIdNumber(candidate.getIdNumber());
      dto.setDomicile(candidate.getDomicile());
      dto.setCurrentAddress(candidate.getCurrentAddress());
      dto.setPlanId(candidate.getPlanId());
      dto.setPlanName(candidate.getPlanName());
      dto.setOrgUnitName(candidate.getOrgUnitName());
      dto.setWorkYears(candidate.getWorkYears());
      dto.setExpectedSalary(candidate.getExpectedSalary());
      dto.setAvailableDate(candidate.getAvailableDate());
      dto.setEmergencyContact(candidate.getEmergencyContact());
      dto.setEmergencyPhone(candidate.getEmergencyPhone());
      dto.setScoreFirstInterview(candidate.getScoreFirstInterview());
      dto.setScoreSecondInterview(candidate.getScoreSecondInterview());
      dto.setInterviewerFirst(candidate.getInterviewerFirst());
      dto.setInterviewerSecond(candidate.getInterviewerSecond());
      dto.setCommentFirstInterview(candidate.getCommentFirstInterview());
      dto.setCommentSecondInterview(candidate.getCommentSecondInterview());
      dto.setScreeningResult(candidate.getScreeningResult());
      dto.setInterviewSchedule(candidate.getInterviewSchedule());
      dto.setMedicalResult(candidate.getMedicalResult());
      dto.setBackgroundCheckResult(candidate.getBackgroundCheckResult());
      dto.setHireConclusion(candidate.getHireConclusion());
      dto.setCertificates(candidate.getCertificates());
      dto.setAwards(candidate.getAwards());
      dto.setSelfEvaluation(candidate.getSelfEvaluation());
      if (candidate.getWorkExperiences() != null) {
         dto.setWorkExperiences((List)candidate.getWorkExperiences().stream().map((w) -> {
            WorkExperienceDTO wd = new WorkExperienceDTO();
            wd.setId(w.getId());
            wd.setCandidateId(w.getCandidateId());
            wd.setCompanyName(w.getCompanyName());
            wd.setPosition(w.getPosition());
            wd.setStartDate(w.getStartDate() != null ? w.getStartDate().toString() : null);
            wd.setEndDate(w.getEndDate() != null ? w.getEndDate().toString() : null);
            wd.setDescription(w.getDescription());
            wd.setAttachmentPath(w.getAttachmentPath());
            return wd;
         }).collect(Collectors.toList()));
      } else {
         dto.setWorkExperiences(Collections.emptyList());
      }

      if (candidate.getProjectExperiences() != null) {
         dto.setProjectExperiences((List)candidate.getProjectExperiences().stream().map((p) -> {
            ProjectExperienceDTO pd = new ProjectExperienceDTO();
            pd.setId(p.getId());
            pd.setCandidateId(p.getCandidateId());
            pd.setProjectName(p.getProjectName());
            pd.setProjectStart(p.getProjectStart());
            pd.setProjectEnd(p.getProjectEnd());
            pd.setRole(p.getRole());
            pd.setDescription(p.getDescription());
            pd.setAchievement(p.getAchievement());
            pd.setAttachmentPath(p.getAttachmentPath());
            return pd;
         }).collect(Collectors.toList()));
      } else {
         dto.setProjectExperiences(Collections.emptyList());
      }

      return dto;
   }
}
