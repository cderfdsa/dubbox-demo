package com.cyf.shop.auth.service;

import com.cyf.base.common.bean.BaseResponse;
import com.cyf.shop.auth.bean.AuthorityHasNode;
import com.cyf.shop.auth.bean.Node;

import java.util.List;

/**
 * Created by chenyf on 2017/4/9.
 */
public interface NodeService {
    /**
     * 取得一个用户的所有可访问的节点 = 游客可访问节点 + 登陆可访问节点 + 已授权访问节点
     * @param userId
     * @return
     */
    List<Node> listActiveAuthNodeByUserId(Long userId);

    public List<Node> listAllActiveNode(String sortColumns);

    public BaseResponse<Long> createNode(Node node);

    public BaseResponse deleteNodeWithRelate(Long nodeId);

    public BaseResponse updateNode(Node node);

    public Node getNodeById(Long nodeId);

    public List<AuthorityHasNode> listAuthorityHasNodeByNodeId(Long nodeId);

    public List<Node> listAllActiveChildNode(Long parentNodeId);
}
