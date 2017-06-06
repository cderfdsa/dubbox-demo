package com.cyf.common.message.receive.shop;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cyf.base.common.mqconfig.TestMQConfig;
import com.cyf.base.common.utils.LoggerWrapper;
import com.cyf.center.common.bean.MessageVo;
import com.cyf.shop.message.bean.MessageConsume;
import com.cyf.shop.message.service.MessageConsumeService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

/**
 * shop服务的定时任务接收器
 * Created by chenyf on 2017/3/14.
 */
@Component
public class TimerReceiver {
    static final LoggerWrapper LOG = LoggerWrapper.getLoggerWrapper();

    @Reference
    MessageConsumeService messageConsumeService;

    //接收cron类型的定时任务消息
    @JmsListener(containerFactory="defaultQueueLCFactory", destination = TestMQConfig.TEST_CRON_TIMER_JOB, subscription = "cron_timer_receiver")
    public void cronTimerReceive(final Message<String> sprMessage) throws JMSException {
        LOG.info("-----------cronTimerReceive start------------");

        MessageVo messageVo = new MessageVo();
        messageVo.setName(TestMQConfig.TEST_CRON_TIMER_JOB);
        messageVo.setPayload(sprMessage.getPayload());
        messageVo.setType(1);
        createMessageConsume("cronTimerReceive", messageVo);

        LOG.info("-----------cronTimerReceive end------------");
    }

    @JmsListener(containerFactory="defaultQueueLCFactory", destination = TestMQConfig.TEST_REPEAT_FOREVER_JOB, subscription = "repeat_forever_receiver")
    public void repeatForeverTimerReceive(final Message<String> sprMessage) throws JMSException {
        LOG.info("-----------repeatForeverTimerReceive start------------");

        MessageVo messageVo = new MessageVo();
        messageVo.setName(TestMQConfig.TEST_REPEAT_FOREVER_JOB);
        messageVo.setPayload(sprMessage.getPayload());
        messageVo.setType(1);
        createMessageConsume("repeatForeverTimerReceive", messageVo);

        LOG.info("-----------repeatForeverTimerReceive end------------");
    }

    @JmsListener(containerFactory="defaultQueueLCFactory", destination = TestMQConfig.TEST_LIMIT_REPEAT_JOB, subscription = "limitRepeatTimerReceive")
    public void limitRepeatTimerReceive(final Message<String> sprMessage) throws JMSException {
        LOG.info("-----------limitRepeatTimerReceive start------------");

        MessageVo messageVo = new MessageVo();
        messageVo.setName(TestMQConfig.TEST_LIMIT_REPEAT_JOB);
        messageVo.setPayload(sprMessage.getPayload());
        messageVo.setType(1);
        messageVo.setMessageDesc("有限制次数的重复任务");
        createMessageConsume("limitRepeatTimerReceive", messageVo);

        LOG.info("-----------limitRepeatTimerReceive end------------");
    }

    @JmsListener(containerFactory="defaultQueueLCFactory", destination = "test_repeat.count", subscription = "repeatCronReceive")
    public void repeatCronReceive(final Message<String> sprMessage) throws JMSException {
        LOG.info("-----------repeatCronReceive start------------");

        MessageVo messageVo = new MessageVo();
        messageVo.setName("test_repeat.count");
        messageVo.setPayload(sprMessage.getPayload());
        messageVo.setType(1);
        messageVo.setMessageDesc("从2017-06-05 00:00:00开始，每600秒执行一次的任务");
        createMessageConsume("repeatCronReceive", messageVo);

        LOG.info("-----------repeatCronReceive end------------");
    }

    @JmsListener(containerFactory="defaultQueueLCFactory", destination = "test_daily.cron", subscription = "dailyCronReceive")
    public void dailyCronReceive(final Message<String> sprMessage) throws JMSException {
        LOG.info("-----------dailyCronReceive start------------");

        MessageVo messageVo = new MessageVo();
        messageVo.setName("test_daily.cron");
        messageVo.setPayload(sprMessage.getPayload());
        messageVo.setType(1);
        messageVo.setMessageDesc("每天20:05分执行的任务");
        createMessageConsume("dailyCronReceive", messageVo);

        LOG.info("-----------dailyCronReceive end------------");
    }

    @JmsListener(containerFactory="defaultQueueLCFactory", destination = "test_interval.cron", subscription = "intervalCronReceive")
    public void intervalCronReceive(final Message<String> sprMessage) throws JMSException {
        LOG.info("-----------intervalCronReceive start------------");

        MessageVo messageVo = new MessageVo();
        messageVo.setName("test_interval.cron");
        messageVo.setPayload(sprMessage.getPayload());
        messageVo.setType(1);
        messageVo.setMessageDesc("每2分钟执行一次的任务");
        createMessageConsume("intervalCronReceive", messageVo);

        LOG.info("-----------intervalCronReceive end------------");
    }

    @JmsListener(containerFactory="defaultQueueLCFactory", destination = "test_daily.period", subscription = "dailyPeriodReceive")
    public void dailyPeriodReceive(final Message<String> sprMessage) throws JMSException {
        LOG.info("-----------dailyPeriodReceive start------------");

        MessageVo messageVo = new MessageVo();
        messageVo.setName("test_daily.period");
        messageVo.setPayload(sprMessage.getPayload());
        messageVo.setType(1);
        messageVo.setMessageDesc("每天8点-10点每6分钟执行一次");
        createMessageConsume("dailyPeriodReceive", messageVo);

        LOG.info("-----------dailyPeriodReceive end------------");
    }

    @JmsListener(containerFactory="defaultQueueLCFactory", destination = "test_weekly.cron", subscription = "weeklyCronReceive")
    public void weeklyCronReceive(final Message<String> sprMessage) throws JMSException {
        LOG.info("-----------weeklyCronReceive start------------");

        MessageVo messageVo = new MessageVo();
        messageVo.setName("test_weekly.cron");
        messageVo.setPayload(sprMessage.getPayload());
        messageVo.setType(1);
        messageVo.setMessageDesc("每周三、周四的15:30分执行的任务");
        createMessageConsume("weeklyCronReceive", messageVo);

        LOG.info("-----------weeklyCronReceive end------------");
    }

    @JmsListener(containerFactory="defaultQueueLCFactory", destination = "test_monthly.cron", subscription = "monthlyCronReceive")
    public void monthlyCronReceive(final Message<String> sprMessage) throws JMSException {
        LOG.info("-----------monthlyCronReceive start------------");

        MessageVo messageVo = new MessageVo();
        messageVo.setName("test_monthly.cron");
        messageVo.setPayload(sprMessage.getPayload());
        messageVo.setType(1);
        messageVo.setMessageDesc("从2017-06-05开始，每月的8号、10号、18号的19:50执行的任务，到2017-08-03结束");
        createMessageConsume("monthlyCronReceive", messageVo);

        LOG.info("-----------monthlyCronReceive end------------");
    }


    @JmsListener(containerFactory="defaultQueueLCFactory", destination = "test_once.job", subscription = "onceJobReceive")
    public void onceJobReceive(final Message<String> sprMessage) throws JMSException {
        LOG.info("-----------onceJobReceive start------------");

        MessageVo messageVo = new MessageVo();
        messageVo.setName("once.job");
        messageVo.setPayload(sprMessage.getPayload());
        messageVo.setType(1);
        messageVo.setMessageDesc("在2017-07-03 15:22:49执行一次的任务");
        createMessageConsume("onceJobReceive", messageVo);

        LOG.info("-----------onceJobReceive end------------");
    }

    @JmsListener(containerFactory="defaultQueueLCFactory", destination = "test_repeat.forever", subscription = "repeatForeverReceive")
    public void repeatForeverReceive(final Message<String> sprMessage) throws JMSException {
        LOG.info("-----------repeatForeverReceive start------------");

        MessageVo messageVo = new MessageVo();
        messageVo.setName("test_repeat.forever");
        messageVo.setPayload(sprMessage.getPayload());
        messageVo.setType(1);
        messageVo.setMessageDesc("从2017-06-15 15:26:10开始，每200秒执行一次的任务");
        createMessageConsume("repeatForeverReceive", messageVo);

        LOG.info("-----------repeatForeverReceive end------------");
    }

    @JmsListener(containerFactory="defaultQueueLCFactory", destination = "test_repeat.period", subscription = "repeatPeriodReceive")
    public void repeatPeriodReceive(final Message<String> sprMessage) throws JMSException {
        LOG.info("-----------repeatPeriodReceive start------------");

        MessageVo messageVo = new MessageVo();
        messageVo.setName("test_repeat.period");
        messageVo.setPayload(sprMessage.getPayload());
        messageVo.setType(1);
        messageVo.setMessageDesc("从2017-06-28 15:31:35到2017-10-03 15:31:40每300秒执行一次的任务");
        createMessageConsume("repeatPeriodReceive", messageVo);

        LOG.info("-----------repeatPeriodReceive end------------");
    }

    @JmsListener(containerFactory="defaultQueueLCFactory", destination = "test_self.defined", subscription = "selfDefinedReceive")
    public void selfDefinedReceive(final Message<String> sprMessage) throws JMSException {
        LOG.info("-----------selfDefinedReceive start------------");

        MessageVo messageVo = new MessageVo();
        messageVo.setName("test_self.defined");
        messageVo.setPayload(sprMessage.getPayload());
        messageVo.setType(1);
        messageVo.setMessageDesc("自定义的，每天下午的 2点到2点59分(整点开始，每隔5分触发) ");
        createMessageConsume("selfDefinedReceive", messageVo);

        LOG.info("-----------selfDefinedReceive end------------");
    }

    private void createMessageConsume(String methodName, MessageVo messageVo){
        MessageConsume messageConsume = new MessageConsume();
        messageConsume.setName(messageVo.getName());
        messageConsume.setType(messageVo.getType());
        messageConsume.setPayload(messageVo.getPayload());
        messageConsume.setConsumer(methodName);
        messageConsume.setIsTimer(1);
        messageConsume.setMessageDesc(messageVo.getMessageDesc());
        messageConsumeService.createMessageConsume(messageConsume);
    }
}
