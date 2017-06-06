package com.cyf.shop.business.auth.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.shop.auth.bean.AuthorityHasNode;
import com.cyf.shop.auth.bean.Node;
import com.cyf.shop.auth.service.NodeService;
import com.cyf.shop.business.auth.biz.NodeBiz;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by chenyf on 2017/4/9.
 */
@Service
public class NodeServiceImpl implements NodeService {
    @Autowired
    NodeBiz nodeBiz;

    /**
     * 取得一个用户的所有可访问的节点 = 游客可访问节点 + 登陆可访问节点 + 已授权访问节点
     * @param userId
     * @return
     */
    public List<Node> listActiveAuthNodeByUserId(Long userId){
        return nodeBiz.listActiveAuthNodeByUserId(userId);
    }

    public List<Node> listAllActiveNode(String sortColumns){
        return nodeBiz.listAllActiveNode(sortColumns);
    }

    public BaseResponse<Long> createNode(Node node){
        return nodeBiz.createNode(node);
    }

    public BaseResponse deleteNodeWithRelate(Long nodeId){
        return nodeBiz.deleteNodeWithRelate(nodeId);
    }

    public BaseResponse updateNode(Node node){
        return nodeBiz.updateNode(node);
    }

    public Node getNodeById(Long nodeId){
        return nodeBiz.getNodeById(nodeId);
    }

    public List<AuthorityHasNode> listAuthorityHasNodeByNodeId(Long nodeId){
        return nodeBiz.listAuthorityHasNodeByNodeId(nodeId);
    }

    public List<Node> listAllActiveChildNode(Long parentNodeId){
        return nodeBiz.listAllActiveChildNode(parentNodeId);
    }

}
