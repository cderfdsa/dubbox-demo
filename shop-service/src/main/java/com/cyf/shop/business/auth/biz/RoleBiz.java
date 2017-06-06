package com.cyf.shop.business.auth.biz;

import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.enums.DeleteEnum;
import com.cyf.base.common.enums.StatusEnum;
import com.cyf.base.common.utils.StringUtil;
import com.cyf.shop.auth.bean.Role;
import com.cyf.shop.auth.bean.RoleHasAuthority;
import com.cyf.shop.auth.bean.RoleUser;
import com.cyf.shop.auth.vo.RoleAssignAuthVo;
import com.cyf.shop.business.auth.dao.RoleDao;
import com.cyf.shop.business.auth.dao.RoleHasAuthorityDao;
import com.cyf.shop.business.auth.dao.RoleUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by chenyf on 2017/4/9.
 */
@Component
public class RoleBiz {
    @Autowired
    RoleHasAuthorityDao roleHasAuthorityDao;
    @Autowired
    RoleUserDao roleUserDao;
    @Autowired
    RoleDao roleDao;

    public BaseResponse<Role> createRole(Role role){
        if(role==null){
            return BaseResponse.fail("要创建的角色对象不能为空");
        }else if(StringUtil.isEmpty(role.getName())){
            return BaseResponse.fail("角色名称不能为空");
        }

        Map<String, Object> param = new HashMap<>();
        param.put("name", role.getName());
        List<Role> roleList = listByCond(param);
        if(roleList.size() > 0){
            return BaseResponse.fail("角色名称:"+role.getName()+"已经存在！");
        }

        role.setType(1);//不允许添加超级管理员
        role.setCreateTime(new Date());
        role.setUpdateTime(new Date());
        if(role.getStatus()==null) role.setStatus(StatusEnum.ACTIVE.getValue());
        if(role.getRemark()==null) role.setRemark("");
        boolean result = roleDao.insert(role) > 0;
        if(result){
            return BaseResponse.success(role);
        }else{
            return BaseResponse.fail("创建角色失败！");
        }
    }

    public BaseResponse updateRole(Role role){
        if(role==null){
            return BaseResponse.fail("要更新的角色对象不能为空");
        }else if(role.getId()==null){
            return BaseResponse.fail("角色Id不能为空");
        }

        Role oldRole = getRoleById(role.getId());
        if(oldRole == null){
            return BaseResponse.fail("角色不存在");
        }else if(! StringUtil.isEmpty(role.getName()) && !role.getName().trim().equals(oldRole.getName())){
            Map<String, Object> param = new HashMap<>();
            param.put("name", role.getName());
            List<Role> roleList = listByCond(param);
            if(roleList.size() > 0){
                return BaseResponse.fail("角色名称:"+role.getName()+"已经存在！");
            }
        }
        if(! StringUtil.isEmpty(role.getName())) oldRole.setName(role.getName());
        if(role.getStatus()!=null) oldRole.setStatus(role.getStatus());
        if(role.getRemark()!=null) oldRole.setRemark(role.getRemark());
        oldRole.setUpdateTime(new Date());
        if(roleDao.update(oldRole) > 0){
            return BaseResponse.success();
        }else{
            return BaseResponse.fail("更新角色失败");
        }
    }

    /**
     * 删除角色、角色-用户关联记录、角色-权限关联记录
     * @param roleId
     * @return
     */
    public BaseResponse deleteWithRelate(Long roleId){
        Role role = getRoleById(roleId);
        if(role == null){
            return BaseResponse.fail("角色不存在");
        }

        List<Long> roleUserIdList = new ArrayList<>();
        List<Long> roleHasAuthorityIdList = new ArrayList<>();

        List<RoleUser> roleUserList = listRoleUserByRoleId(roleId);
        for(RoleUser roleUser : roleUserList){
            roleUserIdList.add(roleUser.getId());
        }

        List<RoleHasAuthority> roleHasAuthorityList = listRoleHasAuthorityByRoleId(roleId);
        for(RoleHasAuthority roleHasAuthority : roleHasAuthorityList){
            roleHasAuthorityIdList.add(roleHasAuthority.getId());
        }

        boolean result = roleDao.deleteById(roleId) > 0;
        if(result && roleUserIdList.size() > 0){
            batchDeleteRoleUser(roleUserIdList);
        }
        if(result && roleHasAuthorityIdList.size() > 0){
            batchDeleteRoleAuthority(roleHasAuthorityIdList);
        }
        if(result){
            return BaseResponse.success();
        }else{
            return BaseResponse.fail("删除角色及其关联记录失败");
        }
    }

    public List<Role> listByCond(Map<String, Object> paramMap){
        return roleDao.listByCond(paramMap);
    }

    public Role getRoleById(Long id){
        return roleDao.getById(id);
    }

    public boolean batchDeleteRoleUser(List<Long> roleUserIdList){
        if(roleUserIdList!=null && ! roleUserIdList.isEmpty()){
            return roleUserDao.deleteByIdList(roleUserIdList) > 0;
        }else{
            return false;
        }
    }

    public boolean batchDeleteRoleAuthority(List<Long> roleAuthorityIdList){
        if(roleAuthorityIdList!=null && ! roleAuthorityIdList.isEmpty()){
            return roleHasAuthorityDao.deleteByIdList(roleAuthorityIdList) > 0;
        }else{
            return false;
        }
    }

    public List<RoleUser> listRoleUserByRoleId(Long roleId){
        Map<String, Object> filter = new HashMap<>();
        filter.put("delete", DeleteEnum.UNDELETED.getValue());
        filter.put("fkRoleId", roleId);
        return roleUserDao.listByCond(filter);
    }

    public List<RoleUser> listRoleUserByUserId(Long fkUserId){
        return roleUserDao.listBy("listRoleUserByUserId", fkUserId);
    }

    public List<RoleHasAuthority> listRoleHasAuthorityByRoleId(Long roleId){
        Map<String, Object> filter = new HashMap<>();
        filter.put("fkRoleId", roleId);
        return roleHasAuthorityDao.listByCond(filter);
    }

    public List<RoleHasAuthority> listRoleHasAuthorityByRoleIdAndAuthorityIdList(Long roleId, List<Long> authorityIdList) {
        if(roleId == null) return null;

        List<Long> roleIdList = new ArrayList<>();
        roleIdList.add(roleId);

        Map<String, Object> filter = new HashMap<>();
        filter.put("fkRoleIdList", roleIdList);
        if(authorityIdList != null && ! authorityIdList.isEmpty()){
            filter.put("fkAuthorityIdList", authorityIdList);
        }
        return roleHasAuthorityDao.listByCond(filter);
    }

    public List<RoleHasAuthority> listRoleHasAuthorityByAuthId(Long authId){
        Map<String, Object> filter = new HashMap<>();
        filter.put("fkAuthorityId", authId);
        return roleHasAuthorityDao.listByCond(filter);
    }

    /**
     * 取得用户的角色-权限关联记录
     * @param userId
     * @return
     */
    public List<RoleHasAuthority> listRoleHasAuthorityByUserId(Long userId){
        if(userId == null) return null;

        //先根据用户id取得用户-角色关联记录
        Map<String, Object> filter = new HashMap<>();
        filter.put("status", StatusEnum.ACTIVE.getValue());
        filter.put("delete", DeleteEnum.UNDELETED.getValue());
        filter.put("fkUserId", userId);
        List<RoleUser> roleUserList = roleUserDao.listByCond(filter);
        if(roleUserList==null || roleUserList.isEmpty()){
            return new ArrayList<>();
        }

        //再根据多个角色id，取得角色-权限关联记录
        List<Long> roleIdList = new ArrayList<>();
        for(RoleUser roleUser : roleUserList){
            roleIdList.add(roleUser.getFkRoleId());
        }
        return listRoleHasAuthorityByRoleIdList(roleIdList);
    }

    public List<RoleHasAuthority> listRoleHasAuthorityByRoleIdList(List<Long> roleIdList){
        if(roleIdList == null || roleIdList.isEmpty()) return null;
        Map<String, Object> filter = new HashMap<>();
        filter.put("fkRoleIdList", roleIdList);
        return roleHasAuthorityDao.listByCond(filter);
    }

    public List<RoleUser> listActiveRoleUserByUserId(Long userId){
        Map<String, Object> filter = new HashMap<>(2);
        filter.put("userId", userId);
        filter.put("status", StatusEnum.ACTIVE.getValue());
        return roleUserDao.listByCond(filter);
    }

    public boolean refreshRoleUser(Long roleId, List<RoleUser> newRoleUserList){
        Map<Long, RoleUser> oldIdMap = new HashMap<>();
        Map<Long, RoleUser> newIdMap = new HashMap<>();
        List<RoleUser> needInsertList = new ArrayList<>();
        List<RoleUser> needUpdateList = new ArrayList<>();
        List<Long> needDeleteIdList = new ArrayList<>();

        List<RoleUser> oldRoleUserList = listRoleUserByRoleId(roleId);
        for(RoleUser roleUser : oldRoleUserList){
            oldIdMap.put(roleUser.getFkUserId(), roleUser);
        }

        if(newRoleUserList != null){
            for(RoleUser roleUser : newRoleUserList){
                if(roleUser.getFkUserId()==null){
                    continue;
                }
                roleUser.setFkRoleId(roleId);
                newIdMap.put(roleUser.getFkUserId(), roleUser);
            }
        }

        if(oldIdMap.isEmpty() && newIdMap.isEmpty()){
            //do nothing
        }else if(oldIdMap.isEmpty() && ! newIdMap.isEmpty()){//全部需要新增
            for(Map.Entry<Long, RoleUser> entry : newIdMap.entrySet()){
                needInsertList.add(entry.getValue());
            }
        }else if(! oldIdMap.isEmpty() && newIdMap.isEmpty()){//全部需要删除
            for(Map.Entry<Long, RoleUser> entry : oldIdMap.entrySet()){
                needDeleteIdList.add(entry.getValue().getId());
            }
        }else{
            for(Map.Entry<Long, RoleUser> entry : oldIdMap.entrySet()){
                if(newIdMap.containsKey(entry.getKey())){//新、旧都有，需要更新
                    RoleUser newRoleUser = newIdMap.get(entry.getKey());

                    newRoleUser.setCreateTime(entry.getValue().getCreateTime());
                    newRoleUser.setUpdateTime(new Date());
                    needUpdateList.add(newRoleUser);
                }else{//旧中有，新中无，需要删除
                    needDeleteIdList.add(entry.getValue().getId());
                }
            }

            for(Map.Entry<Long, RoleUser> entry : newIdMap.entrySet()){
                if(! oldIdMap.containsKey(entry.getKey())){//旧中无，新中有，需要新增
                    RoleUser newRoleUser = entry.getValue();

                    newRoleUser.setCreateTime(new Date());
                    newRoleUser.setUpdateTime(new Date());
                    needInsertList.add(entry.getValue());
                }
            }
        }

        if(needDeleteIdList.size() > 0){
            batchDeleteRoleUser(needDeleteIdList);
        }
        if(needInsertList.size() > 0){
            roleUserDao.insertList(needInsertList);
        }
        if(needUpdateList.size() > 0){
            roleUserDao.updateList(needUpdateList);
        }

        return true;
    }

    public BaseResponse refreshRoleHasAuthority(Long roleId, List<Long> newAuthIdList){
        if(roleId==null) return BaseResponse.fail("角色id为空");
        if(newAuthIdList==null) newAuthIdList = new ArrayList<>();
        List<Long> needDeleteIdList = new ArrayList<>();
        List<RoleHasAuthority> needInsertList = new ArrayList<>();

        //找出当前权限关联的节点
        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("fkRoleId", roleId);
        Map<Long, RoleHasAuthority> oldRoleHasAuthMap = roleHasAuthorityDao.mapByCond(paramMap, "fkAuthorityId");

        Map<Long, String> newAuthIdMap = new HashMap<>();
        for(Long authId : newAuthIdList){
            newAuthIdMap.put(authId, "");
        }

        if(newAuthIdMap.isEmpty() && oldRoleHasAuthMap.isEmpty()){
            //新旧都为空，不做任何处理
        }else if(newAuthIdMap.isEmpty() && !oldRoleHasAuthMap.isEmpty()){//新为空，旧不为空，旧的需要全部删除
            for(Map.Entry<Long, RoleHasAuthority> entry : oldRoleHasAuthMap.entrySet()){
                needDeleteIdList.add(entry.getValue().getId());
            }
        }else if(!newAuthIdMap.isEmpty() && oldRoleHasAuthMap.isEmpty()){//新不为空，旧为空，新的需要全部插入
            for(Map.Entry<Long, String> entry : newAuthIdMap.entrySet()){
                RoleHasAuthority obj = new RoleHasAuthority();
                obj.setFkRoleId(roleId);
                obj.setFkAuthorityId(entry.getKey());
                obj.setCreateTime(new Date());
                obj.setUpdateTime(new Date());
                needInsertList.add(obj);
            }
        }else if(!newAuthIdMap.isEmpty() && !oldRoleHasAuthMap.isEmpty()){//新不为空，旧不为空，需要比较
            for(Map.Entry<Long, RoleHasAuthority> entry : oldRoleHasAuthMap.entrySet()){
                Long nodeId = entry.getKey();
                if(! newAuthIdMap.containsKey(nodeId)){//旧中有，新中无，需要删除
                    needDeleteIdList.add(entry.getValue().getId());
                }else{
                    //新、旧都有，不做处理
                }
            }

            for(Map.Entry<Long, String> entry : newAuthIdMap.entrySet()){//旧中无，新中有，需要新增
                RoleHasAuthority obj = new RoleHasAuthority();
                obj.setFkAuthorityId(entry.getKey());
                obj.setFkRoleId(roleId);
                obj.setCreateTime(new Date());
                obj.setUpdateTime(new Date());
                needInsertList.add(obj);
            }
        }

        if(needDeleteIdList.size() > 0) roleHasAuthorityDao.deleteByIdList(needDeleteIdList);
        if(needInsertList.size() > 0) roleHasAuthorityDao.insertList(needInsertList);
        return BaseResponse.success();
    }


    public List<RoleAssignAuthVo> listRoleAssignAuth(Long roleId){
        //找出当前权限关联的节点
        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("fkRoleId", roleId);
        Map<Long, RoleHasAuthority> roleHasAuthMap = roleHasAuthorityDao.mapByCond(paramMap, "fkAuthorityId");

        //找出所有有效的节点
        List<RoleAssignAuthVo> voList = roleHasAuthorityDao.listBy("listRoleAssignAuth",null);

        //封装Vo返回
        for(RoleAssignAuthVo vo : voList){
            vo.setRoleId(roleId);
            if(roleHasAuthMap.containsKey(vo.getAuthId())){
                vo.setIsAssignedRoleId(true);
            }else{
                vo.setIsAssignedRoleId(false);
            }
        }
        return voList;
    }
}
