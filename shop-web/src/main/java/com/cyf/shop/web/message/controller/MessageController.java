package com.cyf.shop.web.message.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.bean.PageParam;
import com.cyf.base.common.utils.JsonUtil;
import com.cyf.center.common.bean.MessageVo;
import com.cyf.center.common.business.service.MessageService;
import com.cyf.base.common.bean.AjaxResult;
import com.cyf.shop.message.bean.MessageConsume;
import com.cyf.shop.message.service.MessageConsumeService;
import com.cyf.shop.web.message.vo.BatchSendVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/3.
 */
@Controller
@RequestMapping("message/message")
public class MessageController {
    private static final String amqUrl = "http://127.0.0.1:8161/admin";
    @Reference
    MessageService messageService;
    @Reference
    MessageConsumeService messageConsumeService;


    @RequestMapping("sendQueueView")
    public ModelAndView sendQueueView(){
        ModelAndView mv = new ModelAndView();
        mv.addObject("amqUrl", amqUrl);
        return mv;
    }

    @ResponseBody
    @RequestMapping("sendQueueSave")
    public AjaxResult sendQueueSave(MessageVo messageVo){
        messageVo.setToQueue();
        return AjaxResult.convert(messageService.sendMessage(messageVo));
    }

    @RequestMapping("sendTopicView")
    public ModelAndView sendTopicView(){
        ModelAndView mv = new ModelAndView();
        mv.addObject("amqUrl", amqUrl);
        return mv;
    }

    @ResponseBody
    @RequestMapping("sendTopicSave")
    public AjaxResult sendTopicSave(MessageVo messageVo){
        messageVo.setToTopic();
        return AjaxResult.convert(messageService.sendMessage(messageVo));
    }

    @RequestMapping("batchSendMessageView")
    public ModelAndView batchSendMessageView(){
        ModelAndView mv = new ModelAndView();
        mv.addObject("amqUrl", amqUrl);
        return mv;
    }

    @ResponseBody
    @RequestMapping("batchSendMessageSave")
    public AjaxResult batchSendMessageSave(BatchSendVo batchSendVo){
        List<MessageVo> messageVoList = new ArrayList<>();
        for(int i=0; i<batchSendVo.getQueueNumber(); i++){
            MessageVo vo = new MessageVo();
            vo.setToQueue();
            vo.setName(batchSendVo.getQueueName());
            vo.setPayload(batchSendVo.getQueuePayload());
            messageVoList.add(vo);
        }
        for(int i=0; i<batchSendVo.getTopicNumber(); i++){
            MessageVo vo = new MessageVo();
            vo.setToTopic();
            vo.setName(batchSendVo.getTopicName());
            vo.setPayload(batchSendVo.getTopicPayload());
            messageVoList.add(vo);
        }
        return AjaxResult.convert(messageService.batchSendMessage(messageVoList));
    }

    @RequestMapping("messageConsumeList")
    public ModelAndView messageConsumeList(MessageConsume messageConsume, String createTimeStart, String createTimeEnd, PageParam pageParam){
        ModelAndView mv = new ModelAndView();
        Map<String, Object> param = new HashMap<>();
        param.put("type", messageConsume.getType());
        param.put("name", messageConsume.getName());
        param.put("messageDescLike", messageConsume.getMessageDesc());
        param.put("isTimer", messageConsume.getIsTimer());
        param.put("createTimeStart", createTimeStart);
        param.put("createTimeEnd", createTimeEnd);
        pageParam.setSortColumns("id DESC");
        BaseResponse<List<MessageConsume>> response = messageConsumeService.listPageByCond(param, pageParam);
        pageParam.setTotalRecord(response.getTotalRecord());
        mv.addObject("messageList", JsonUtil.toStringBroswer(response.getData()));
        mv.addObject("pageParam", pageParam);
        mv.addObject("type",  messageConsume.getType());
        mv.addObject("name", messageConsume.getName());
        mv.addObject("messageDesc", messageConsume.getMessageDesc());
        mv.addObject("isTimer", messageConsume.getIsTimer());
        mv.addObject("createTimeStart", createTimeStart);
        mv.addObject("createTimeEnd", createTimeEnd);
        return mv;
    }

    @ResponseBody
    @RequestMapping("deleteMessage")
    public AjaxResult deleteMessage(Long id){
        boolean result = messageConsumeService.deleteMessageConsume(id);
        if(result){
            return AjaxResult.success();
        }else{
            return AjaxResult.fail("删除已消费消息失败！");
        }
    }
}
