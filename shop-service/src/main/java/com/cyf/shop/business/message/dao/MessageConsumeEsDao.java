/*
 * Powered By [cyf.com]
 */

package com.cyf.shop.business.message.dao;

import com.cyf.shop.message.bean.MessageConsume;
import org.springframework.stereotype.Repository;
import com.cyf.base.common.dao.EsDao;

@Repository
public class MessageConsumeEsDao extends EsDao<MessageConsume, Long>{
	/** @see super#setIndex */
    protected void setIndex(){
		this.index ="message";
	}
	/** @see super#setType */
    protected void setType(){
		this.type ="messageConsume";
	}


}
