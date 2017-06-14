package com.cyf.shop.web.auth.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cyf.base.user.bean.User;
import com.cyf.base.user.service.UserService;
import com.cyf.base.common.bean.AjaxResult;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.enums.DeleteEnum;
import com.cyf.base.common.utils.JsonUtil;
import com.cyf.base.common.utils.StringUtil;
import com.cyf.shop.auth.bean.Role;
import com.cyf.shop.auth.bean.RoleUser;
import com.cyf.shop.auth.service.RoleService;
import com.cyf.shop.auth.vo.RoleAssignAuthVo;
import com.cyf.shop.libs.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

/**
 * @Title：角色控制器
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/4/18 12:25
 */
@Controller
@RequestMapping("auth/role")
public class RoleController {
    @Autowired
    Permission permission;
    @Reference
    RoleService roleService;
    @Reference
    UserService userService;

    @RequestMapping("roleList")
    public ModelAndView roleList(){
        ModelAndView mv = new ModelAndView();
        List<Role> roleList = roleService.listRoleByCond(new HashMap<>());
        mv.addObject("roleList", JsonUtil.toStringBroswer(roleList));
        return mv;
    }

    @RequestMapping("addRoleView")
    public ModelAndView addRoleView(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }

    @ResponseBody
    @RequestMapping("addRoleSave")
    public AjaxResult addRoleSave(Role role){
        BaseResponse<Role> response = roleService.createRole(role);
        return AjaxResult.convert(response);
    }

    @RequestMapping("editRoleView")
    public ModelAndView editRoleView(Long roleId){
        ModelAndView mv = new ModelAndView();
        Role role = roleService.getRoleById(roleId);
        mv.addObject("role", JsonUtil.toStringBroswer(role));
        return mv;
    }

    @ResponseBody
    @RequestMapping("editRoleSave")
    public AjaxResult editRoleSave(Role role){
        return AjaxResult.convert(roleService.updateRole(role));
    }

    @ResponseBody
    @RequestMapping("deleteRole")
    public AjaxResult deleteRole(Long roleId){
        BaseResponse response = roleService.deleteRoleWithRelate(roleId);
        return AjaxResult.convert(response);
    }

    @RequestMapping("assignUserView")
    public ModelAndView assignUserView(Long roleId){
        ModelAndView mv = new ModelAndView();

        //取得角色对象
        Role role = roleService.getRoleById(roleId);

        //取得所有未删除的的用户
        Map<String, Object> param = new HashMap<>();
        param.put("delete", DeleteEnum.UNDELETED.getValue());
        List<User> userList = userService.listByCond(param);
        filterUserList(userList);

        //根据角色id取得当前角色已关联的用户
        List<RoleUser> roleUserList = roleService.listRoleUserByRoleId(roleId);

        mv.addObject("role", JsonUtil.toStringBroswer(role));
        mv.addObject("roleUserList", JsonUtil.toStringBroswer(roleUserList));
        mv.addObject("userList", JsonUtil.toStringBroswer(userList));
        return mv;
    }

    @ResponseBody
    @RequestMapping("assignUserSave")
    public AjaxResult assignUserSave(Long roleId, String userIdStr){
        List<RoleUser> newRoleUserList = new ArrayList<>();
        String[] idArr = userIdStr.split(",");
        for(int i=0; i<idArr.length; i++){
            RoleUser roleUser = new RoleUser();
            roleUser.setFkRoleId(roleId);
            roleUser.setFkUserId(Long.valueOf(idArr[i]));
            newRoleUserList.add(roleUser);
        }
        boolean result = roleService.refreshRoleUser(roleId, newRoleUserList);
        if(result){
            return AjaxResult.success();
        }else{
            return AjaxResult.fail("角色用户绑定关系处理失败");
        }
    }

    /**
     * 分配权限
     * @param roleId
     * @return
     */
    @RequestMapping("assignAuthView")
    public ModelAndView assignAuthView(Long roleId){
        ModelAndView mv = new ModelAndView();
        Role role = roleService.getRoleById(roleId);
        List<RoleAssignAuthVo> assignVoList = roleService.listRoleAssignAuth(roleId);
        mv.addObject("role", JsonUtil.toStringBroswer(role));
        mv.addObject("assignVoList", JsonUtil.toStringBroswer(assignVoList));
        return mv;
    }

    /**
     * 保存分配权限
     * @param roleId
     * @return
     */
    @ResponseBody
    @RequestMapping("assignAuthSave")
    public AjaxResult assignAuthSave(Long roleId, String authIdJsonStr){
        if(roleId==null) return AjaxResult.fail("角色id为空");
        List<Long> authIdList = StringUtil.getLongList(authIdJsonStr, ",");
        return AjaxResult.convert(roleService.refreshRoleHasAuthority(roleId, authIdList));
    }

    private void filterUserList(List<User> userList){
        Iterator<User> iterator = userList.iterator();
        while (iterator.hasNext()){
            User user = iterator.next();
            if(permission.isSuperAdmin(user.getId())){
                iterator.remove();
            }else{
                user.setPassword(null);
            }
        }
    }
}
