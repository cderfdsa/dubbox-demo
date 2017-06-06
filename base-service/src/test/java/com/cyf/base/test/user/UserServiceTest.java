package com.cyf.base.test.user;

import com.cyf.base.test.BaseTestCase;
import com.cyf.base.user.bean.User;
import com.cyf.base.user.service.UserService;
import com.cyf.base.common.utils.JsonUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/5/9 16:01
 */
public class UserServiceTest extends BaseTestCase {
    @Autowired
    UserService userService;

    @Test
    public void getUserById(){
        User user = userService.getUserById(1L);
        System.out.println(JsonUtil.toStringPretty(user));
    }
}
