package com.recruitment.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.recruitment.annotation.Log;
import com.recruitment.dto.CandidateDTO;
import com.recruitment.dto.ProjectExperienceDTO;
import com.recruitment.dto.UserDTO;
import com.recruitment.dto.WorkExperienceDTO;
import com.recruitment.entity.Candidate;
import com.recruitment.entity.ProjectExperience;
import com.recruitment.entity.WorkExperience;
import com.recruitment.repository.CandidateRepository;
import com.recruitment.service.CandidateService;
import com.recruitment.util.CandidateExcelListener;
import com.recruitment.util.CandidateImportRow;
import com.recruitment.util.PermissionUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping({"/api/candidates"})
public class CandidateController {
   @Autowired
   private CandidateService candidateService;
   @Autowired
   private CandidateRepository candidateRepository;

   @PostMapping
   @Log(
      module = "候选人",
      description = "新建候选人"
   )
   public Map<String, Object> createCandidate(@RequestBody Map<String, Object> candidateData, HttpSession session) {
      Map<String, Object> result = new HashMap();

      try {
         UserDTO user = PermissionUtil.getCurrentUser(session);
         PermissionUtil.requireRole(user, "admin", "manager");
         Candidate candidate = this.convertToEntity(candidateData);
         CandidateDTO created = this.candidateService.createCandidate(candidate);
         result.put("success", true);
         result.put("data", created);
         result.put("message", "创建成功");
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }

   @GetMapping
   public Map<String, Object> getAllCandidates(@RequestParam(required = false) String status, @RequestParam(required = false) String keyword) {
      Map<String, Object> result = new HashMap();

      try {
         List<CandidateDTO> candidates;
         if (status != null) {
            candidates = this.candidateService.getCandidatesByStatus(status);
         } else if (keyword != null && !keyword.isEmpty()) {
            candidates = this.candidateService.searchCandidates(keyword);
         } else {
            candidates = this.candidateService.getAllCandidates();
         }

         result.put("success", true);
         result.put("data", candidates);
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }

   @GetMapping({"/search"})
   public Map<String, Object> searchCandidates(@RequestParam String keyword) {
      Map<String, Object> result = new HashMap();

      try {
         List<Candidate> candidates = this.candidateRepository.searchCandidates(keyword);
         result.put("success", true);
         result.put("data", candidates.stream().map(this::convertToDTO).collect(Collectors.toList()));
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }

   @GetMapping({"/{id}"})
   public Map<String, Object> getCandidateById(@PathVariable Long id) {
      Map<String, Object> result = new HashMap();

      try {
         CandidateDTO candidate = this.candidateService.getCandidateById(id);
         result.put("success", true);
         result.put("data", candidate);
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }

   @PutMapping({"/{id}"})
   @Log(
      module = "候选人",
      description = "更新候选人"
   )
   public Map<String, Object> updateCandidate(@PathVariable Long id, @RequestBody Map<String, Object> candidateData, HttpSession session) {
      Map<String, Object> result = new HashMap();

      try {
         UserDTO user = PermissionUtil.getCurrentUser(session);
         PermissionUtil.requireRole(user, "admin", "manager");
         Candidate candidate = this.convertToEntity(candidateData);
         CandidateDTO updated = this.candidateService.updateCandidate(id, candidate);
         result.put("success", true);
         result.put("data", updated);
         result.put("message", "更新成功");
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }

   @DeleteMapping({"/{id}"})
   @Log(
      module = "候选人",
      description = "删除候选人"
   )
   public Map<String, Object> deleteCandidate(@PathVariable Long id, HttpSession session) {
      Map<String, Object> result = new HashMap();

      try {
         UserDTO user = PermissionUtil.getCurrentUser(session);
         PermissionUtil.requireRole(user, "admin", "manager");
         CandidateDTO candidate = this.candidateService.getCandidateById(id);
         this.candidateService.deleteCandidate(id);
         result.put("success", true);
         result.put("data", candidate);
         result.put("message", "删除成功");
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }

   @GetMapping({"/{id}/resume"})
   public void downloadResume(@PathVariable Long id, HttpServletResponse response) {
      try {
         Candidate candidate = (Candidate)this.candidateRepository.findById(id).orElseThrow(() -> new RuntimeException("候选人不存在"));
         String filePath = candidate.getResumeFilePath();
         if (filePath == null || filePath.trim().isEmpty()) {
            response.setStatus(404);
            response.getWriter().write("简历文件不存在");
            return;
         }

         File file = this.resolveUploadFile(filePath);
         if (!file.exists()) {
            response.setStatus(404);
            response.getWriter().write("文件不存在");
            return;
         }

         String fileName = file.getName();
         response.setContentType("application/octet-stream");
         response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
         FileInputStream fis = new FileInputStream(file);
         OutputStream os = response.getOutputStream();
         byte[] buffer = new byte[8192];

         int bytesRead;
         while((bytesRead = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
         }

         fis.close();
         os.flush();
         os.close();
      } catch (Exception var12) {
         Exception e = var12;
         var12.printStackTrace();
         response.setStatus(500);

         try {
            response.getWriter().write("下载失败：" + e.getMessage());
         } catch (Exception var11) {
         }
      }

   }

   @PostMapping({"/{id}/status"})
   @Log(
      module = "候选人",
      description = "更新候选人状态"
   )
   public Map<String, Object> updateCandidateStatus(@PathVariable Long id, @RequestBody Map<String, String> params, HttpSession session) {
      Map<String, Object> result = new HashMap();

      try {
         UserDTO user = PermissionUtil.getCurrentUser(session);
         PermissionUtil.requireRole(user, "admin", "manager");
         String status = (String)params.get("status");
         String rejectionReason = (String)params.get("rejectionReason");
         this.candidateService.updateCandidateStatus(id, status, rejectionReason);
         result.put("success", true);
         result.put("data", this.candidateService.getCandidateById(id));
         result.put("message", "状态更新成功");
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }

   @PostMapping({"/{id}/score"})
   @Log(
      module = "候选人",
      description = "记录成绩"
   )
   public Map<String, Object> recordScore(@PathVariable Long id, @RequestBody Map<String, Object> scores, HttpSession session) {
      Map<String, Object> result = new HashMap();

      try {
         UserDTO user = PermissionUtil.getCurrentUser(session);
         PermissionUtil.requireRole(user, "admin", "manager", "judge");
         Double written = scores.containsKey("written") ? ((Number)scores.get("written")).doubleValue() : null;
         Double interview = scores.containsKey("interview") ? ((Number)scores.get("interview")).doubleValue() : null;
         String rating = (String)scores.get("rating");
         this.candidateService.recordScore(id, written, interview, rating);
         result.put("success", true);
         result.put("data", this.candidateService.getCandidateById(id));
         result.put("message", "成绩记录成功");
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }

   private Candidate convertToEntity(Map<String, Object> data) {
      Candidate candidate = new Candidate();
      candidate.setRealName((String)data.get("realName"));
      candidate.setGender((String)data.get("gender"));
      candidate.setAge((Integer)data.get("age"));
      candidate.setEducation((String)data.get("education"));
      candidate.setUniversity((String)data.get("university"));
      candidate.setMajor((String)data.get("major"));
      candidate.setPhone((String)data.get("phone"));
      candidate.setEmail((String)data.get("email"));
      candidate.setAppliedPosition((String)data.get("appliedPosition"));
      candidate.setResumeSource((String)data.get("resumeSource"));
      candidate.setResumeFilePath((String)data.get("resumeFilePath"));
      candidate.setStatus((String)data.get("status"));
      Object sw = data.get("scoreWritten");
      if (sw instanceof Number) {
         candidate.setScoreWritten(((Number)sw).doubleValue());
      }

      Object si = data.get("scoreInterview");
      if (si instanceof Number) {
         candidate.setScoreInterview(((Number)si).doubleValue());
      }

      candidate.setRating((String)data.get("rating"));
      candidate.setCommentWritten((String)data.get("commentWritten"));
      candidate.setCommentInterview((String)data.get("commentInterview"));
      candidate.setInterviewer((String)data.get("interviewer"));
      candidate.setRemark((String)data.get("remark"));
      candidate.setIdNumber((String)data.get("idNumber"));
      candidate.setDomicile((String)data.get("domicile"));
      candidate.setCurrentAddress((String)data.get("currentAddress"));
      Object planIdObj = data.get("planId");
      if (planIdObj instanceof Number) {
         candidate.setPlanId(((Number)planIdObj).longValue());
      }

      candidate.setPlanName((String)data.get("planName"));
      candidate.setOrgUnitName((String)data.get("orgUnitName"));
      candidate.setRejectionReason((String)data.get("rejectionReason"));
      Object wy = data.get("workYears");
      if (wy instanceof Number) {
         candidate.setWorkYears(((Number)wy).doubleValue());
      }

      candidate.setExpectedSalary((String)data.get("expectedSalary"));
      candidate.setAvailableDate((String)data.get("availableDate"));
      candidate.setEmergencyContact((String)data.get("emergencyContact"));
      candidate.setEmergencyPhone((String)data.get("emergencyPhone"));
      Object sfi = data.get("scoreFirstInterview");
      if (sfi instanceof Number) {
         candidate.setScoreFirstInterview(((Number)sfi).doubleValue());
      }

      Object ssi = data.get("scoreSecondInterview");
      if (ssi instanceof Number) {
         candidate.setScoreSecondInterview(((Number)ssi).doubleValue());
      }

      candidate.setInterviewerFirst((String)data.get("interviewerFirst"));
      candidate.setInterviewerSecond((String)data.get("interviewerSecond"));
      candidate.setCommentFirstInterview((String)data.get("commentFirstInterview"));
      candidate.setCommentSecondInterview((String)data.get("commentSecondInterview"));
      candidate.setScreeningResult((String)data.get("screeningResult"));
      candidate.setInterviewSchedule((String)data.get("interviewSchedule"));
      candidate.setMedicalResult((String)data.get("medicalResult"));
      candidate.setBackgroundCheckResult((String)data.get("backgroundCheckResult"));
      candidate.setHireConclusion((String)data.get("hireConclusion"));
      candidate.setCertificates((String)data.get("certificates"));
      candidate.setAwards((String)data.get("awards"));
      candidate.setSelfEvaluation((String)data.get("selfEvaluation"));
      List<Map<String, Object>> workList = (List)data.get("workExperiences");
      if (workList != null && !workList.isEmpty()) {
         List<WorkExperience> workExperiences = (List)workList.stream().filter((w) -> w.get("companyName") != null && !this.getString(w.get("companyName")).trim().isEmpty()).map((w) -> {
            WorkExperience work = new WorkExperience();
            work.setCompanyName(this.getString(w.get("companyName")));
            work.setPosition(this.getString(w.get("position")));
            Object sd = w.get("startDate");
            if (sd != null) {
               work.setStartDate(this.parseDate(sd.toString()));
            }

            Object ed = w.get("endDate");
            if (ed != null) {
               work.setEndDate(this.parseDate(ed.toString()));
            }

            work.setDescription(this.getString(w.get("description")));
            work.setAttachmentPath(this.getString(w.get("attachmentPath")));
            return work;
         }).collect(Collectors.toList());
         if (!workExperiences.isEmpty()) {
            candidate.setWorkExperiences(workExperiences);
         }
      }

      List<Map<String, Object>> projectList = (List)data.get("projectExperiences");
      if (projectList != null && !projectList.isEmpty()) {
         List<ProjectExperience> projectExperiences = (List)projectList.stream().filter((p) -> p.get("projectName") != null && !this.getString(p.get("projectName")).trim().isEmpty()).map((p) -> {
            ProjectExperience project = new ProjectExperience();
            project.setProjectName(this.getString(p.get("projectName")));
            project.setRole(this.getString(p.get("role")));
            project.setProjectStart(this.getString(p.get("projectStart")));
            project.setProjectEnd(this.getString(p.get("projectEnd")));
            project.setDescription(this.getString(p.get("description")));
            project.setAchievement(this.getString(p.get("achievement")));
            project.setAttachmentPath(this.getString(p.get("attachmentPath")));
            return project;
         }).collect(Collectors.toList());
         if (!projectExperiences.isEmpty()) {
            candidate.setProjectExperiences(projectExperiences);
         }
      }

      return candidate;
   }

   private String getString(Object obj) {
      return obj != null ? obj.toString() : null;
   }

   /**
    * 将数据库保存的上传相对路径（/uploads/...）解析为实际文件，兼容 Windows/绝对路径
    */
   private File resolveUploadFile(String filePath) {
      if (filePath == null || filePath.trim().isEmpty()) return new File(filePath == null ? "" : filePath);
      String p = filePath.trim();
      if (p.startsWith("/uploads/")) {
         return new File(System.getProperty("user.dir") + File.separator + p.substring(1).replace("/", File.separator));
      }
      return new File(p);
   }

   /**
    * 容错解析日期：支持 yyyy-MM-dd / yyyy-MM / yyyy，解析失败返回 null
    */
   private LocalDate parseDate(String str) {
      if (str == null) return null;
      String s = str.trim();
      if (s.isEmpty()) return null;
      try {
         if (s.length() == 10) return LocalDate.parse(s);
         if (s.length() == 7) return LocalDate.parse(s + "-01");
         if (s.length() == 4) return LocalDate.parse(s + "-01-01");
         // 其他长度尝试标准格式，失败则返回 null
         return LocalDate.parse(s);
      } catch (Exception e) {
         return null;
      }
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
      dto.setAppliedPosition(candidate.getAppliedPosition());
      dto.setResumeSource(candidate.getResumeSource());
      dto.setResumeFilePath(candidate.getResumeFilePath());
      dto.setStatus(candidate.getStatus());
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
            WorkExperienceDTO dtoWork = new WorkExperienceDTO();
            dtoWork.setId(w.getId());
            dtoWork.setCandidateId(w.getCandidateId());
            dtoWork.setCompanyName(w.getCompanyName());
            dtoWork.setPosition(w.getPosition());
            dtoWork.setStartDate(w.getStartDate() != null ? w.getStartDate().toString() : null);
            dtoWork.setEndDate(w.getEndDate() != null ? w.getEndDate().toString() : null);
            dtoWork.setDescription(w.getDescription());
            dtoWork.setAttachmentPath(w.getAttachmentPath());
            return dtoWork;
         }).collect(Collectors.toList()));
      }

      if (candidate.getProjectExperiences() != null) {
         dto.setProjectExperiences((List)candidate.getProjectExperiences().stream().map((p) -> {
            ProjectExperienceDTO dtoProject = new ProjectExperienceDTO();
            dtoProject.setId(p.getId());
            dtoProject.setCandidateId(p.getCandidateId());
            dtoProject.setProjectName(p.getProjectName());
            dtoProject.setProjectStart(p.getProjectStart());
            dtoProject.setProjectEnd(p.getProjectEnd());
            dtoProject.setRole(p.getRole());
            dtoProject.setDescription(p.getDescription());
            dtoProject.setAchievement(p.getAchievement());
            dtoProject.setAttachmentPath(p.getAttachmentPath());
            return dtoProject;
         }).collect(Collectors.toList()));
      }

      return dto;
   }

   @PostMapping({"/import"})
   @Log(
      module = "候选人",
      description = "批量导入候选人"
   )
   public Map<String, Object> importCandidates(@RequestParam("file") MultipartFile file, HttpSession session) {
      Map<String, Object> result = new HashMap();

      try {
         UserDTO user = PermissionUtil.getCurrentUser(session);
         PermissionUtil.requireRole(user, "admin", "manager");
         if (file == null || file.isEmpty()) {
            result.put("success", false);
            result.put("message", "请选择要导入的文件");
            return result;
         }

         String originalFilename = file.getOriginalFilename();
         if (originalFilename == null) {
            result.put("success", false);
            result.put("message", "文件名不能为空");
            return result;
         }

         String fileExtension = originalFilename.toLowerCase();
         if (!fileExtension.endsWith(".xls") && !fileExtension.endsWith(".xlsx")) {
            result.put("success", false);
            result.put("message", "只支持 .xls 或 .xlsx 格式的 Excel 文件");
            return result;
         }

         final CandidateExcelListener listener = new CandidateExcelListener(this.candidateRepository);
         if (fileExtension.endsWith(".xlsx")) {
            ((ExcelReaderSheetBuilder)EasyExcel.read(file.getInputStream(), CandidateImportRow.class, listener).autoCloseStream(true).sheet(0).headRowNumber(1)).doRead();
         } else if (fileExtension.endsWith(".xls")) {
            ((ExcelReaderSheetBuilder)EasyExcel.read(file.getInputStream(), CandidateImportRow.class, listener).autoCloseStream(true).useDefaultListener(true).sheet(0).headRowNumber(1)).doRead();
         }

         result.put("success", true);
         result.put("data", new HashMap<String, Object>() {
            {
               this.put("total", listener.getSuccessCount());
               this.put("errors", listener.getErrors());
            }
         });
         int var10002 = listener.getSuccessCount();
         result.put("message", "成功导入 " + var10002 + " 条记录" + (listener.getErrors().isEmpty() ? "" : "，部分数据跳过：" + listener.getErrors()));
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", "导入失败：" + e.getMessage());
         e.printStackTrace();
      }

      return result;
   }

   @PostMapping({"/refresh-version"})
   public Map<String, Object> refreshFileVersion(@RequestBody Map<String, Long> params, HttpSession session) {
      Map<String, Object> result = new HashMap();

      try {
         UserDTO user = PermissionUtil.getCurrentUser(session);
         PermissionUtil.requireRole(user, "admin", "manager");
         Long candidateId = (Long)params.get("candidateId");
         this.candidateService.recordFileVersionManually(candidateId);
         result.put("success", true);
         result.put("message", "简历版本记录已触发");
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", "操作失败：" + e.getMessage());
      }

      return result;
   }

   @GetMapping({"/debug/{id}"})
   public Map<String, Object> debugCandidate(@PathVariable Long id) {
      Map<String, Object> result = new HashMap();

      try {
         final Candidate candidate = (Candidate)this.candidateRepository.findById(id).orElseThrow(() -> new RuntimeException("候选人不存在：" + id));
         final File file = candidate.getResumeFilePath() != null ? new File(candidate.getResumeFilePath()) : null;
         result.put("success", true);
         result.put("data", new HashMap<String, Object>() {
            {
               this.put("id", candidate.getId());
               this.put("candidateNo", candidate.getCandidateNo());
               this.put("realName", candidate.getRealName());
               this.put("resumeFilePath", candidate.getResumeFilePath());
               this.put("fileExists", file != null && file.exists());
               this.put("fileSize", file != null ? file.length() : 0L);
            }
         });
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }

   @GetMapping({"/public/{id}"})
   public Map<String, Object> getCandidatePublic(@PathVariable Long id) {
      Map<String, Object> result = new HashMap();

      try {
         final Candidate candidate = (Candidate)this.candidateRepository.findById(id).orElseThrow(() -> new RuntimeException("候选人不存在：" + id));
         result.put("success", true);
         result.put("data", new HashMap<String, Object>() {
            {
               this.put("id", candidate.getId());
               this.put("candidateNo", candidate.getCandidateNo());
               this.put("realName", candidate.getRealName());
               this.put("resumeFilePath", candidate.getResumeFilePath());
            }
         });
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }
}
