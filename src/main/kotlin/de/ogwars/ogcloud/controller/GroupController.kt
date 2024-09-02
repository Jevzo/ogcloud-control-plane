package de.ogwars.ogcloud.controller

import de.ogwars.ogcloud.request.CreateGroupRequest
import de.ogwars.ogcloud.response.GroupResponse
import de.ogwars.ogcloud.service.GroupService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/group")
class GroupController(
    private val groupService: GroupService
) {

    @PostMapping
    fun createGroup(@RequestBody createGroupRequest: CreateGroupRequest): GroupResponse {
        return groupService.createGroup(createGroupRequest)
    }
}