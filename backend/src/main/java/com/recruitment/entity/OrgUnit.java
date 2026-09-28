package com.recruitment.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "org_unit")
public class OrgUnit extends BaseEntity {

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "org_name", nullable = false)
    private String orgName;

    @Column(name = "org_code")
    private String orgCode;

    @Column(name = "org_type")
    private String orgType;

    private Integer level;

    @Column(name = "sort_order")
    private Integer sortOrder;

    private Integer status = 1;

    @OneToMany(mappedBy = "orgUnit")
    private List<User> users;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getOrgCode() { return orgCode; }
    public void setOrgCode(String orgCode) { this.orgCode = orgCode; }
    public String getOrgType() { return orgType; }
    public void setOrgType(String orgType) { this.orgType = orgType; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
}
