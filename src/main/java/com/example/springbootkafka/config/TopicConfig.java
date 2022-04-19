package com.example.springbootkafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * @author xuqi
 */
@Configuration
public class TopicConfig {

    @Value("${tpd.topic-name}")
    private String topicName;

    @Bean
    public NewTopic adviceTopic() {
        return TopicBuilder.name(topicName).partitions(3).replicas(1).build();
        // return new NewTopic(topicName, 3, (short) 1);
        // return new KafkaAdmin.NewTopics(...)
    }

    @Bean
    public NewTopic multiGroup() {
        return TopicBuilder.name("multi-group").partitions(3).replicas(1).build();
    }
}
