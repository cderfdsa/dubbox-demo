package com.cyf.shop.business.auth.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.shop.auth.bean.Role;
import com.cyf.shop.auth.bean.RoleHasAuthority;
import com.cyf.shop.auth.bean.RoleUser;
import com.cyf.shop.auth.service.RoleService;
import com.cyf.shop.auth.vo.RoleAssignAuthVo;
import com.cyf.shop.business.auth.biz.RoleBiz;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by chenyf on 2017/4/9.
 */
@Service
public class RoleServiceImpl implements RoleService{
    @Autowired
    RoleBiz roleBiz;

    public BaseResponse<Role> createRole(Role role){
        return roleBiz.createRole(role);
    }

    public BaseResponse updateRole(Role role){
        return roleBiz.updateRole(role);
    }

    /**
     * 删除角色、角色-用户关联记录、角色-权限关联记录
     * @param roleId
     * @return
     */
    public BaseResponse deleteRoleWithRelate(Long roleId){
        return roleBiz.deleteWithRelate(roleId);
    }

    public List<Role> listRoleByCond(Map<String, Object> paramMap){
        return roleBiz.listByCond(paramMap);
    }

    public Role getRoleById(Long id){
        return roleBiz.getRoleById(id);
    }

    public List<RoleUser> listRoleUserByRoleId(Long roleId){
        return roleBiz.listRoleUserByRoleId(roleId);
    }

    public List<RoleUser> listRoleUserByUserId(Long userId){
        return roleBiz.listRoleUserByUserId(userId);
    }

    public boolean refreshRoleUser(Long roleId, List<RoleUser> newRoleUserList){
        return roleBiz.refreshRoleUser(roleId, newRoleUserList);
    }

    public List<RoleHasAuthority> listRoleHasAuthorityByRoleId(Long roleId){
        return roleBiz.listRoleHasAuthorityByRoleId(roleId);
    }

    public BaseResponse refreshRoleHasAuthority(Long roleId, List<Long> newAuthIdList){
        return roleBiz.refreshRoleHasAuthority(roleId, newAuthIdList);
    }

    public List<RoleAssignAuthVo> listRoleAssignAuth(Long roleId){
        return roleBiz.listRoleAssignAuth(roleId);
    }
}
