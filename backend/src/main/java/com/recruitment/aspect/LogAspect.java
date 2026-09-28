package com.recruitment.aspect;

import com.recruitment.annotation.Log;
import com.recruitment.dto.CandidateDTO;
import com.recruitment.dto.RecruitPlanDTO;
import com.recruitment.dto.UserDTO;
import com.recruitment.entity.ProcessApproval;
import com.recruitment.entity.SystemLog;
import com.recruitment.repository.SystemLogRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LogAspect {
   @Autowired
   private SystemLogRepository logRepository;

   @Around("@annotation(com.recruitment.annotation.Log)")
   public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
      long start = System.currentTimeMillis();
      MethodSignature signature = (MethodSignature)joinPoint.getSignature();
      Log logAnno = (Log)signature.getMethod().getAnnotation(Log.class);
      ServletRequestAttributes attrs = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
      HttpServletRequest request = attrs != null ? attrs.getRequest() : null;
      String username = "anonymous";
      String userRole = "";
      if (request != null) {
         HttpSession session = request.getSession(false);
         if (session != null) {
            UserDTO user = (UserDTO)session.getAttribute("user");
            if (user != null) {
               username = user.getUsername();
               userRole = user.getRole();
            }
         }
      }

      SystemLog sysLog = new SystemLog();
      sysLog.setUsername(username);
      sysLog.setUserRole(userRole);
      sysLog.setModule(logAnno.module());
      if (request != null) {
         sysLog.setMethod(request.getMethod());
         sysLog.setRequestUrl(request.getRequestURI());
         sysLog.setIp(this.getIp(request));
      }

      Object[] args = joinPoint.getArgs();
      if (args != null && args.length > 0) {
         String params = Arrays.toString(args);
         if (params.length() > 500) {
            params = params.substring(0, 500) + "...";
         }

         sysLog.setRequestParams(params);
      }

      try {
         Object result = joinPoint.proceed();
         long duration = System.currentTimeMillis() - start;
         sysLog.setDuration(duration);
         sysLog.setResult("success");
         sysLog.setDescription(logAnno.description());
         sysLog.setBizNo(this.extractBizNo(result, args));
         this.logRepository.save(sysLog);
         return result;
      } catch (Throwable e) {
         long duration = System.currentTimeMillis() - start;
         sysLog.setDuration(duration);
         sysLog.setResult("fail");
         sysLog.setErrorMsg(e.getMessage());
         sysLog.setDescription(logAnno.description());
         sysLog.setBizNo(this.extractBizNo(null, args));
         this.logRepository.save(sysLog);
         throw e;
      }
   }

   /**
    * 提取业务编号（计划编号/候选人编号/审批流程ID），供日志 bizNo 列使用
    */
   private String extractBizNo(Object result, Object[] args) {
      String bizNo = this.extractBusinessNo(result);
      return bizNo != null ? bizNo : this.extractIdFromArgs(args);
   }

   /**
    * 从接口返回结果中提取业务编号
    */
   private String extractBusinessNo(Object result) {
      if (result instanceof Map) {
         Object data = ((Map<?, ?>) result).get("data");
         if (data != null) {
            return extractNoFromObject(data);
         }
      }
      return null;
   }

   private String extractNoFromObject(Object obj) {
      if (obj instanceof RecruitPlanDTO) {
         return ((RecruitPlanDTO) obj).getPlanNo();
      }
      if (obj instanceof CandidateDTO) {
         return ((CandidateDTO) obj).getCandidateNo();
      }
      if (obj instanceof ProcessApproval) {
         ProcessApproval pa = (ProcessApproval) obj;
         return pa.getProcessId() != null ? "审批ID:" + pa.getProcessId() : null;
      }
      if (obj instanceof Map) {
         Object planNo = ((Map<?, ?>) obj).get("planNo");
         if (planNo != null) return planNo.toString();
         Object candidateNo = ((Map<?, ?>) obj).get("candidateNo");
         if (candidateNo != null) return candidateNo.toString();
         Object processId = ((Map<?, ?>) obj).get("processId");
         if (processId != null) return "审批ID:" + processId;
      }
      return null;
   }

   /**
    * 从方法参数中提取 id（如 @PathVariable Long id），用于删除/审批等无返回数据的操作
    */
   private String extractIdFromArgs(Object[] args) {
      if (args == null) return null;
      for (Object arg : args) {
         if (arg instanceof Long) {
            return "ID:" + arg;
         }
         if (arg instanceof List && !((List<?>) arg).isEmpty() && ((List<?>) arg).get(0) instanceof Long) {
            List<?> list = (List<?>) arg;
            return "批量" + list.size() + "个(ID:" + list.get(0) + "等)";
         }
      }
      return null;
   }

   private String getIp(HttpServletRequest request) {
      String ip = request.getHeader("X-Forwarded-For");
      if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
         ip = request.getRemoteAddr();
      }

      // 多级代理时取第一个真实IP
      if (ip != null && ip.contains(",")) {
         ip = ip.split(",")[0].trim();
      }

      // IPv6 回环地址统一转换为 127.0.0.1
      if (ip != null && ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip))) {
         ip = "127.0.0.1";
      }

      return ip;
   }
}
