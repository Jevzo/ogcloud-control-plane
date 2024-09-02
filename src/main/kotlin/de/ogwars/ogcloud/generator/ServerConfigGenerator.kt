package de.ogwars.ogcloud.generator

import de.ogwars.ogcloud.database.entity.Group
import de.ogwars.ogcloud.service.UploadService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.*

@Component("server_config_generator")
class ServerConfigGenerator(
    private val uploadService: UploadService,
    @Value("\${spring.velocity.secret}")
    private val velocitySecret: String
) {

    private val yaml = Yaml()

    fun generatePaperYaml(
        group: Group
    ) {
        val tempDir = Files.createTempDirectory("startup-config-upload-${group.name}-${UUID.randomUUID()}").toFile()
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        val configFile = File(tempDir, "paper-global.yml")
        configFile.createNewFile()

        val config = mapOf(
            "_version" to 29,
            "block-updates" to mapOf(
                "disable-chorus-plant-updates" to false,
                "disable-mushroom-block-updates" to false,
                "disable-noteblock-updates" to false,
                "disable-tripwire-updates" to false
            ),
            "chunk-loading-advanced" to mapOf(
                "auto-config-send-distance" to true,
                "player-max-concurrent-chunk-generates" to 0,
                "player-max-concurrent-chunk-loads" to 0
            ),
            "chunk-loading-basic" to mapOf(
                "player-max-chunk-generate-rate" to -1.0,
                "player-max-chunk-load-rate" to 100.0,
                "player-max-chunk-send-rate" to 75.0
            ),
            "chunk-system" to mapOf(
                "gen-parallelism" to "default",
                "io-threads" to -1,
                "worker-threads" to -1
            ),
            "collisions" to mapOf(
                "enable-player-collisions" to false,
                "send-full-pos-for-hard-colliding-entities" to true
            ),
            "commands" to mapOf(
                "fix-target-selector-tag-completion" to true,
                "suggest-player-names-when-null-tab-completions" to true,
                "time-command-affects-all-worlds" to false
            ),
            "console" to mapOf(
                "enable-brigadier-completions" to true,
                "enable-brigadier-highlighting" to true,
                "has-all-permissions" to false
            ),
            "item-validation" to mapOf(
                "book" to mapOf(
                    "author" to 8192,
                    "page" to 16384,
                    "title" to 8192
                ),
                "book-size" to mapOf(
                    "page-max" to 2560,
                    "total-multiplier" to 0.98
                ),
                "display-name" to 8192,
                "lore-line" to 8192,
                "resolve-selectors-in-books" to false
            ),
            "logging" to mapOf(
                "deobfuscate-stacktraces" to true
            ),
            "messages" to mapOf(
                "kick" to mapOf(
                    "authentication-servers-down" to "<lang:multiplayer.disconnect.authservers_down>",
                    "connection-throttle" to "Connection throttled! Please wait before reconnecting.",
                    "flying-player" to "<lang:multiplayer.disconnect.flying>",
                    "flying-vehicle" to "<lang:multiplayer.disconnect.flying>"
                ),
                "no-permission" to "<red>I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.",
                "use-display-name-in-quit-message" to false
            ),
            "misc" to mapOf(
                "chat-threads" to mapOf(
                    "chat-executor-core-size" to -1,
                    "chat-executor-max-size" to -1
                ),
                "client-interaction-leniency-distance" to "default",
                "compression-level" to "default",
                "fix-entity-position-desync" to true,
                "load-permissions-yml-before-plugins" to true,
                "max-joins-per-tick" to 30,
                "region-file-cache-size" to 256,
                "strict-advancement-dimension-check" to false,
                "use-alternative-luck-formula" to false,
                "use-dimension-type-for-custom-spawners" to false
            ),
            "packet-limiter" to mapOf(
                "all-packets" to mapOf(
                    "action" to "KICK",
                    "interval" to 7.0,
                    "max-packet-rate" to 500.0
                ),
                "kick-message" to "<red><lang:disconnect.exceeded_packet_rate>",
                "overrides" to mapOf(
                    "ServerboundPlaceRecipePacket" to mapOf(
                        "action" to "DROP",
                        "interval" to 4.0,
                        "max-packet-rate" to 5.0
                    )
                )
            ),
            "player-auto-save" to mapOf(
                "max-per-tick" to -1,
                "rate" to -1
            ),
            "proxies" to mapOf(
                "bungee-cord" to mapOf(
                    "online-mode" to true
                ),
                "proxy-protocol" to false,
                "velocity" to mapOf(
                    "enabled" to true,
                    "online-mode" to true,
                    "secret" to velocitySecret
                )
            ),
            "scoreboards" to mapOf(
                "save-empty-scoreboard-teams" to true,
                "track-plugin-scoreboards" to false
            ),
            "spam-limiter" to mapOf(
                "incoming-packet-threshold" to 300,
                "recipe-spam-increment" to 1,
                "recipe-spam-limit" to 20,
                "tab-spam-increment" to 1,
                "tab-spam-limit" to 500
            ),
            "spark" to mapOf(
                "enable-immediately" to false,
                "enabled" to true
            ),
            "timings" to mapOf(
                "enabled" to false,
                "hidden-config-entries" to listOf(
                    "database",
                    "proxies.velocity.secret"
                ),
                "history-interval" to 300,
                "history-length" to 3600,
                "server-name" to "Unknown Server",
                "server-name-privacy" to false,
                "url" to "https://timings.aikar.co/",
                "verbose" to true
            ),
            "unsupported-settings" to mapOf(
                "allow-headless-pistons" to false,
                "allow-permanent-block-break-exploits" to false,
                "allow-piston-duplication" to false,
                "allow-tripwire-disarming-exploits" to false,
                "allow-unsafe-end-portal-teleportation" to false,
                "compression-format" to "ZLIB",
                "perform-username-validation" to true,
                "skip-vanilla-damage-tick-when-shield-blocked" to false
            ),
            "watchdog" to mapOf(
                "early-warning-delay" to 10000,
                "early-warning-every" to 5000
            )
        )

        configFile.writer(charset = StandardCharsets.UTF_8).use {
            yaml.dump(config, it)
            uploadService.uploadFiles(tempDir, group, "/config/config")
        }
    }

    fun generateBukkitYaml(
        group: Group
    ) {
        val tempDir = Files.createTempDirectory("startup-config-upload-${group.name}-${UUID.randomUUID()}").toFile()
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        val configFile = File(tempDir, "bukkit.yml")
        configFile.createNewFile()

        val config = mapOf(
            "settings" to mapOf(
                "allow-end" to false,
                "warn-on-overload" to true,
                "permissions-file" to "permissions.yml",
                "update-folder" to "update",
                "plugin-profiling" to false,
                "connection-throttle" to 4000,
                "query-plugins" to true,
                "deprecated-verbose" to "default",
                "shutdown-message" to "Server closed",
                "minimum-api" to "none",
                "use-map-color-cache" to true
            ),
            "spawn-limits" to mapOf(
                "monsters" to 70,
                "animals" to 10,
                "water-animals" to 5,
                "water-ambient" to 20,
                "water-underground-creature" to 5,
                "axolotls" to 5,
                "ambient" to 15
            ),
            "chunk-gc" to mapOf(
                "period-in-ticks" to 600
            ),
            "ticks-per" to mapOf(
                "animal-spawns" to 400,
                "monster-spawns" to 1,
                "water-spawns" to 1,
                "water-ambient-spawns" to 1,
                "water-underground-creature-spawns" to 1,
                "axolotl-spawns" to 1,
                "ambient-spawns" to 1,
                "autosave" to 6000
            ),
            "aliases" to "now-in-commands.yml"
        )

        configFile.writer(charset = StandardCharsets.UTF_8).use {
            yaml.dump(config, it)
            uploadService.uploadFiles(tempDir, group, "/config")
        }
    }

    fun generateSpigotYaml(
        group: Group
    ) {
        val tempDir = Files.createTempDirectory("startup-config-upload-${group.name}-${UUID.randomUUID()}").toFile()
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        val configFile = File(tempDir, "spigot.yml")
        configFile.createNewFile()

        val config = mapOf(
            "settings" to mapOf(
                "debug" to false,
                "sample-count" to 12,
                "bungeecord" to false,
                "save-user-cache-on-stop-only" to false,
                "log-villager-deaths" to true,
                "log-named-deaths" to true,
                "timeout-time" to 60,
                "restart-on-crash" to false,
                "restart-script" to "./start.sh",
                "user-cache-size" to 1000,
                "player-shuffle" to 0,
                "attribute" to mapOf(
                    "maxAbsorption" to mapOf("max" to 2048.0),
                    "maxHealth" to mapOf("max" to 2048.0),
                    "movementSpeed" to mapOf("max" to 2048.0),
                    "attackDamage" to mapOf("max" to 2048.0)
                ),
                "netty-threads" to 4,
                "moved-too-quickly-multiplier" to 10.0,
                "moved-wrongly-threshold" to 0.0625
            ),
            "messages" to mapOf(
                "whitelist" to "You are not whitelisted on this server!",
                "unknown-command" to "Unknown command. Type \"/help\" for help.",
                "server-full" to "The server is full!",
                "outdated-client" to "Outdated client! Please use {0}",
                "outdated-server" to "Outdated server! I'm still on {0}",
                "restart" to "Server is restarting"
            ),
            "world-settings" to mapOf(
                "default" to mapOf(
                    "below-zero-generation-in-existing-chunks" to true,
                    "max-tnt-per-tick" to 100,
                    "growth" to mapOf(
                        "cactus-modifier" to 100,
                        "cane-modifier" to 100,
                        "melon-modifier" to 100,
                        "mushroom-modifier" to 100,
                        "pumpkin-modifier" to 100,
                        "sapling-modifier" to 100,
                        "beetroot-modifier" to 100,
                        "carrot-modifier" to 100,
                        "potato-modifier" to 100,
                        "torchflower-modifier" to 100,
                        "wheat-modifier" to 100,
                        "netherwart-modifier" to 100,
                        "vine-modifier" to 100,
                        "cocoa-modifier" to 100,
                        "bamboo-modifier" to 100,
                        "sweetberry-modifier" to 100,
                        "kelp-modifier" to 100,
                        "twistingvines-modifier" to 100,
                        "weepingvines-modifier" to 100,
                        "cavevines-modifier" to 100,
                        "glowberry-modifier" to 100,
                        "pitcherplant-modifier" to 100
                    ),
                    "entity-tracking-range" to mapOf(
                        "players" to 128,
                        "animals" to 96,
                        "monsters" to 96,
                        "misc" to 96,
                        "display" to 128,
                        "other" to 64
                    ),
                    "seed-village" to 10387312,
                    "seed-desert" to 14357617,
                    "seed-igloo" to 14357618,
                    "seed-jungle" to 14357619,
                    "seed-swamp" to 14357620,
                    "seed-monument" to 10387313,
                    "seed-shipwreck" to 165745295,
                    "seed-ocean" to 14357621,
                    "seed-outpost" to 165745296,
                    "seed-endcity" to 10387313,
                    "seed-slime" to 987234911,
                    "seed-nether" to 30084232,
                    "seed-mansion" to 10387319,
                    "seed-fossil" to 14357921,
                    "seed-portal" to 34222645,
                    "seed-ancientcity" to 20083232,
                    "seed-trailruins" to 83469867,
                    "seed-trialchambers" to 94251327,
                    "seed-buriedtreasure" to 10387320,
                    "seed-mineshaft" to "default",
                    "seed-stronghold" to "default",
                    "entity-activation-range" to mapOf(
                        "animals" to 32,
                        "monsters" to 32,
                        "raiders" to 64,
                        "misc" to 16,
                        "water" to 16,
                        "villagers" to 32,
                        "flying-monsters" to 32,
                        "wake-up-inactive" to mapOf(
                            "animals-max-per-tick" to 4,
                            "animals-every" to 1200,
                            "animals-for" to 100,
                            "monsters-max-per-tick" to 8,
                            "monsters-every" to 400,
                            "monsters-for" to 100,
                            "villagers-max-per-tick" to 4,
                            "villagers-every" to 600,
                            "villagers-for" to 100,
                            "flying-monsters-max-per-tick" to 8,
                            "flying-monsters-every" to 200,
                            "flying-monsters-for" to 100
                        ),
                        "villagers-work-immunity-after" to 100,
                        "villagers-work-immunity-for" to 20,
                        "villagers-active-for-panic" to true,
                        "tick-inactive-villagers" to true,
                        "ignore-spectators" to false
                    ),
                    "max-tick-time" to mapOf(
                        "tile" to 50,
                        "entity" to 50
                    ),
                    "item-despawn-rate" to 6000,
                    "mob-spawn-range" to 8,
                    "arrow-despawn-rate" to 1200,
                    "trident-despawn-rate" to 1200,
                    "nerf-spawner-mobs" to false,
                    "thunder-chance" to 100000,
                    "unload-frozen-chunks" to false,
                    "hunger" to mapOf(
                        "jump-walk-exhaustion" to 0.05,
                        "jump-sprint-exhaustion" to 0.2,
                        "combat-exhaustion" to 0.1,
                        "regen-exhaustion" to 6.0,
                        "swim-multiplier" to 0.01,
                        "sprint-multiplier" to 0.1,
                        "other-multiplier" to 0.0
                    ),
                    "merge-radius" to mapOf(
                        "item" to 0.5,
                        "exp" to -1.0
                    ),
                    "ticks-per" to mapOf(
                        "hopper-transfer" to 8,
                        "hopper-check" to 1
                    ),
                    "hopper-amount" to 1,
                    "hopper-can-load-chunks" to false,
                    "zombie-aggressive-towards-villager" to true,
                    "enable-zombie-pigmen-portal-spawns" to true,
                    "simulation-distance" to "default",
                    "view-distance" to "default",
                    "hanging-tick-frequency" to 100,
                    "end-portal-sound-radius" to 0,
                    "wither-spawn-sound-radius" to 0,
                    "dragon-death-sound-radius" to 0,
                    "verbose" to false
                )
            ),
            "commands" to mapOf(
                "tab-complete" to 0,
                "send-namespaced" to true,
                "spam-exclusions" to listOf("/skill"),
                "replace-commands" to listOf(
                    "setblock",
                    "summon",
                    "testforblock",
                    "tellraw"
                ),
                "log" to true,
                "silent-commandblock-console" to false
            ),
            "advancements" to mapOf(
                "disable-saving" to false,
                "disabled" to listOf("minecraft:story/disabled")
            ),
            "players" to mapOf(
                "disable-saving" to false
            ),
            "config-version" to 12,
            "stats" to mapOf(
                "disable-saving" to false,
                "forced-stats" to emptyMap<String, Any>()
            )
        )

        configFile.writer(charset = StandardCharsets.UTF_8).use {
            yaml.dump(config, it)
            uploadService.uploadFiles(tempDir, group, "/config")
        }
    }

    // TODO: Find a better way to handle properties files
    fun generateProperties(
        group: Group
    ) {
        val tempDir = Files.createTempDirectory("startup-config-upload-${group.name}-${UUID.randomUUID()}").toFile()
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        val configFile = File(tempDir, "server.properties")
        configFile.createNewFile()

        val serverProperties = "accepts-transfers=false\n" +
                "allow-flight=false\n" +
                "allow-nether=false\n" +
                "broadcast-console-to-ops=false\n" +
                "broadcast-rcon-to-ops=true\n" +
                "bug-report-link=\n" +
                "debug=false\n" +
                "difficulty=easy\n" +
                "enable-command-block=false\n" +
                "enable-jmx-monitoring=false\n" +
                "enable-query=false\n" +
                "enable-rcon=false\n" +
                "enable-status=true\n" +
                "enforce-secure-profile=true\n" +
                "enforce-whitelist=false\n" +
                "entity-broadcast-range-percentage=100\n" +
                "force-gamemode=false\n" +
                "function-permission-level=2\n" +
                "gamemode=survival\n" +
                "generate-structures=true\n" +
                "generator-settings={}\n" +
                "hardcore=false\n" +
                "hide-online-players=false\n" +
                "initial-disabled-packs=\n" +
                "initial-enabled-packs=vanilla\n" +
                "level-name=world\n" +
                "level-seed=\n" +
                "level-type=minecraft\\:normal\n" +
                "log-ips=false\n" +
                "max-chained-neighbor-updates=1000000\n" +
                "max-players=${group.maxPlayers}\n" +
                "max-tick-time=60000\n" +
                "max-world-size=29999984\n" +
                "motd=A Minecraft Server\n" +
                "network-compression-threshold=256\n" +
                "online-mode=false\n" +
                "op-permission-level=4\n" +
                "player-idle-timeout=0\n" +
                "prevent-proxy-connections=false\n" +
                "pvp=true\n" +
                "query.port=25565\n" +
                "rate-limit=0\n" +
                "rcon.password=\n" +
                "rcon.port=25575\n" +
                "region-file-compression=deflate\n" +
                "require-resource-pack=false\n" +
                "resource-pack=\n" +
                "resource-pack-id=\n" +
                "resource-pack-prompt=\n" +
                "resource-pack-sha1=\n" +
                "server-ip=\n" +
                "server-port=25565\n" +
                "simulation-distance=10\n" +
                "spawn-animals=true\n" +
                "spawn-monsters=true\n" +
                "spawn-npcs=true\n" +
                "spawn-protection=16\n" +
                "sync-chunk-writes=true\n" +
                "text-filtering-config=\n" +
                "use-native-transport=true\n" +
                "view-distance=10\n" +
                "white-list=false"

        configFile.writeText(serverProperties, StandardCharsets.UTF_8)
        uploadService.uploadFiles(tempDir, group, "/config")
    }
}