package com.cyf.shop.auth.vo;

/**
 * @Title：角色分配权限的vo
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/5/3 13:21
 */
public class RoleAssignAuthVo implements java.io.Serializable{
    private static final long serialVersionUID = 1L;

    //权限id
    private Long authId;
    //权限名称
    private String name;
    //父权限id
    private Long parentId;
    //权限检查等级
    private Integer checkLevel;
    //已分配的角色Id
    private String assignedRoleIds;
    //当前要判断的角色id
    private Long roleId;
    //是否已绑定当前角色id
    private Boolean isAssignedRoleId;

    public Long getAuthId() {
        return authId;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getCheckLevel() {
        return checkLevel;
    }

    public void setCheckLevel(Integer checkLevel) {
        this.checkLevel = checkLevel;
    }

    public String getAssignedRoleIds() {
        return assignedRoleIds;
    }

    public void setAssignedRoleIds(String assignedRoleIds) {
        this.assignedRoleIds = assignedRoleIds;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Boolean getIsAssignedRoleId() {
        return isAssignedRoleId;
    }

    public void setIsAssignedRoleId(Boolean assignedRoleId) {
        isAssignedRoleId = assignedRoleId;
    }
}
