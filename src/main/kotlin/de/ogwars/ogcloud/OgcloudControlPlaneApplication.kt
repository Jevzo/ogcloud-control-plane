package de.ogwars.ogcloud

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OgcloudControlPlaneApplication

fun main(args: Array<String>) {
    runApplication<OgcloudControlPlaneApplication>(*args)
}
