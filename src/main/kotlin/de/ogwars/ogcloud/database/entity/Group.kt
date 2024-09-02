package de.ogwars.ogcloud.database.entity

import jakarta.persistence.*

@Entity
@Table(name = "groups")
data class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String,

    @Enumerated(EnumType.STRING)
    val mode: GroupMode,

    @Enumerated(EnumType.STRING)
    val type: GroupType,

    @Enumerated(EnumType.STRING)
    val restartPolicy: RestartPolicy,

    val maxPlayers: Int,
    val joinPower: Int,
)

enum class GroupMode {
    STATIC, DYNAMIC, LOBBY
}

enum class GroupType {
    PROXY,
    SERVER
}

enum class RestartPolicy(val policy: String) {
    NEVER("Never"), ON_FAILURE("OnFailure"), ALWAYS("Always")
}