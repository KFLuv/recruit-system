package com.recruitment.controller;

import com.recruitment.dto.ApprovalDTO;
import com.recruitment.dto.UserDTO;
import com.recruitment.entity.ProcessApproval;
import com.recruitment.service.ProcessApprovalService;
import com.recruitment.util.PermissionUtil;
import com.recruitment.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approvals")
public class ProcessApprovalController {

    @Autowired
    private ProcessApprovalService processApprovalService;

    @PostMapping
    @Log(module = "审批管理", description = "创建审批")
    public Map<String, Object> createApproval(@RequestBody Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");
            
            String processType = (String) params.get("processType");
            Long processId = Long.parseLong(params.get("processId").toString());
            Long creatorId = Long.parseLong(params.get("creatorId").toString());
            
            ProcessApproval approval = processApprovalService.createApproval(processType, processId, creatorId);
            result.put("success", true);
            result.put("data", approval);
            result.put("message", "创建审批流程成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/all/{processType}")
    public Map<String, Object> getAllApprovals(@PathVariable String processType) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ApprovalDTO> approvals = processApprovalService.getAllApprovals(processType);
            result.put("success", true);
            result.put("data", approvals);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/pending/{processType}")
    public Map<String, Object> getPendingApprovals(@PathVariable String processType) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ApprovalDTO> approvals = processApprovalService.getPendingApprovals(processType);
            result.put("success", true);
            result.put("data", approvals);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getApprovalById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            ProcessApproval approval = processApprovalService.getApprovalById(id);
            result.put("success", true);
            result.put("data", approval);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/{id}/approve")
    @Log(module = "审批管理", description = "审批通过")
    public Map<String, Object> approve(@PathVariable Long id, @RequestBody Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");
            
            Long approverId = Long.parseLong(params.get("approverId").toString());
            String comment = (String) params.get("comment");
            
            ProcessApproval approval = processApprovalService.approve(id, approverId, comment);
            result.put("success", true);
            result.put("data", approval);
            result.put("message", "审批通过");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/{id}/reject")
    @Log(module = "审批管理", description = "审批驳回")
    public Map<String, Object> reject(@PathVariable Long id, @RequestBody Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");
            
            Long approverId = Long.parseLong(params.get("approverId").toString());
            String comment = (String) params.get("comment");
            
            ProcessApproval approval = processApprovalService.reject(id, approverId, comment);
            result.put("success", true);
            result.put("data", approval);
            result.put("message", "审批驳回");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/{id}")
    @Log(module = "审批管理", description = "取消审批")
    public Map<String, Object> deleteApproval(@PathVariable Long id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");
            
            processApprovalService.cancelApproval(id);
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
