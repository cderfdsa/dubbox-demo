package com.cyf.shop.test.auth;

import com.cyf.base.common.utils.JsonUtil;
import com.cyf.shop.auth.bean.Node;
import com.cyf.shop.auth.service.NodeService;
import com.cyf.shop.test.BaseTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Administrator on 2017/5/6.
 */
public class NodeServiceTest extends BaseTestCase {
    @Autowired
    NodeService nodeService;

    @Test
    public void listAllActiveNode(){
        List<Node> nodeList = nodeService.listAllActiveNode("id desc");
        System.out.println(JsonUtil.toStringPretty(nodeList));
    }
}
