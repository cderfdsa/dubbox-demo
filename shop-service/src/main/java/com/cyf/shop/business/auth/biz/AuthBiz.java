package com.cyf.shop.business.auth.biz;

import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.enums.DeleteEnum;
import com.cyf.base.common.enums.StatusEnum;
import com.cyf.base.common.utils.StringUtil;
import com.cyf.shop.auth.bean.*;
import com.cyf.shop.auth.vo.AuthAssignNodeVo;
import com.cyf.shop.business.auth.dao.AuthorityDao;
import com.cyf.shop.business.auth.dao.AuthorityHasNodeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by chenyf on 2017/4/9.
 */
@Component
public class AuthBiz {
    @Autowired
    RoleBiz roleBiz;
    @Autowired
    AuthorityDao authorityDao;
    @Autowired
    AuthorityHasNodeDao authorityHasNodeDao;

    /**
     * 删除权限、权限-角色关联记录、权限-节点关联记录
     * @param authId
     * @return
     */
    public BaseResponse deleteAuthWithRelate(Long authId) {
        if(authId == null) return BaseResponse.fail("authId不能为空");

        //如果当前节点还有子节点，则不允许删除
        Map<String, Object> filter = new HashMap<String, Object>(2);
        filter.put("parentId", authId);
        filter.put("status", StatusEnum.ACTIVE.getValue());
        List<Authority> childAuthList = authorityDao.listByCond(filter, "");
        if(childAuthList != null && !childAuthList.isEmpty()) return BaseResponse.fail("当前权限含有子权限，不能删除！");

        //根据权限id取得权限-角色关联记录
        List<Long> roleAuthIdList = new ArrayList<>();
        List<RoleHasAuthority> roleAuthList = roleBiz.listRoleHasAuthorityByAuthId(authId);
        for(RoleHasAuthority roleAuth : roleAuthList){
            roleAuthIdList.add(roleAuth.getId());
        }

        //根据权限id取得权限-节点关联记录
        List<Long> authNodeIdList = new ArrayList<>();
        List<Long> authIdList = new ArrayList<>();
        authIdList.add(authId);
        List<AuthorityHasNode> authNodeList = listAuthorityHasNodeByAuthorityIdList(authIdList);
        for(AuthorityHasNode authNode : authNodeList){
            authNodeIdList.add(authNode.getId());
        }

        boolean result = authorityDao.deleteById(authId) > 0;
        if(result && roleAuthIdList.size() > 0){
            roleBiz.batchDeleteRoleAuthority(roleAuthIdList);
        }
        if(result && authNodeIdList.size() > 0){
            authorityHasNodeDao.deleteByIdList(authNodeIdList);
        }
        if(result){
            return BaseResponse.success();
        }else{
            return BaseResponse.fail("删除权限失败！");
        }
    }

    /**
     * 取得某个权限下的所有子权限
     * @param parentAuthId
     * @return
     */
    public List<Authority> findAllActiveChildAuthority(Long parentAuthId){
        List<Authority> allAuthorityList = authorityDao.listAll();
        List<Authority> childAuthorityList = new ArrayList<>();
        findAllActiveChildAuthority(parentAuthId, allAuthorityList, childAuthorityList);
        return childAuthorityList;
    }
    private void findAllActiveChildAuthority(Long parentId, List<Authority> searchAuthList, List<Authority> childAuthList){
        for(Authority auth : searchAuthList){
            if(auth.getStatus() != StatusEnum.ACTIVE.getValue()) continue;

            if(parentId.equals(auth.getParentId())){
                childAuthList.add(auth);
                findAllActiveChildAuthority(auth.getId(), searchAuthList, childAuthList);
            }
        }
    }

    /**
     * 添加权限
     * @param authority
     * @return
     */
    public BaseResponse createAuthority(Authority authority){
        if(authority == null) return BaseResponse.fail("对象不能为空");
        if(authority.getParentId()==null){
            return BaseResponse.fail("parentId不能为空");
        }

        int level = 0;
        if(authority.getParentId()==0){
            level = 1;
        }else{
            Authority parent = authorityDao.getById(authority.getParentId());
            if(parent==null){
                return BaseResponse.fail("parent不存在");
            }else{
                level = parent.getLevel() + 1;
            }
        }

        authority.setLevel(level);
        authority.setStatus(StatusEnum.ACTIVE.getValue());
        authority.setCreateTime(new Date());
        authority.setUpdateTime(new Date());

        Integer count = authorityDao.insert(authority);
        if(count > 0){
            return BaseResponse.success();
        }else{
            return BaseResponse.fail("添加权限失败！");
        }
    }

    /**
     * 更新权限
     * @param authority
     * @return
     */
    public BaseResponse updateAuthority(Authority authority){
        if(authority == null) return BaseResponse.fail("对象不能为空");
        if(authority.getId() == null) return BaseResponse.fail("对象id不能为空");

        Authority oldAuthority = authorityDao.getById(authority.getId());
        if(oldAuthority == null){
            return BaseResponse.fail("权限不存在");
        }

        if(authority.getParentId() == null) authority.setParentId(oldAuthority.getParentId());
        Long oldParentId = oldAuthority.getParentId();
        Long newParentId = authority.getParentId();
        boolean isChangeParent = newParentId.longValue() != oldParentId.longValue() ? true : false;

        //如果有更换父节点，则需要重新计算节点的level
        int level = oldAuthority.getLevel();
        if(! oldParentId.equals(newParentId)){
            if(authority.getParentId()==0){
                level = 1;
            }else {
                Authority parent = authorityDao.getById(newParentId);
                if (parent == null) {
                    return BaseResponse.fail("父权限不存在");
                } else {
                    level = parent.getLevel() + 1;
                }
            }
        }
        authority.setLevel(level);
        authority.setUpdateTime(new Date());

        //如果有改变父节点，则需要查看是否有子节点，如果有，则需要更新其所有子节点的level
        int levelChangeNumber = authority.getLevel() - oldAuthority.getLevel();//层级改变的数量(可能为负数)
        List<Authority> allAuthList = authorityDao.listAll();
        List<Authority> needUpdateAuthList = new ArrayList<>();
        if(isChangeParent && levelChangeNumber != 0){
            findAllActiveChildAuthority(authority.getId(), allAuthList, needUpdateAuthList);
            if(needUpdateAuthList.size() > 0){
                Date updateTime = new Date();
                for(Authority auth : needUpdateAuthList){
                    auth.setLevel(auth.getLevel()+levelChangeNumber);
                    auth.setUpdateTime(updateTime);
                }
            }
        }

        boolean result = authorityDao.updateIfNotNull(authority) > 0;
        if(result && needUpdateAuthList.size() > 0){
            result = authorityDao.updateList(needUpdateAuthList) > 0;
        }

        if(result){
            return BaseResponse.success();
        }else{
            return BaseResponse.fail("更新权限失败！");
        }
    }

    /**
     * 取得一个用户的所有权限
     * @param userId
     * @return
     */
    public List<Authority> listActiveAuthorityByUserId(Long userId){
        if(userId == null) return null;

        List<Authority> authList = new ArrayList<>();
        List<Long> roleIdList = new ArrayList<>();
        List<RoleUser> roleUserList = roleBiz.listActiveRoleUserByUserId(userId);
        if(roleUserList != null){
            for(RoleUser roleUser : roleUserList){
                roleIdList.add(roleUser.getFkRoleId());
            }
        }

        if(! roleIdList.isEmpty()){
            authList = this.listActiveAuthorityByRoleIdList(roleIdList);
        }
        return authList;
    }

    /**
     * 取得多个角色的所有权限
     * @param fkRoleIdList
     * @return
     */
    public List<Authority> listActiveAuthorityByRoleIdList(List<Long> fkRoleIdList) {
        if(fkRoleIdList == null || fkRoleIdList.isEmpty()) return null;

        List<RoleHasAuthority> roleHasAuthorityList = roleBiz.listRoleHasAuthorityByRoleIdList(fkRoleIdList);

        List<Long> authorityIdList = new ArrayList<>();
        if(roleHasAuthorityList.isEmpty()){
            return new ArrayList<>();
        }

        for(RoleHasAuthority entity: roleHasAuthorityList){
            authorityIdList.add(entity.getFkAuthorityId());
        }

        List<Integer> statusList = new ArrayList<>();
        statusList.add(StatusEnum.ACTIVE.getValue());
        Map<String, Object> filter = new HashMap<>();
        filter.put("idList", authorityIdList);
        filter.put("statusList", statusList);
        return this.authorityDao.listByCond(filter);
    }

    /**
     * 取得某个角色的所有权限
     * @param fkRoleId
     * @return
     */
    public List<Authority> listActiveAuthorityByRoleId(Long fkRoleId){
        if(fkRoleId == null) return null;
        List<Long> roleIdList = new ArrayList<>();
        roleIdList.add(fkRoleId);
        return listActiveAuthorityByRoleIdList(roleIdList);
    }

    /**
     * 根据根据角色id、多个权限id，先过滤掉不属于当前角色的权限、再在剩下的权限中找出处于有效状态下的权限
     * @param roleId
     * @param authorityIdList
     * @return
     */
    public List<Authority> listActiveAuthorityByRoleIdAndAuthorityIdList(Long roleId, List<Long> authorityIdList){
        List<RoleHasAuthority> roleHasAuthorityList = roleBiz.listRoleHasAuthorityByRoleIdAndAuthorityIdList(roleId, authorityIdList);
        authorityIdList.clear();
        if(! roleHasAuthorityList.isEmpty()){
            for(RoleHasAuthority entity : roleHasAuthorityList){
                authorityIdList.add(entity.getFkAuthorityId());
            }
        }
        return authorityDao.listByIdList(authorityIdList);
    }

    /**
     * 根据多个权限id，取得权限-角色关联记录表
     * @param authorityIdList
     * @return
     */
    public List<AuthorityHasNode> listAuthorityHasNodeByAuthorityIdList(List<Long> authorityIdList){
        Map<String, Object> filter = new HashMap<>();
        filter.put("fkAuthorityIdList", authorityIdList);
        return authorityHasNodeDao.listByCond(filter);
    }

    public List<Authority> listByCond(Map<String, Object> paramMap){
        return authorityDao.listByCond(paramMap);
    }

    public Authority getAuthorityById(Long authId){
        return authorityDao.getById(authId);
    }

    public List<Authority> listAllActiveAuth(String sortColumns){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("status", StatusEnum.ACTIVE.getValue());
        paramMap.put("delete", DeleteEnum.UNDELETED.getValue());
        if(! StringUtil.isEmpty(sortColumns)){
            paramMap.put("sortColumns", sortColumns);
        }
        return authorityDao.listByCond(paramMap);
    }

    public List<AuthorityHasNode> listAuthorityHasNodeByAuthId(Long authId){
        if(authId == null) return null;
        Map<String, Object> param = new HashMap<>();
        param.put("fkAuthorityId", authId);
        return authorityHasNodeDao.listByCond(param);
    }

    public List<AuthAssignNodeVo> listAuthAssignNode(Long authId){
        //找出当前权限关联的节点
        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("fkAuthorityId", authId);
        Map<Long, AuthorityHasNode> authHasNodeMap = authorityHasNodeDao.mapByCond(paramMap, "fkNodeId");

        //找出所有有效的节点
        List<AuthAssignNodeVo> voList = authorityHasNodeDao.listBy("listAuthAssignNode",null);

        //封装Vo返回
        for(AuthAssignNodeVo vo : voList){
            vo.setAuthId(authId);
            if(authHasNodeMap.containsKey(vo.getNodeId())){
                vo.setIsAssignedAuthId(true);
            }else{
                vo.setIsAssignedAuthId(false);
            }
        }
        return voList;
    }

    public BaseResponse refreshAuthorityHasNode(Long authId, List<Long> newNodeIdList){
        if(authId==null) return BaseResponse.fail("权限id为空");
        if(newNodeIdList==null) newNodeIdList = new ArrayList<>();

        //找出当前权限关联的节点
        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("fkAuthorityId", authId);
        Map<Long, AuthorityHasNode> oldAuthHasNodeMap = authorityHasNodeDao.mapByCond(paramMap, "fkNodeId");

        Map<Long, String> newNodeIdMap = new HashMap<>();
        for(Long nodeId : newNodeIdList){
            newNodeIdMap.put(nodeId, "");
        }

        List<Long> needDeleteIdList = new ArrayList<>();
        List<AuthorityHasNode> needInsertList = new ArrayList<>();
        if(newNodeIdMap.isEmpty() && oldAuthHasNodeMap.isEmpty()){
            //新旧都为空，不做任何处理
        }else if(newNodeIdMap.isEmpty() && !oldAuthHasNodeMap.isEmpty()){//新为空，旧不为空，旧的需要全部删除
            for(Map.Entry<Long, AuthorityHasNode> entry : oldAuthHasNodeMap.entrySet()){
                needDeleteIdList.add(entry.getValue().getId());
            }
        }else if(!newNodeIdMap.isEmpty() && oldAuthHasNodeMap.isEmpty()){//新不为空，旧为空，新的需要全部插入
            for(Map.Entry<Long, String> entry : newNodeIdMap.entrySet()){
                AuthorityHasNode obj = new AuthorityHasNode();
                obj.setFkAuthorityId(authId);
                obj.setFkNodeId(entry.getKey());
                obj.setCreateTime(new Date());
                obj.setUpdateTime(new Date());
                needInsertList.add(obj);
            }
        }else if(!newNodeIdMap.isEmpty() && !oldAuthHasNodeMap.isEmpty()){//新不为空，旧不为空，需要比较
            for(Map.Entry<Long, AuthorityHasNode> entry : oldAuthHasNodeMap.entrySet()){
                Long nodeId = entry.getKey();
                if(! newNodeIdMap.containsKey(nodeId)){//旧中有，新中无，需要删除
                    needDeleteIdList.add(entry.getValue().getId());
                }else{
                    //新、旧都有，不做处理
                }
            }

            for(Map.Entry<Long, String> entry : newNodeIdMap.entrySet()){//旧中无，新中有，需要新增
                AuthorityHasNode obj = new AuthorityHasNode();
                obj.setFkNodeId(entry.getKey());
                obj.setFkAuthorityId(authId);
                obj.setCreateTime(new Date());
                obj.setUpdateTime(new Date());
                needInsertList.add(obj);
            }
        }

        if(needDeleteIdList.size() > 0) authorityHasNodeDao.deleteByIdList(needDeleteIdList);
        if(needInsertList.size() > 0) authorityHasNodeDao.insertList(needInsertList);
        return BaseResponse.success();
    }
}
