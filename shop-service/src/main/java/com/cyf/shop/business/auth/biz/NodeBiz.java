package com.cyf.shop.business.auth.biz;

import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.enums.DeleteEnum;
import com.cyf.base.common.enums.NodeTypeEnum;
import com.cyf.base.common.enums.StatusEnum;
import com.cyf.base.common.utils.StringUtil;
import com.cyf.shop.auth.bean.*;
import com.cyf.shop.auth.enums.CheckLevelEnum;
import com.cyf.shop.business.auth.dao.AuthorityHasNodeDao;
import com.cyf.shop.business.auth.dao.NodeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by chenyf on 2017/4/9.
 */
@Component
public class NodeBiz {
    private final static int ROOT_NODE_ID = 0;//根节点id
    @Autowired
    NodeDao nodeDao;
    @Autowired
    AuthBiz authBiz;
    @Autowired
    RoleBiz roleBiz;
    @Autowired
    AuthorityHasNodeDao authorityHasNodeDao;

    public BaseResponse<Long> createNode(Node node){
        if(node==null){
            return BaseResponse.fail("要创建的节点对象不能为空");
        }else if(node.getType()==null){
            return BaseResponse.fail("节点类型不能为空");
        }else if(StringUtil.isEmpty(node.getName())){
            return BaseResponse.fail("节点名称不能为空");
        }else if(node.getExclusive()==null){
            return BaseResponse.fail("是否独占不能为空");
        }else if(node.getParentId()==null){
            return BaseResponse.fail("父节点Id不能为空");
        }

        //计算当前节点的level
        int level = 1;
        String ancestorsId = String.valueOf(ROOT_NODE_ID);//所有祖先节点的id
        if(node.getParentId() > 0){
            Node parentNode = nodeDao.getById(node.getParentId());
            if(parentNode==null){
                return BaseResponse.fail("父节点不存在");
            }else if( !parentNode.getType().equals(NodeTypeEnum.MENU.getValue()) && node.getType().equals(NodeTypeEnum.MENU.getValue()) ){
                return BaseResponse.fail("父节点为非菜单类型的节点，故当前节点不能为菜单类型");
            }else{
                level = parentNode.getLevel() + 1;
                ancestorsId = parentNode.getAncestorsId()+","+parentNode.getId();
            }
        }

        if(node.getUrl()==null)node.setUrl("");
        if(node.getIcon()==null)node.setIcon("");
        if(node.getClassAttr()==null)node.setClassAttr("");
        if(node.getSort()==null)node.setSort(0);
        node.setStatus(StatusEnum.ACTIVE.getValue());
        node.setLevel(level);
        node.setAncestorsId(ancestorsId);
        node.setCreateTime(new Date());
        node.setUpdateTime(new Date());

        boolean result = nodeDao.insert(node) > 0;
        if(result){
            return BaseResponse.success(node.getId());
        }else{
            return BaseResponse.fail("新增节点失败！");
        }
    }

    /**
     * 删除节点
     * @param nodeId
     * @return
     */
    public BaseResponse deleteNodeWithRelate(Long nodeId){
        if(nodeId == null) return BaseResponse.fail("nodeId不能为空");

        //如果当前节点还有子节点，则不允许删除
        Map<String, Object> filter = new HashMap<String, Object>(2);
        filter.put("parentId", nodeId);
        filter.put("status", StatusEnum.ACTIVE.getValue());
        List<Node> childNodeList = nodeDao.listByCond(filter);
        if(childNodeList != null && !childNodeList.isEmpty()) return BaseResponse.fail("当前节点含有子节点，不能删除！");

        //根据节点id取得权限-节点关联记录
        List<Long> authNodeIdList = new ArrayList<>();
        List<AuthorityHasNode> authNodeList = listAuthorityHasNodeByNodeId(nodeId);
        for(AuthorityHasNode authNode : authNodeList){
            authNodeIdList.add(authNode.getId());
        }

        boolean result = nodeDao.deleteById(nodeId) > 0;
        if(result && authNodeIdList.size() > 0){
            authorityHasNodeDao.deleteByIdList(authNodeIdList);
        }

        if(result){
            return BaseResponse.success();
        }else{
            return BaseResponse.fail("删除节点失败！");
        }
    }

    public BaseResponse updateNode(Node node){
        if(node == null) return BaseResponse.fail("要更新的对象不能为空");
        if(node.getId() == null) return BaseResponse.fail("对象id不能为空");

        Node oldNode = nodeDao.getById(node.getId());
        if(node == null){
            return BaseResponse.fail("节点不存在");
        }

        if(node.getParentId() == null) node.setParentId(oldNode.getParentId());
        Long oldParentId = oldNode.getParentId();
        Long newParentId = node.getParentId();
        boolean isChangeParent = newParentId.longValue() != oldParentId.longValue() ? true : false;

        //如果有更换父节点，则需要重新计算节点的level
        int level = oldNode.getLevel();
        String ancestorsId = oldNode.getAncestorsId();//所有祖先节点的id
        if(! oldParentId.equals(newParentId)){//说明有更换父节点
            if(oldNode.getParentId().equals(0)){
                level = 1;
                ancestorsId = String.valueOf(ROOT_NODE_ID);
            }else{
                Node parent = nodeDao.getById(newParentId);
                if (parent == null) {
                    return BaseResponse.fail("父节点不存在");
                }else if( !parent.getType().equals(NodeTypeEnum.MENU.getValue()) && node.getType().equals(NodeTypeEnum.MENU.getValue()) ){
                    return BaseResponse.fail("不能把菜单类型的节点挂在非菜单类型的父节点下");
                } else {
                    level = parent.getLevel() + 1;
                    ancestorsId = parent.getAncestorsId()+","+parent.getId();
                }
            }
        }
        node.setLevel(level);
        node.setAncestorsId(ancestorsId);
        node.setUpdateTime(new Date());

        //如果有改变父节点，则需要查看是否有子节点，如果有，则需要更新其所有子节点的level
        int levelChangeNumber = node.getLevel() - oldNode.getLevel();//层级改变的数量(可能为负数)
        List<Node> needUpdateNodeList = new ArrayList<>();
        if(isChangeParent){
            needUpdateNodeList = listAllActiveChildNode(node.getId());
            if(needUpdateNodeList.size() > 0){
                Date updateTime = new Date();
                for(Node nodeTemp : needUpdateNodeList){
                    nodeTemp.setLevel(nodeTemp.getLevel()+levelChangeNumber);
                    nodeTemp.setAncestorsId(node.getAncestorsId()+","+node.getId());
                    nodeTemp.setUpdateTime(updateTime);
                }
            }
        }

        boolean result = nodeDao.updateIfNotNull(node) > 0;
        if(result && needUpdateNodeList.size() > 0){
            result = nodeDao.updateList(needUpdateNodeList) > 0;
        }

        if(result){
            return BaseResponse.success();
        }else{
            return BaseResponse.fail("更新节点失败！");
        }
    }

    public List<AuthorityHasNode> listAuthorityHasNodeByNodeId(Long nodeId){
        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("fkNodeId", nodeId);
        return authorityHasNodeDao.listBy("listAuthorityHasNodeByNodeId", paramMap);
    }

    public List<Node> listNodeByIdList(List<Long> nodeIdList){
        if(nodeIdList==null || nodeIdList.isEmpty()) return null;
        return nodeDao.listByIdList(nodeIdList);
    }

    /**
     * 取得一个用户的所有可访问的节点 = 游客可访问节点 + 登陆可访问节点 + 已授权访问节点
     * @param userId
     * @return
     */
    public List<Node> listActiveAuthNodeByUserId(Long userId){
        List<Node> allowNodeList = new ArrayList<>();

        //取得所有节点
        List<Node> allNodeList = nodeDao.listAll();

        //取得当前用户可访问的所有的节点id
        Map<Long, String> nodeIdMap = this.mapAuthorizeNodeIdMapByUserId(userId);

        for(Node node : allNodeList){
            if(node.getStatus().equals(StatusEnum.INACTIVE.getValue())){
                continue;
            }else if(nodeIdMap.containsKey(node.getId())){
                allowNodeList.add(node);
            }
        }
        return allowNodeList;
    }

    /**
     * 取得一个用户所有可访问的节点id
     * @param userId
     * @return
     */
    private Map<Long, String> mapAuthorizeNodeIdMapByUserId(Long userId){
        Map<Long, String> nodeIdMap = new HashMap<>();
        List<Long> authorityIdList = new ArrayList<>();

        //取得游客可访问的权限
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("status", StatusEnum.ACTIVE.getValue());
        paramMap.put("delete", DeleteEnum.UNDELETED.getValue());
        paramMap.put("checkLevel", CheckLevelEnum.GUEST.getValue());
        List<Authority> authList = authBiz.listByCond(paramMap);
        if(! authList.isEmpty()){
            for(Authority auth : authList){
                authorityIdList.add(auth.getId());
            }
            authList.clear();//清空，给下一步重用
        }

        //如果userId不为空，则取得 登陆后可访问的权限 + 当前用户已被授权的权限
        if(userId != null && userId > 0){
            paramMap.put("checkLevel", CheckLevelEnum.LOGIN.getValue());
            authList = authBiz.listByCond(paramMap);
            if(! authList.isEmpty()){
                for(Authority auth : authList){
                    authorityIdList.add(auth.getId());
                }
            }

            List<RoleHasAuthority> roleHasAuthorityList = roleBiz.listRoleHasAuthorityByUserId(userId);
            if(roleHasAuthorityList != null && roleHasAuthorityList.size() > 0){
                for(RoleHasAuthority entity : roleHasAuthorityList){
                    authorityIdList.add(entity.getFkAuthorityId());
                }
            }
        }

        //再根据多个权限id取得权限-节点关联记录
        if(authorityIdList.size() > 0){
            List<AuthorityHasNode> authorityHasNodeList = authBiz.listAuthorityHasNodeByAuthorityIdList(authorityIdList);
            if(authorityHasNodeList != null && authorityHasNodeList.size() > 0){
                for(AuthorityHasNode entity : authorityHasNodeList){
                    if(! nodeIdMap.containsKey(entity.getFkNodeId())){
                        nodeIdMap.put(entity.getFkNodeId(), "");
                    }
                }
            }
        }
        return nodeIdMap;
    }

    /**
     * 根据多个权限id，取得节点id
     * @param authorityIdList
     * @return
     */
    public List<Node> listActiveNodeByAuthorityIdList(List<Long> authorityIdList){
        if(authorityIdList==null || authorityIdList.isEmpty()) return null;

        List<Integer> statusList = new ArrayList<>();
        statusList.add(StatusEnum.ACTIVE.getValue());

        //根据权限id取得权限-节点关联表记录
        List<AuthorityHasNode> authorityHasNodeList = authBiz.listAuthorityHasNodeByAuthorityIdList(authorityIdList);
        if(authorityHasNodeList.isEmpty()){
            return new ArrayList<>();
        }

        //根据节点id取得节点记录
        List<Long> nodeIdList = new ArrayList<>();
        for(AuthorityHasNode entity : authorityHasNodeList){
            authorityIdList.add(entity.getFkNodeId());
        }
        return this.listNodeByIdList(nodeIdList);
    }

    /**
     * 取得某个节点下的所有子节点
     * @param nodeId
     * @return
     */
    public List<Node> listAllActiveChildNode(Long nodeId){
        if(nodeId == null) return null;
        return nodeDao.listBy("listActiveChildNode", nodeId);
    }

    /**
     * @deprecated
     * @param parentId
     * @param searchNodeList
     * @param childNodeList
     */
    private void listAllActiveChildNode(Long parentId, List<Node> searchNodeList, List<Node> childNodeList){
        for(Node node : searchNodeList){
            if(node.getStatus() != StatusEnum.ACTIVE.getValue()) continue;

            if(parentId.equals(node.getParentId())){
                childNodeList.add(node);
                listAllActiveChildNode(node.getId(), searchNodeList, childNodeList);
            }
        }
    }

    public List<Node> listAllActiveNode(String sortColumns){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("status", StatusEnum.ACTIVE.getValue());
        paramMap.put("delete", DeleteEnum.UNDELETED.getValue());
        if(! StringUtil.isEmpty(sortColumns)){
            paramMap.put("sortColumns", sortColumns);
        }
        return nodeDao.listByCond(paramMap);
    }

    public Node getNodeById(Long nodeId){
        if(nodeId==null) return null;
        return nodeDao.getById(nodeId);
    }
}
