package com.cyf.shop.web.auth.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cyf.base.user.bean.User;
import com.cyf.base.user.service.UserService;
import com.cyf.base.common.bean.AjaxResult;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.bean.PageParam;
import com.cyf.base.common.enums.StatusEnum;
import com.cyf.base.common.utils.JsonUtil;
import com.cyf.shop.auth.service.RoleService;
import com.cyf.shop.libs.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/5/4 13:35
 */
@Controller
@RequestMapping("auth/user")
public class UserController {
    @Reference
    UserService userService;
    @Reference
    RoleService roleService;
    @Autowired
    Permission permission;

    @RequestMapping("userList")
    public ModelAndView userList(Integer status, String username, PageParam pageParam){
        ModelAndView mv = new ModelAndView();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("status", status);
        paramMap.put("username", username);
        BaseResponse<List<User>> response = userService.listPageByCond(paramMap, pageParam);
        pageParam.setTotalRecord(response.getTotalRecord());

        filterUserList(response.getData());
        mv.addObject("userList", JsonUtil.toStringBroswer(response.getData()));
        mv.addObject("pageParam", pageParam);
        mv.addObject("status", status);
        mv.addObject("username", username);
        return mv;
    }

    @RequestMapping("editUserView")
    public ModelAndView editUserView(Long userId){
        ModelAndView mv = new ModelAndView();
        User user = userService.getUserById(userId);
        mv.addObject("user", JsonUtil.toStringBroswer(user));
        return mv;
    }

    @ResponseBody
    @RequestMapping("editUserSave")
    public AjaxResult editUserSave(User user){
        return AjaxResult.convert(userService.updateUser(user));
    }

    /**
     * 激活用户
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping("activeUser")
    public AjaxResult activeUser(Long userId){
        User user = userService.getUserById(userId);
        if(user==null){
            return AjaxResult.fail("用户不存在");
        }
        user.setStatus(StatusEnum.ACTIVE.getValue());
        return AjaxResult.convert(userService.updateUser(user));
    }

    /**
     * 禁用用户
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping("inactiveUser")
    public AjaxResult inactiveUser(Long userId){
        User user = userService.getUserById(userId);
        if(user==null){
            return AjaxResult.fail("用户不存在");
        }
        user.setStatus(StatusEnum.INACTIVE.getValue());
        return AjaxResult.convert(userService.updateUser(user));
    }

    /**
     * 根据用户id查看当前用户已分配的角色
     * @param userId
     * @return
     */
    @RequestMapping("assignedRoleView")
    public ModelAndView assignedRoleView(Long userId){
        ModelAndView mv = new ModelAndView();
        User user = userService.getUserById(userId);
        user.setPassword("");//设置密码为空
        mv.addObject("user", JsonUtil.toStringBroswer(user));
        mv.addObject("roleUserList", JsonUtil.toStringBroswer(roleService.listRoleUserByUserId(userId)));
        return mv;
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
