package com.cyf.shop.web.auth.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cyf.base.common.bean.AjaxResult;
import com.cyf.base.common.utils.JsonUtil;
import com.cyf.shop.auth.bean.Node;
import com.cyf.shop.auth.service.NodeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/23.
 */
@Controller
@RequestMapping("auth/node")
public class NodeController {
    @Reference
    NodeService nodeService;

    @RequestMapping("nodeList")
    public ModelAndView nodeList() throws Exception{
        ModelAndView mv = new ModelAndView();
        getNodeCommonData(mv);
        return mv;
    }

    @RequestMapping("addNodeView")
    public ModelAndView addNodeView(){
        ModelAndView mv = new ModelAndView();
        getNodeCommonData(mv);
        return mv;
    }

    @RequestMapping("addChildNode")
    public ModelAndView addChildNode(Long nodeId){
        ModelAndView mv = new ModelAndView();
        getNodeCommonData(mv);
        Node node = nodeService.getNodeById(nodeId);
        mv.addObject("node", JsonUtil.toStringBroswer(node));
        return mv;
    }

    @ResponseBody
    @RequestMapping("addNodeSave")
    public AjaxResult addNodeSave(Node node){
        return AjaxResult.convert(nodeService.createNode(node));
    }

    @ResponseBody
    @RequestMapping("deleteNode")
    public AjaxResult deleteNode(Long nodeId){
        return AjaxResult.convert(nodeService.deleteNodeWithRelate(nodeId));
    }

    @RequestMapping("editNodeView")
    public ModelAndView editNodeView(Long nodeId){
        ModelAndView mv = new ModelAndView();
        getNodeCommonData(mv);
        mv.addObject("node", JsonUtil.toStringBroswer(nodeService.getNodeById(nodeId)));
        return mv;
    }

    @ResponseBody
    @RequestMapping("editNodeSave")
    public AjaxResult editNodeSave(Node node){
        return AjaxResult.convert(nodeService.updateNode(node));
    }

    /**
     * 根据节点id查看当前节点已分配的权限
     * @param nodeId
     * @return
     */
    @RequestMapping("assignedAuthView")
    public ModelAndView assignedAuthView(Long nodeId){
        ModelAndView mv = new ModelAndView();
        mv.addObject("authNodeList", JsonUtil.toStringBroswer(nodeService.listAuthorityHasNodeByNodeId(nodeId)));
        return mv;
    }

    //排序：使同一父节点下的所有节点能够相邻排列
    private void sortNodeToBrotherNextBy(Long parentId, List<Node> nodeList, List<Node> sortNodeList){
        for(Node node : nodeList){
            if(node.getParentId().equals(parentId)){
                sortNodeList.add(node);
                sortNodeToBrotherNextBy(node.getId(), nodeList, sortNodeList);
            }
        }
    }

    //获取截个页面的公共数据，现在有节点列表、添加节点、编辑节点 共用此方法
    private void getNodeCommonData(ModelAndView mv){
        String sortColumns = "sort DESC,name";
        List<Node> nodeList = nodeService.listAllActiveNode(sortColumns);

        Map<String, String> parentIdMap = new HashMap<>();
        for(Node node : nodeList){
            parentIdMap.put(node.getParentId().toString(), "");
        }

        List<Node> sortNodeList = new ArrayList<>();
        sortNodeToBrotherNextBy(0L, nodeList, sortNodeList);
        mv.addObject("nodeList", JsonUtil.toStringBroswer(sortNodeList));
        mv.addObject("parentIdMap", JsonUtil.toStringBroswer(parentIdMap));
    }
}
