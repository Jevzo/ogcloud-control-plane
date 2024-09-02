package de.ogwars.ogcloud.controller

import de.ogwars.ogcloud.service.StatefulSetService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/statefulset")
class StateFulSetController(
    private val statefulSetService: StatefulSetService
) {

    @PostMapping("/{group}/scale")
    fun scale(
        @PathVariable group: String,
        @RequestParam(name = "amount", required = true) amount: Int
    ): ResponseEntity<String> {
        statefulSetService.scale(group, amount)
        return ResponseEntity.ok().build()
    }
}