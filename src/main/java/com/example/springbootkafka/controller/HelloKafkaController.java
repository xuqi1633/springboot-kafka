package com.example.springbootkafka.controller;

import com.example.springbootkafka.entity.PracticalAdvice;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * @author xuqi
 */
@RestController
@Slf4j
public class HelloKafkaController {

    private final KafkaTemplate<String, Object> template;
    private final String topicName;
    private final int messagesPerRequest;
    private CountDownLatch latch;


    public HelloKafkaController(KafkaTemplate<String, Object> template,
                                @Value("${tpd.topic-name}") String topicName,
                                @Value("${tpd.messages-per-request}") int messagesPerRequest) {
        this.template = template;
        this.topicName = topicName;
        this.messagesPerRequest = messagesPerRequest;
    }

    @GetMapping("/multi")
    public String multiGroup() {
        IntStream.range(0, messagesPerRequest)
                .forEach(i -> this.template.send("multi-group", String.valueOf(i),
                        new PracticalAdvice("A Practical Advice", i)));
        return "Multi Group";
    }

    @GetMapping("/hello")
    public String hello() throws InterruptedException {
        latch = new CountDownLatch(messagesPerRequest);
        IntStream.range(0, messagesPerRequest)
                .forEach(i -> this.template.send(topicName, String.valueOf(i),
                        new PracticalAdvice("A Practical Advice", i)));
        latch.await(60, TimeUnit.SECONDS);
        log.info("All messages received");
        return "Hello Kafka";
    }

    @KafkaListener(topics = "advice-topic", clientIdPrefix = "json",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenAsObject(ConsumerRecord<String, PracticalAdvice> cr,
                               @Payload PracticalAdvice payload) {
        log.info("Logger 1 [JSON] received key {}: Type [{}] | Payload: {} | Record: {}", cr.key(),
                typeIdHeader(cr.headers()), payload, cr);
        latch.countDown();
    }

    @KafkaListener(topics = "advice-topic", clientIdPrefix = "string",
            containerFactory = "kafkaListenerStringContainerFactory")
    public void listenAsString(ConsumerRecord<String, String> cr,
                               @Payload String payload) {
        log.info("Logger 2 [String] received key {}: Type [{}] | Payload: {} | Record: {}", cr.key(),
                typeIdHeader(cr.headers()), payload, cr);
        latch.countDown();
    }

    @KafkaListener(topics = "advice-topic", clientIdPrefix = "bytearray",
            containerFactory = "kafkaListenerByteArrayContainerFactory")
    public void listenAsByteArray(ConsumerRecord<String, byte[]> cr,
                                  @Payload byte[] payload) {
        log.info("Logger 3 [ByteArray] received key {}: Type [{}] | Payload: {} | Record: {}", cr.key(),
                typeIdHeader(cr.headers()), payload, cr);
        latch.countDown();
    }

    private String typeIdHeader(Headers headers) {
        return StreamSupport.stream(headers.spliterator(), false)
                .filter(header -> "__TypeId__".equals(header.key()))
                .findFirst().map(header -> new String(header.value())).orElse("N/A");
    }
}
