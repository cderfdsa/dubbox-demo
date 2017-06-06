package com.cyf.shop.auth.vo;

/**
 * Created by Administrator on 2017/5/1.
 */
public class AuthAssignNodeVo implements java.io.Serializable{

    private static final long serialVersionUID = 1L;

    //节点id
    private Long nodeId;
    //节点名称
    private String name;
    //父节点id
    private Long parentId;
    //是否独占(0=否 1=是,不允许分配给多个权限)
    private Integer exclusive;
    //已分配的权限Id
    private String assignedAuthIds;
    //当前要判断的权限id
    private Long authId;
    //是否已绑定当前权限id
    private Boolean isAssignedAuthId;

    public Long getAuthId() {
        return authId;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
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

    public Integer getExclusive() {
        return exclusive;
    }

    public void setExclusive(Integer exclusive) {
        this.exclusive = exclusive;
    }

    public String getAssignedAuthIds() {
        return assignedAuthIds;
    }

    public void setAssignedAuthIds(String assignedAuthIds) {
        this.assignedAuthIds = assignedAuthIds;
    }

    public void setIsAssignedAuthId(Boolean isAssignedAuthId) {
        this.isAssignedAuthId = isAssignedAuthId;
    }

    public Boolean  getIsAssignedAuthId() {
        return isAssignedAuthId;
    }
}
