package com.cyf.base.business.user.biz;

import com.cyf.base.business.user.dao.UserDao;
import com.cyf.base.user.bean.User;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.bean.PageParam;
import com.cyf.base.common.utils.MD5Util;
import com.cyf.base.common.utils.StringUtil;
import com.cyf.base.common.utils.UidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Title：用户业务层
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/5/9
 */
@Component
public class UserBiz {
    @Autowired
    UserDao userDao;

    public BaseResponse updateUser(User user){
        if(user == null){
            return BaseResponse.fail("用户对象不能为空");
        }else if(user.getId()==null){
            return BaseResponse.fail("对象id不能为空");
        }

        User userOld = getUserById(user.getId());
        if(userOld==null){
            return BaseResponse.fail("用户不存在");
        }
        //只允许更新下面这几个字段
        userOld.setStatus(user.getStatus());
        userOld.setUsername(user.getUsername());
        user.setUpdateTime(new Date());
        boolean result = userDao.update(user) > 0;
        if(result){
            return BaseResponse.success();
        }else{
            return BaseResponse.fail("更新用户失败");
        }
    }

    public List<User> listByCond(Map<String, Object> paramMap){
        return userDao.listByCond(paramMap);
    }

    public BaseResponse<List<User>> listByCond(Map<String, Object> paramMap, PageParam pageParam){
        return userDao.listByCond(paramMap, pageParam);
    }

    /**
     * @return
     */
    public User getUserById(Long userId){
        return userDao.getById(userId);
    }

    /**
     * 根据用户名、密码获取用户
     * @param loginName  账户名：手机号/邮箱/用户名
     * @param password  密码
     * @return User or null
     */
    public User getByValidPassword(String loginName, String password){
        User user = userDao.getByLoginName(loginName);
        if(user != null && user.getPassword().equals(encryptPassword(password))){
            return user;
        }else{
            return null;
        }
    }

    /**
     * 注册
     * @return
     */
    public BaseResponse register(User user){
        if(user==null){
            return BaseResponse.fail("注册用户不能为空");
        }else if(StringUtil.isEmpty(user.getPhone())){
            return BaseResponse.fail("手机号不能为空");
        }else if(StringUtil.isEmpty(user.getEmail())){
            return BaseResponse.fail("邮箱不能为空");
        }else if(StringUtil.isEmpty(user.getPassword())){
            return BaseResponse.fail("密码不能为空");
        }

        user.setUsercode(UidUtil.getMD5UnqId());
        User user1 = userDao.getByUnionKey(user.getPhone(), user.getEmail(), user.getUsercode());
        if(user1 != null){
            if(user.getPhone().equals(user1.getPhone())){
                return BaseResponse.fail("手机号："+user1.getPhone()+"已被注册");
            }else if(user.getEmail().equals(user1.getEmail())){
                return BaseResponse.fail("邮箱："+user1.getEmail()+"已被注册");
            }else if(user.getUsercode().equals(user1.getUsercode())){
                return BaseResponse.fail("用户号："+user1.getUsercode()+"已被注册");
            }
        }

        user.setPassword(encryptPassword(user.getPassword()));
        user.setStatus(1);
        user.setDeleted(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        if(user.getGender()==null) user.setGender(3);
        int count = userDao.insert(user);

        if(count > 0){
            return BaseResponse.success();
        }else{
            return BaseResponse.fail("注册失败，保存用户时失败");
        }
    }

    public BaseResponse deleteUser(Long id){
        if(id==null){
            return BaseResponse.fail("id不能为空");
        }
        boolean result = userDao.deleteById(id) > 0;
        if(result){
            return BaseResponse.success();
        }else{
            return BaseResponse.fail("删除用户失败");
        }
    }

    private String encryptPassword(String password){
        return MD5Util.md5(password);
    }
}
