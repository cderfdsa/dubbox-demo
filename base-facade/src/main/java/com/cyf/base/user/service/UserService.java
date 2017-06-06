package com.cyf.base.user.service;

import com.cyf.base.user.bean.User;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.bean.PageParam;

import java.util.List;
import java.util.Map;

/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/5/9
 */
public interface UserService {
    public BaseResponse updateUser(User user);

    public List<User> listByCond(Map<String, Object> paramMap);

    public BaseResponse<List<User>> listPageByCond(Map<String, Object> paramMap, PageParam pageParam);

    public User getUserById(Long userId);

    public User getByValidPassword(String loginName, String password);

    public BaseResponse register(User user);

    public BaseResponse deleteUser(Long id);
}
