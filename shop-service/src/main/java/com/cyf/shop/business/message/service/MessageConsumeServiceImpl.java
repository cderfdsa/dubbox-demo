package com.cyf.shop.business.message.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.bean.PageParam;
import com.cyf.shop.business.message.biz.MessageConsumeBiz;
import com.cyf.shop.message.bean.MessageConsume;
import com.cyf.shop.message.service.MessageConsumeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @Title：消息消费服务实现类
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/5/12
 */
@Service
public class MessageConsumeServiceImpl implements MessageConsumeService {
    @Autowired
    MessageConsumeBiz messageConsumeBiz;

    /**
     * 创建一个消息消费记录
     * @param messageConsume
     * @return
     */
    public boolean createMessageConsume(MessageConsume messageConsume){
        return messageConsumeBiz.create(messageConsume);
    }

    /**
     * 查询消息消费记录
     * @param param
     * @param pageParam
     * @return
     */
    public BaseResponse<List<MessageConsume>> listPageByCond(Map<String, Object> param, PageParam pageParam){
        return messageConsumeBiz.listPageByCond(param, pageParam);
    }

    /**
     * 删除消息消费记录
     * @param id
     * @return
     */
    public boolean deleteMessageConsume(Long id){
        return messageConsumeBiz.delete(id);
    }
}
