package com.recruitment.controller;

import com.recruitment.entity.OrgUnit;
import com.recruitment.repository.OrgUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/org-units")
public class OrgUnitController {

    @Autowired
    private OrgUnitRepository orgUnitRepository;

    @GetMapping
    public Map<String, Object> getAllUnits() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<OrgUnit> units = orgUnitRepository.findAll();
            result.put("success", true);
            result.put("data", units);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping
    public Map<String, Object> createUnit(@RequestBody OrgUnit unit) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (unit.getLevel() == null) {
                unit.setLevel(1);
            }
            if (unit.getSortOrder() == null) {
                unit.setSortOrder(0);
            }
            if (unit.getStatus() == null) {
                unit.setStatus(1);
            }
            
            OrgUnit saved = orgUnitRepository.save(unit);
            result.put("success", true);
            result.put("data", saved);
            result.put("message", "创建成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateUnit(@PathVariable Long id, @RequestBody OrgUnit unit) {
        Map<String, Object> result = new HashMap<>();
        try {
            OrgUnit existing = orgUnitRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("部门不存在"));
            
            existing.setOrgName(unit.getOrgName());
            existing.setOrgCode(unit.getOrgCode());
            existing.setOrgType(unit.getOrgType());
            existing.setParentId(unit.getParentId());
            existing.setLevel(unit.getLevel());
            existing.setSortOrder(unit.getSortOrder());
            
            OrgUnit updated = orgUnitRepository.save(existing);
            result.put("success", true);
            result.put("data", updated);
            result.put("message", "更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUnit(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            orgUnitRepository.deleteById(id);
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/active")
    public Map<String, Object> getActiveUnits() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<OrgUnit> units = orgUnitRepository.findByStatus(1);
            result.put("success", true);
            result.put("data", units);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
