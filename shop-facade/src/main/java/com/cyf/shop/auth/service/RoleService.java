package com.cyf.shop.auth.service;

import com.cyf.base.common.bean.BaseResponse;
import com.cyf.shop.auth.bean.Role;
import com.cyf.shop.auth.bean.RoleHasAuthority;
import com.cyf.shop.auth.bean.RoleUser;
import com.cyf.shop.auth.vo.RoleAssignAuthVo;

import java.util.List;
import java.util.Map;

/**
 * 角色服务
 * Created by chenyf on 2017/4/9.
 */
public interface RoleService {
    BaseResponse<Role> createRole(Role role);

    BaseResponse updateRole(Role role);

    /**
     * 删除角色、角色-用户关联记录、角色-权限关联记录
     * @param roleId
     * @return
     */
    BaseResponse deleteRoleWithRelate(Long roleId);

    List<Role> listRoleByCond(Map<String, Object> paramMap);

    Role getRoleById(Long id);

    public List<RoleUser> listRoleUserByRoleId(Long roleId);

    public List<RoleUser> listRoleUserByUserId(Long userId);

    public boolean refreshRoleUser(Long roleId, List<RoleUser> newRoleUserList);

    public List<RoleHasAuthority> listRoleHasAuthorityByRoleId(Long roleId);

    public BaseResponse refreshRoleHasAuthority(Long roleId, List<Long> newAuthIdList);

    public List<RoleAssignAuthVo> listRoleAssignAuth(Long roleId);
}
