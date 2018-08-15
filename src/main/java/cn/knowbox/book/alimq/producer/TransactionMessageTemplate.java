package cn.knowbox.book.alimq.producer;

import javax.annotation.Resource;

import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.TransactionProducerBean;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;

import cn.knowbox.book.alimq.event.MessageEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hengxi
 * @version 1.0
 * @desc 事务消息生产者
 * @date 2018/8/13
 */
@Slf4j
public class TransactionMessageTemplate {

    @Resource
    private TransactionProducerBean transactionProducer;

    /****
     * @Description: 同步发送顺序消息
     * @Param: [event]
     * @param: sharding 分区顺序消息中区分不同分区的关键字段，sharding key 于普通消息的 key 是完全不同的概念。
     * @Author: jibaole
     */
    public SendResult send(MessageEvent event,LocalTransactionExecuter executer) {
    	if(event == null) {
    		throw new RuntimeException("event is null.");
    	}
        log.info("start to send message. [topic: {}, tag: {}]", event.getTopic(), event.getTag());
        if (StringUtils.isEmpty(event.getTopic())  || null == event.getDomain()) {
            throw new RuntimeException("topic, or body is null.");
        }
        Message message = new Message(event.getTopic(), event.getTag(), SerializationUtils.serialize(event));
        message.setKey(event.generateTxId());
        SendResult result = this.transactionProducer.send(message, executer, null);
        log.info("send message success. "+ result.toString());
        return result;
    }
}
