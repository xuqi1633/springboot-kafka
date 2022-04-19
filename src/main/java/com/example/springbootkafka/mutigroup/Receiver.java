package com.example.springbootkafka.mutigroup;

import com.example.springbootkafka.entity.PracticalAdvice;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author 3y
 * 消费MQ的消息
 */
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Receiver {

    @KafkaListener(topics = "multi-group")
    public void consumer(ConsumerRecord<String, PracticalAdvice> consumerRecord, @Header(KafkaHeaders.GROUP_ID) String topicGroupId) {
        Optional<PracticalAdvice> kafkaMessage = Optional.ofNullable(consumerRecord.value());
        kafkaMessage.ifPresent(practicalAdvice -> log.info("topicGroupId:  " + topicGroupId + "-------" + "DATA:  " + practicalAdvice));
    }
}
