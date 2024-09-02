package de.ogwars.ogcloud.service

import de.ogwars.ogcloud.database.entity.Group
import de.ogwars.ogcloud.database.entity.GroupType
import de.ogwars.ogcloud.database.repository.GroupRepository
import de.ogwars.ogcloud.exception.GroupExistsException
import de.ogwars.ogcloud.generator.ProxyConfigGenerator
import de.ogwars.ogcloud.generator.ServerConfigGenerator
import de.ogwars.ogcloud.request.CreateGroupRequest
import de.ogwars.ogcloud.response.GroupResponse
import de.ogwars.ogcloud.response.mapper.GroupResponseMapper
import org.springframework.stereotype.Service as SpringService

@SpringService
class GroupService(
    private val groupRepository: GroupRepository,
    private val groupResponseMapper: GroupResponseMapper,
    private val proxyConfigGenerator: ProxyConfigGenerator,
    private val serverConfigGenerator: ServerConfigGenerator,
    private val statefulSetService: StatefulSetService,
    private val volumeService: VolumeService
) {

    fun createGroup(
        createGroupRequest: CreateGroupRequest
    ): GroupResponse {
        groupRepository.findByName(createGroupRequest.name).ifPresent {
            throw GroupExistsException(createGroupRequest.name)
        }

        val group = Group(
            name = createGroupRequest.name,
            mode = createGroupRequest.mode,
            type = createGroupRequest.type,
            restartPolicy = createGroupRequest.restartPolicy,
            maxPlayers = createGroupRequest.maxPlayers,
            joinPower = createGroupRequest.joinPower
        )

        volumeService.checkAndCreateConfigVolume(group)
        volumeService.checkAndCreatePluginsVolume(group)

        when (group.type) {
            GroupType.SERVER -> {
                volumeService.checkAndCreateMapsVolume(group)

                serverConfigGenerator.generateBukkitYaml(group)
                serverConfigGenerator.generateSpigotYaml(group)
                serverConfigGenerator.generateProperties(group)
                serverConfigGenerator.generatePaperYaml(group)
            }

            GroupType.PROXY -> {
                proxyConfigGenerator.generateVelocityToml(group)
                proxyConfigGenerator.generateForwardingSecret(group)
            }
        }

        statefulSetService.checkAndCreateStatefulSet(group)
        statefulSetService.checkAndCreateService(group)

        return groupResponseMapper.apply(groupRepository.save(group))
    }
}