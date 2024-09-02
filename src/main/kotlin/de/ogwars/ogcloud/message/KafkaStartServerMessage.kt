package de.ogwars.ogcloud.message

data class KafkaStartServerMessage(
    val group: String,
    val count: String
)