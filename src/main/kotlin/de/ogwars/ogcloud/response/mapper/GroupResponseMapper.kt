package de.ogwars.ogcloud.response.mapper

import de.ogwars.ogcloud.database.entity.Group
import de.ogwars.ogcloud.response.GroupResponse
import org.springframework.stereotype.Component
import java.util.function.Function

@Component("group_response_mapper")
class GroupResponseMapper : Function<Group, GroupResponse> {

    override fun apply(group: Group): GroupResponse {
        return GroupResponse(
            group.id,
            group.name,
            group.mode,
            group.type,
            group.restartPolicy,
            group.maxPlayers,
            group.joinPower
        )
    }
}