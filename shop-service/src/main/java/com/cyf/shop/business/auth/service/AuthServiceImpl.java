package com.cyf.shop.business.auth.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.shop.auth.bean.Authority;
import com.cyf.shop.auth.bean.AuthorityHasNode;
import com.cyf.shop.auth.service.AuthService;
import com.cyf.shop.auth.vo.AuthAssignNodeVo;
import com.cyf.shop.business.auth.biz.AuthBiz;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by chenyf on 2017/4/9.
 */
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    AuthBiz authBiz;

    /**
     * 取得一个用户的所有可访问的节点 = 游客可访问节点 + 登陆可访问节点 + 已授权访问节点
     * @param userId
     * @return
     */
    public List<Authority> listActiveAuthByUserId(Long userId){
        return authBiz.listActiveAuthorityByUserId(userId);
    }

    public List<Authority> listAllActiveAuth(String sortColumns){
        return authBiz.listAllActiveAuth(sortColumns);
    }

    public BaseResponse<Long> createAuth(Authority auth){
        return authBiz.createAuthority(auth);
    }

    public BaseResponse deleteAuthWithRelate(Long authId){
        return authBiz.deleteAuthWithRelate(authId);
    }

    public BaseResponse updateAuth(Authority auth){
        return authBiz.updateAuthority(auth);
    }

    public Authority getAuthById(Long authId){
        return authBiz.getAuthorityById(authId);
    }

    public List<AuthorityHasNode> listAuthorityHasNodeByAuthId(Long authId){
        return authBiz.listAuthorityHasNodeByAuthId(authId);
    }

    public List<AuthAssignNodeVo> listAuthAssignNode(Long authId){
        return authBiz.listAuthAssignNode(authId);
    }

    public BaseResponse refreshAuthorityHasNode(Long authId, List<Long> newNodeIdList){
        return authBiz.refreshAuthorityHasNode(authId, newNodeIdList);
    }
}
