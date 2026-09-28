package com.recruitment.repository;

import com.recruitment.entity.OrgUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrgUnitRepository extends JpaRepository<OrgUnit, Long> {
    List<OrgUnit> findByStatus(Integer status);
}
