package de.ogwars.ogcloud.database.repository

import de.ogwars.ogcloud.database.entity.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GroupRepository : JpaRepository<Group, Long> {
    fun findByName(name: String): Optional<Group>
}