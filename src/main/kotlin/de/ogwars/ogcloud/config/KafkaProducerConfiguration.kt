package de.ogwars.ogcloud.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaProducerConfiguration(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String
) {

    //fun producerConfiguration(): Map<String, Any> = mapOf(
    //    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
    //    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
    //    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StartServerMessageSerializer::class.java,
    //)
//
    //@Bean
    //fun producerFactory(): ProducerFactory<String, KafkaStartServerMessage> =
    //    DefaultKafkaProducerFactory(producerConfiguration())
//
    //@Bean
    //fun kafkaTemplate(
    //    producerFactory: ProducerFactory<String, KafkaStartServerMessage>
    //): KafkaTemplate<String, KafkaStartServerMessage> = KafkaTemplate(producerFactory)
}