package de.ogwars.ogcloud.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaTopicConfiguration(
    @Value("\${spring.kafka.topic.servers}")
    private val serversTopic: String,
    @Value("\${spring.kafka.topic.notify}")
    private val notifyTopic: String
) {

    //@Bean
    //fun serversTopic(): NewTopic = TopicBuilder.name(serversTopic).build()
//
    //@Bean
    //fun notifyTopic(): NewTopic = TopicBuilder.name(notifyTopic).build()
}