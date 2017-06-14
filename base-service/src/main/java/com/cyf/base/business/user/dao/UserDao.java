package com.cyf.base.business.user.dao;

import com.cyf.base.user.bean.User;
import com.cyf.base.common.dao.MyBatisDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户dao
 * Created by chenyf on 2017/3/25.
 */
@Repository
public class UserDao extends MyBatisDao<User, Long> {

    public User getByLoginName(String loginName){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("loginName", loginName);
        return this.getOne("getByLoginName", paramMap);
    }

    public User getByUnionKey(String phone, String email, String usercode){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("phone", phone);
        paramMap.put("email", email);
        paramMap.put("usercode", usercode);
        return this.getOne("getByUnionKey", paramMap);
    }
}
