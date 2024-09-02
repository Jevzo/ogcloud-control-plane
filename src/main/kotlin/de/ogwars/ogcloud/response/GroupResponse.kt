package de.ogwars.ogcloud.response

import de.ogwars.ogcloud.database.entity.GroupMode
import de.ogwars.ogcloud.database.entity.GroupType
import de.ogwars.ogcloud.database.entity.RestartPolicy

data class GroupResponse(
    val id: Long? = null,
    val name: String,
    val mode: GroupMode,
    val type: GroupType,
    val restartPolicy: RestartPolicy,
    val maxPlayers: Int,
    val joinPower: Int
)