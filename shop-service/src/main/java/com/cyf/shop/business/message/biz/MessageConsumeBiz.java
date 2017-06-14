package com.cyf.shop.business.message.biz;

import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.bean.PageParam;
import com.cyf.shop.business.message.dao.MessageConsumeDao;
import com.cyf.shop.business.message.dao.MessageConsumeEsDao;
import com.cyf.shop.message.bean.MessageConsume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Title：消息消费服务biz
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/5/12
 */
@Component
public class MessageConsumeBiz {
    @Autowired
    MessageConsumeDao messageConsumeDao;
    @Autowired
    MessageConsumeEsDao messageConsumeEsDao;

    public boolean create(MessageConsume messageConsume){
        messageConsume.setCreateTime(new Date());
        int count = messageConsumeDao.insert(messageConsume);
        if(count > 0){
            messageConsumeEsDao.insert(messageConsume);
        }
        return count > 0;
    }

    public BaseResponse<List<MessageConsume>> listPageByCond(Map<String, Object> param, PageParam pageParam){
        return messageConsumeEsDao.listByCond(param, pageParam);
    }

    public boolean delete(Long id){
        int count = messageConsumeDao.deleteById(id);
        if(count > 0){
            messageConsumeEsDao.deleteById(id);
        }
        return count > 0;
    }
}
