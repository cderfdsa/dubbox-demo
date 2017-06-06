/*
 * Powered By [cyf.com]
 */

package com.cyf.shop.business.auth.dao;

import org.springframework.stereotype.Repository;
import com.cyf.base.common.dao.EsDao;
import com.cyf.shop.auth.bean.Authority;

@Repository
public class AuthorityEsDao extends EsDao<Authority, Long>{
	/** @see super#setIndex */
	protected void setIndex(){
		this.index ="auth";
	}
	/** @see super#setType */
	protected void setType(){
		this.type ="authority";
	}

}
