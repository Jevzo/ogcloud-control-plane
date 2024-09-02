package de.ogwars.ogcloud.response

data class PodsStatusResponse(
    val pods: MutableSet<PodStatusResponse>
)

class PodStatusResponse(
    val name: String,
    val status: String,
    val startTime: String,
)
