package com.cyf.shop.message.service;

import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.bean.PageParam;
import com.cyf.shop.message.bean.MessageConsume;

import java.util.List;
import java.util.Map;

/**
 * @Title：消息消费服务
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/5/12 12:09
 */
public interface MessageConsumeService {

    /**
     * 创建一个消息消费记录
     * @param messageConsume
     * @return
     */
    public boolean createMessageConsume(MessageConsume messageConsume);

    /**
     * 查询消息消费记录
     * @param param
     * @param pageParam
     * @return
     */
    public BaseResponse<List<MessageConsume>> listPageByCond(Map<String, Object> param, PageParam pageParam);

    /**
     * 删除消息消费记录
     * @param id
     * @return
     */
    public boolean deleteMessageConsume(Long id);
}
