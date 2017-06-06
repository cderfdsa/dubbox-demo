package com.cyf.shop.web.auth.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cyf.base.common.bean.AjaxResult;
import com.cyf.base.common.utils.JsonUtil;
import com.cyf.base.common.utils.StringUtil;
import com.cyf.shop.auth.bean.Authority;
import com.cyf.shop.auth.service.AuthService;
import com.cyf.shop.auth.service.NodeService;
import com.cyf.shop.auth.vo.AuthAssignNodeVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

/**
 * Created by Administrator on 2017/4/26.
 */
@Controller
@RequestMapping("auth/auth")
public class AuthController {
    @Reference
    AuthService authService;
    @Reference
    NodeService nodeService;

    @RequestMapping("authList")
    public ModelAndView authList(){
        ModelAndView mv = new ModelAndView();
        getAuthCommonData(mv);
        return mv;
    }

    @RequestMapping("addAuthView")
    public ModelAndView addAuthView(){
        ModelAndView mv = new ModelAndView();
        getAuthCommonData(mv);
        return mv;
    }

    @RequestMapping("addChildAuth")
    public ModelAndView addChildAuth(Long authId){
        ModelAndView mv = new ModelAndView();
        getAuthCommonData(mv);
        Authority auth = authService.getAuthById(authId);
        mv.addObject("auth", JsonUtil.toStringBroswer(auth));
        return mv;
    }

    @ResponseBody
    @RequestMapping("addAuthSave")
    public AjaxResult addAuthSave(Authority auth){
        return AjaxResult.convert(authService.createAuth(auth));
    }

    @ResponseBody
    @RequestMapping("deleteAuth")
    public AjaxResult deleteAuth(Long authId){
        return AjaxResult.convert(authService.deleteAuthWithRelate(authId));
    }

    @RequestMapping("editAuthView")
    public ModelAndView editAuthView(Long authId){
        ModelAndView mv = new ModelAndView();
        getAuthCommonData(mv);
        Authority auth = authService.getAuthById(authId);
        mv.addObject("auth", JsonUtil.toStringBroswer(auth));
        return mv;
    }

    @ResponseBody
    @RequestMapping("editAuthSave")
    public AjaxResult editAuthSave(Authority auth){
        return AjaxResult.convert(authService.updateAuth(auth));
    }

    /**
     * 为当前权限分配节点
     * @param authId
     * @return
     */
    @RequestMapping("assignNodeView")
    public ModelAndView assignNodeView(Long authId){
        ModelAndView mv = new ModelAndView();
        Authority auth = authService.getAuthById(authId);
        List<AuthAssignNodeVo> assignVoList = authService.listAuthAssignNode(authId);
        mv.addObject("auth", JsonUtil.toStringBroswer(auth));
        mv.addObject("assignVoList", JsonUtil.toStringBroswer(assignVoList));
        return mv;
    }

    /**
     * 保存当前权限分配到的节点
     * @param authId
     * @return
     */
    @ResponseBody
    @RequestMapping("assignNodeSave")
    public AjaxResult assignNodeSave(Long authId, String nodeIdJsonStr){
        if(authId==null) return AjaxResult.fail("权限id为空");
        List<Long> nodeIdList = StringUtil.getLongList(nodeIdJsonStr, ",");
        return AjaxResult.convert(authService.refreshAuthorityHasNode(authId, nodeIdList));
    }

    //排序：使同一父节点下的所有权限能够相邻排列
    private void sortAuthToBrotherNextBy(Long parentId, List<Authority> authList, List<Authority> sortAuthList){
        for(Authority auth : authList){
            if(auth.getParentId().equals(parentId)){
                sortAuthList.add(auth);
                sortAuthToBrotherNextBy(auth.getId(), authList, sortAuthList);
            }
        }
    }

    //获取截个页面的公共数据，现在有节点列表、添加节点、编辑节点 共用此方法
    private void getAuthCommonData(ModelAndView mv){
        String sortColumns = "createTime,name";
        List<Authority> authList = authService.listAllActiveAuth(sortColumns);

        Map<String, String> parentIdMap = new HashMap<>();
        for(Authority auth : authList){
            parentIdMap.put(auth.getParentId().toString(), "");
        }

        List<Authority> sortAuthList = new ArrayList<>();
        sortAuthToBrotherNextBy(0L, authList, sortAuthList);
        mv.addObject("authList", JsonUtil.toStringBroswer(sortAuthList));
        mv.addObject("parentIdMap", JsonUtil.toStringBroswer(parentIdMap));
    }
}
