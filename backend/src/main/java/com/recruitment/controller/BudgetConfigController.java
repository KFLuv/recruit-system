package com.recruitment.controller;

import com.recruitment.entity.AnnualBudgetConfig;
import com.recruitment.repository.AnnualBudgetConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/budget-configs")
public class BudgetConfigController {

    @Autowired
    private AnnualBudgetConfigRepository budgetConfigRepository;

    @GetMapping
    public Map<String, Object> listBudgetConfigs() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<AnnualBudgetConfig> configs = budgetConfigRepository.findAll();
            List<Map<String, Object>> dtoList = new java.util.ArrayList<>();
            for (AnnualBudgetConfig config : configs) {
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", config.getId());
                dto.put("configYear", config.getConfigYear());
                dto.put("orgUnitId", config.getOrgUnitId());
                dto.put("orgUnitName", getOrgUnitName(config.getOrgUnitId()));
                dto.put("headcountLimit", config.getHeadcountLimit());
                dto.put("budgetLimit", config.getBudgetLimit());
                dtoList.add(dto);
            }
            result.put("success", true);
            result.put("data", dtoList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getBudgetConfig(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Optional<AnnualBudgetConfig> opt = budgetConfigRepository.findById(id);
            if (opt.isPresent()) {
                AnnualBudgetConfig config = opt.get();
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", config.getId());
                dto.put("configYear", config.getConfigYear());
                dto.put("orgUnitId", config.getOrgUnitId());
                dto.put("orgUnitName", getOrgUnitName(config.getOrgUnitId()));
                dto.put("headcountLimit", config.getHeadcountLimit());
                dto.put("budgetLimit", config.getBudgetLimit());
                result.put("success", true);
                result.put("data", dto);
            } else {
                result.put("success", false);
                result.put("message", "Config not found");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping
    public Map<String, Object> createBudgetConfig(@RequestBody AnnualBudgetConfig config) {
        Map<String, Object> result = new HashMap<>();
        try {
            // Check if config already exists
            boolean exists = budgetConfigRepository.findByConfigYearAndOrgUnitId(
                    config.getConfigYear(), config.getOrgUnitId())
                    .isPresent();
            if (exists) {
                result.put("success", false);
                result.put("message", "Config already exists for this year and org");
                return result;
            }
            AnnualBudgetConfig saved = budgetConfigRepository.save(config);
            result.put("success", true);
            result.put("data", saved);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateBudgetConfig(@PathVariable Long id, 
                                                   @RequestBody AnnualBudgetConfig config) {
        Map<String, Object> result = new HashMap<>();
        try {
            Optional<AnnualBudgetConfig> opt = budgetConfigRepository.findById(id);
            if (opt.isPresent()) {
                AnnualBudgetConfig existing = opt.get();
                existing.setConfigYear(config.getConfigYear());
                existing.setOrgUnitId(config.getOrgUnitId());
                existing.setHeadcountLimit(config.getHeadcountLimit());
                existing.setBudgetLimit(config.getBudgetLimit());
                AnnualBudgetConfig saved = budgetConfigRepository.save(existing);
                result.put("success", true);
                result.put("data", saved);
            } else {
                result.put("success", false);
                result.put("message", "Config not found");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteBudgetConfig(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            budgetConfigRepository.deleteById(id);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    private String getOrgUnitName(Long orgUnitId) {
        if (orgUnitId == null) return null;
        return "Unit " + orgUnitId;
    }
}
