package de.ogwars.ogcloud.generator

import de.ogwars.ogcloud.database.entity.Group
import de.ogwars.ogcloud.service.UploadService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.*

@Component("proxy_config_generator")
class ProxyConfigGenerator(
    private val uploadService: UploadService,
    @Value("\${spring.velocity.secret}")
    private val velocitySecret: String
) {

    // TODO: Find a better way to handle toml files
    fun generateVelocityToml(
        group: Group
    ) {
        val tempDir = Files.createTempDirectory("startup-config-upload-${group.name}-${UUID.randomUUID()}").toFile()
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        val configFile = File(tempDir, "velocity.toml")
        configFile.createNewFile()

        val velocity = "# Config version. Do not change this\n" +
                "config-version = \"2.7\"\n" +
                "bind = \"0.0.0.0:25577\"\n" +
                "motd = \"<dark_gray>➦ <yellow>OgCloud <gray>▰<dark_gray>▰ <aqua>high performance cloud <dark_gray>▎ <yellow>v0.0.1\n<dark_gray>➥ <red>k8s connection established, plugin failure?\"\n" +
                "show-max-players = ${group.maxPlayers}\n" +
                "online-mode = true\n" +
                "force-key-authentication = true\n" +
                "prevent-client-proxy-connections = false\n" +
                "player-info-forwarding-mode = \"modern\"\n" +
                "forwarding-secret-file = \"forwarding.secret\"\n" +
                "announce-forge = false\n" +
                "kick-existing-players = false\n" +
                "ping-passthrough = \"DISABLED\"\n" +
                "[servers]\n" +
                "fallback = \"server-fallback-0.service-fallback.default.svc.cluster.local:25565\"\n" +
                "try = [\n" +
                "    \"fallback\"\n" +
                "]\n" +
                "[forced-hosts]\n" +
                "[advanced]\n" +
                "compression-threshold = 256\n" +
                "compression-level = -1\n" +
                "login-ratelimit = 0\n" +
                "connection-timeout = 5000\n" +
                "read-timeout = 30000\n" +
                "haproxy-protocol = false\n" +
                "tcp-fast-open = false\n" +
                "bungee-plugin-message-channel = true\n" +
                "show-ping-requests = false\n" +
                "failover-on-unexpected-server-disconnect = true\n" +
                "announce-proxy-commands = true\n" +
                "log-command-executions = false\n" +
                "log-player-connections = true\n" +
                "accepts-transfers = true\n" +
                "[query]\n" +
                "enabled = false\n" +
                "port = 25577\n" +
                "map = \"Velocity\"\n" +
                "show-plugins = false\n"

        configFile.writeText(velocity, StandardCharsets.UTF_8)
        uploadService.uploadFiles(tempDir, group, "/config")
    }

    fun generateForwardingSecret(group: Group) {
        val tempDir = Files.createTempDirectory("startup-config-upload-${group.name}-${UUID.randomUUID()}").toFile()
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        val configFile = File(tempDir, "forwarding.secret")
        configFile.createNewFile()

        configFile.writeText(velocitySecret, StandardCharsets.UTF_8)
        uploadService.uploadFiles(tempDir, group, "/config")
    }
}