package com.cyf.shop.web.index.controller;

import com.cyf.base.common.bean.AjaxResult;
import com.cyf.base.common.enums.NodeTypeEnum;
import com.cyf.base.common.enums.StatusEnum;
import com.cyf.base.common.utils.JsonUtil;
import com.cyf.base.common.utils.StringUtil;
import com.cyf.shop.auth.bean.Node;
import com.cyf.shop.libs.*;
import com.cyf.shop.web.index.vo.MenuVo;
import dubbo.spring.javaconfig.EnvConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @Title：index控制器
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/3/21
 */
@Controller
@RequestMapping("index/index")
public class IndexController {
    @Autowired
    Permission permission;
    @Autowired
    UserComponent userComponent;
    @Autowired
    EnvConfig envConfig;

    @RequestMapping("index")
    public ModelAndView index(HttpServletRequest request){
        ModelAndView mv = new ModelAndView();
        mv.addObject("userName", RequestUtil.getUserName());

        //取得当前用户可以访问的所有节点，然后找出一级菜单节点
        List<MenuVo> menuVoList = new ArrayList<>();
        List<Node> nodeList = permission.getUserNodes(RequestUtil.getUserId());
        quickSortNodeBySortDesc(nodeList, 0, nodeList.size() - 1);
        if(nodeList != null && ! nodeList.isEmpty()){
            for(Node node : nodeList){
                if(node.getLevel().equals(1) && node.getType().equals(1)){
                    menuVoList.add(formatToMenuVo(node));
                }
            }
        }

        String isAdmin = permission.isSuperAdmin(RequestUtil.getUserId()) ? "true" : "false";
        Map<String, String> userRoutes = permission.getNodeMap(RequestUtil.getUserId());
        mv.addObject("ssoHost", envConfig.ssoHost);
        mv.addObject("loginUrl", userComponent.getSsoLoginUrl(request));
        mv.addObject("topMenuList", menuVoList);
        mv.addObject("userRoutes", JsonUtil.toString(userRoutes));
        mv.addObject("isAdmin", isAdmin);
        return mv;
    }

    /**
     * 根据父菜单id取得其所有子菜单的json格式
     * @param parentId
     * @return
     */
    @ResponseBody
    @RequestMapping("childMenu")
    public Object childMenu(Long parentId){
        if(parentId==null) return AjaxResult.fail("父id不能为空");
        //取得当前菜单下的所有子菜单
        MenuVo menuVo = findUserActiveChildMenu(RequestUtil.getUserId(), parentId);

        //把菜单节点封装成BJUI能够解析的格式，然后返回JSON格式的数据
        return formatToBJUIMenu(menuVo);
    }

    /**
     * 登陆页面
     * @param request
     * @return
     */
    @RequestMapping("login")
    public ModelAndView login(HttpServletRequest request){
        ModelAndView mv = new ModelAndView();
        String loginUrl = userComponent.getSsoLoginUrl(request);
        mv.addObject("loginUrl", loginUrl);
        return mv;
    }

    /**
     * 注销
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception{
        permission.clearCacheNode(RequestUtil.getUserId());//清除权限节点缓存
        userComponent.logout(request, response);//清除用户信息缓存并跳转到登陆页面
    }

    //为节点快速排序
    private void quickSortNodeBySortDesc(List<Node> nodeList, int start, int end){
        int i, j;
        i = start;
        j = end;
        if ((nodeList == null) || (nodeList.size() == 0)){
            return;
        }else if(start > end){
            return;
        }else if(start < 0 || end >= nodeList.size()){
            return;
        }

        while (i < j) {//查找基准点下标
            while (i < j && nodeList.get(i).getSort() >= nodeList.get(j).getSort())
                // 以数组start下标的数据为key，右侧扫描
                j--;
            if (i < j) { // 右侧扫描，找出第一个比key小的，交换位置
                Node temp = nodeList.get(i);
                nodeList.set(i, nodeList.get(j));
                nodeList.set(j, temp);
            }

            while (i < j && nodeList.get(i).getSort() > nodeList.get(j).getSort())
                // 左侧扫描（此时j中存储着key值）
                i++;
            if (i < j) { // 找出第一个比key大的，交换位置
                Node temp = nodeList.get(i);
                nodeList.set(i, nodeList.get(j));
                nodeList.set(j, temp);
            }
        }
        if (i - start > 1) { // 递归调用，把key前面的完成排序
            quickSortNodeBySortDesc(nodeList, 0, i - 1);
        }
        if (end - j > 1) {
            quickSortNodeBySortDesc(nodeList, j + 1, end); // 递归调用，把key后面的完成排序
        }
    }

    //把MenuVo对象转换为BJUI能识别的菜单数据结构
    private String formatToBJUIMenu(MenuVo menuVo){
        return JsonUtil.toString(menuVo.getChildren());
    }

    //找出当期用户的当前菜单节点下的所有子菜单
    private MenuVo findUserActiveChildMenu(Long userId, Long parentNodeId){
        List<Node> allNodeList = permission.getUserChildNode(userId, parentNodeId);//取得当前用户拥有当前菜单下的所有子节点
        filterNotMenuNode(allNodeList);//去掉非菜单类型的节点
        MenuVo menuVo = new MenuVo();
        buildTreeMenu(parentNodeId, allNodeList, menuVo);
        return menuVo;
    }

    //过滤掉已删除的，无效的，非菜单类型的节点，并把节点放到Map里面返回
    private void filterNotMenuNode(List<Node> nodeList){
        Iterator<Node> nodeIterator = nodeList.iterator();
        while(nodeIterator.hasNext()){
            Node node = nodeIterator.next();
            if(! node.getType().equals(NodeTypeEnum.MENU.getValue())){ //不是菜单类型的不要
                nodeIterator.remove();
            }
        }
    }

    //创建无限级树形菜单，如果nodeList的数据量很大，会有性能问题
    private void buildTreeMenu(Long parentId, List<Node> nodeList, MenuVo menuVo){
        for(Node node : nodeList){
            if(node.getParentId().equals(parentId)){
                MenuVo childMenuVo = formatToMenuVo(node);
                if(menuVo.getChildren()==null){
                    menuVo.setChildren(new ArrayList<>());
                }
                menuVo.getChildren().add(childMenuVo);
                buildTreeMenu(node.getId(), nodeList, childMenuVo);
            }
        }
    }

    //把Node对象转换为MenuVo对象
    private MenuVo formatToMenuVo(Node node){
        MenuVo menuVo = new MenuVo();
        menuVo.setId("sys_menu_id_"+node.getId().toString());
        menuVo.setName(node.getName());
        menuVo.setTarget("navtab");
        menuVo.setLevel(node.getLevel().toString());
        menuVo.setSort(node.getSort());
        menuVo.setUrl(node.getUrl());
        menuVo.setNodeId(node.getId());
        menuVo.setParentId(node.getParentId());
        return menuVo;
    }
}
