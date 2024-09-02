package de.ogwars.ogcloud.request

import de.ogwars.ogcloud.database.entity.GroupMode
import de.ogwars.ogcloud.database.entity.GroupType
import de.ogwars.ogcloud.database.entity.RestartPolicy

data class CreateGroupRequest(
    val name: String,
    val mode: GroupMode,
    val type: GroupType,
    val restartPolicy: RestartPolicy,
    val maxPlayers: Int,
    val joinPower: Int
)