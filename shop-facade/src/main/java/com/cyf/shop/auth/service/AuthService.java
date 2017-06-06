package com.cyf.shop.auth.service;

import com.cyf.base.common.bean.BaseResponse;
import com.cyf.shop.auth.bean.Authority;
import com.cyf.shop.auth.bean.AuthorityHasNode;
import com.cyf.shop.auth.vo.AuthAssignNodeVo;

import java.util.List;

/**
 * Created by chenyf on 2017/4/9.
 */
public interface AuthService {
    public List<Authority> listActiveAuthByUserId(Long userId);

    public List<Authority> listAllActiveAuth(String sortColumns);

    public BaseResponse<Long> createAuth(Authority auth);

    public BaseResponse deleteAuthWithRelate(Long authId);

    public BaseResponse updateAuth(Authority auth);

    public Authority getAuthById(Long authId);

    public List<AuthorityHasNode> listAuthorityHasNodeByAuthId(Long authId);

    public List<AuthAssignNodeVo> listAuthAssignNode(Long authId);

    public BaseResponse refreshAuthorityHasNode(Long authId, List<Long> newNodeIdList);
}
