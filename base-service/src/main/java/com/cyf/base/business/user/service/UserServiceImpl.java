package com.cyf.base.business.user.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.cyf.base.business.user.biz.UserBiz;
import com.cyf.base.user.bean.User;
import com.cyf.base.user.service.UserService;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.bean.PageParam;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/5/9 15:00
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserBiz userBiz;

    public BaseResponse updateUser(User user){
        return userBiz.updateUser(user);
    }

    public List<User> listByCond(Map<String, Object> paramMap){
        return userBiz.listByCond(paramMap);
    }

    public BaseResponse<List<User>> listPageByCond(Map<String, Object> paramMap, PageParam pageParam){
        return userBiz.listByCond(paramMap, pageParam);
    }

    /**
     * @return
     */
    public User getUserById(Long userId){
        return userBiz.getUserById(userId);
    }

    /**
     * 根据用户名、密码获取用户
     * @param loginName  账户名：手机号/邮箱/用户名
     * @param password  密码
     * @return User or null
     */
    public User getByValidPassword(String loginName, String password){
        return userBiz.getByValidPassword(loginName, password);
    }

    /**
     * 注册
     * @return
     */
    public BaseResponse register(User user){
        return userBiz.register(user);
    }

    public BaseResponse deleteUser(Long id){
        return userBiz.deleteUser(id);
    }
}
