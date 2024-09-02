package de.ogwars.ogcloud.service

import de.ogwars.ogcloud.database.entity.Group
import de.ogwars.ogcloud.database.entity.GroupType
import de.ogwars.ogcloud.database.repository.GroupRepository
import de.ogwars.ogcloud.exception.GroupNotFoundException
import de.ogwars.ogcloud.exception.ServiceFoundException
import de.ogwars.ogcloud.exception.StatefulSetFoundException
import io.fabric8.kubernetes.api.model.*
import io.fabric8.kubernetes.api.model.apps.StatefulSetBuilder
import io.fabric8.kubernetes.client.KubernetesClient
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service


@Service
class StatefulSetService(
    private val kubernetesClient: KubernetesClient,
    private val groupRepository: GroupRepository,
    private val environment: Environment
) {

    fun scale(groupName: String, amount: Int) {
        val group = groupRepository.findByName(groupName).orElseThrow {
            throw GroupNotFoundException(groupName)
        }

        val statefulSet = kubernetesClient.apps().statefulSets().withName("server-${group.name}").get()

        kubernetesClient.apps().statefulSets().resource(statefulSet).scale(amount)
    }

    // TODO: Cleanup
    fun checkAndCreateStatefulSet(group: Group) {
        var statefulSet = kubernetesClient.apps().statefulSets().withName("server-${group.name}").get()
        if (statefulSet != null) throw StatefulSetFoundException("server-${group.name}")

        statefulSet = when (group.type) {
            GroupType.PROXY -> StatefulSetBuilder().withNewMetadata().withName("server-${group.name}")
                .addToLabels("app", "server-${group.name}").addToLabels("type", "proxy").endMetadata().withNewSpec()
                .withReplicas(0).withServiceName("service-${group.name}")
                .withNewSelector().addToMatchLabels("app", "server-${group.name}")
                .endSelector().withNewTemplate().withNewMetadata().addToLabels("app", "server-${group.name}")
                .addToLabels("type", "proxy").endMetadata().withNewSpec().withTerminationGracePeriodSeconds(60)
                .addNewContainer().withName("server-${group.name}-container").withImage("ogwars/velocity:3.3.0")
                .withNewLifecycle().withNewPreStop().withNewExec().withCommand("sh", "-c", "kill -TERM 1").endExec()
                .endPreStop().endLifecycle().addNewPort().withContainerPort(25577).endPort()
                .withVolumeMounts(
                    VolumeMountBuilder().withName("config").withMountPath("/config").build(),
                    VolumeMountBuilder().withName("plugins").withMountPath("/plugins").build()
                ).withCommand("/bin/sh", "-c")
                .withArgs(
                    "mkdir -p /config && " +
                            "cp -R /config/* /opt/minecraft/server/ || true && " +
                            "mkdir -p /plugins && " +
                            "mkdir -p /opt/minecraft/server/plugins && " +
                            "cp -R /plugins/* /opt/minecraft/server/plugins || true && " +
                            "exec java -jar -server -Xmx8G -Xms512M /opt/minecraft/server/velocity.jar"
                )
                .withEnv(
                    EnvVarBuilder()
                        .withName("OGCLOUD_SERVER_NAME")
                        .withNewValueFrom()
                        .withNewFieldRef()
                        .withFieldPath("metadata.name")
                        .endFieldRef()
                        .endValueFrom()
                        .build(),
                    EnvVarBuilder()
                        .withName("OGCLOUD_CLUSTER_ADDRESS")
                        .withValue("$(OGCLOUD_SERVER_NAME).service-${group.name}.default.svc.cluster.local")
                        .build()
                )
                .endContainer()
                .withVolumes(
                    VolumeBuilder().withName("config").withNewPersistentVolumeClaim()
                        .withClaimName("config-${group.name}").endPersistentVolumeClaim().build(),
                    VolumeBuilder().withName("plugins").withNewPersistentVolumeClaim()
                        .withClaimName("plugins-${group.name}").endPersistentVolumeClaim().build()
                )
                .endSpec().endTemplate()
                .endSpec().build()

            GroupType.SERVER -> StatefulSetBuilder().withNewMetadata().withName("server-${group.name}")
                .addToLabels("app", "server-${group.name}").addToLabels("type", "server").endMetadata().withNewSpec()
                .withReplicas(0).withServiceName("service-${group.name}")
                .withNewSelector().addToMatchLabels("app", "server-${group.name}")
                .endSelector().withNewTemplate().withNewMetadata().addToLabels("app", "server-${group.name}")
                .addToLabels("type", "server").endMetadata().withNewSpec().withTerminationGracePeriodSeconds(60)
                .addNewContainer().withName("server-${group.name}-container").withImage("ogwars/paper:1.21")
                .withNewLifecycle().withNewPreStop().withNewExec().withCommand("sh", "-c", "kill -TERM 1").endExec()
                .endPreStop().endLifecycle().addNewPort().withContainerPort(25565).endPort()
                .withVolumeMounts(
                    VolumeMountBuilder().withName("maps").withMountPath("/maps").build(),
                    VolumeMountBuilder().withName("config").withMountPath("/config").build(),
                    VolumeMountBuilder().withName("plugins").withMountPath("/plugins").build()
                ).withCommand("/bin/sh", "-c")
                .withArgs(
                    "mkdir -p /maps && " +
                            "cp -R /maps/* /opt/minecraft/server/ || true && " +
                            "mkdir -p /config/config && " +
                            "cp -R /config/* /opt/minecraft/server/ || true && " +
                            "mkdir -p /plugins && " +
                            "mkdir -p /opt/minecraft/server/plugins && " +
                            "cp -R /plugins/* /opt/minecraft/server/plugins || true && " +
                            "exec java -jar -server -Dcom.mojang.eula.agree=true -Xmx4G -Xms512M /opt/minecraft/server/paper.jar --nojline"
                )
                .withEnv(
                    EnvVarBuilder()
                        .withName("OGCLOUD_SERVER_NAME")
                        .withNewValueFrom()
                        .withNewFieldRef()
                        .withFieldPath("metadata.name")
                        .endFieldRef()
                        .endValueFrom()
                        .build(),
                    EnvVarBuilder()
                        .withName("OGCLOUD_CLUSTER_ADDRESS")
                        .withValue("$(OGCLOUD_SERVER_NAME).service-${group.name}.default.svc.cluster.local")
                        .build()
                )
                .endContainer()
                .withVolumes(
                    VolumeBuilder().withName("maps").withNewPersistentVolumeClaim().withClaimName("maps-${group.name}")
                        .endPersistentVolumeClaim().build(),
                    VolumeBuilder().withName("config").withNewPersistentVolumeClaim()
                        .withClaimName("config-${group.name}").endPersistentVolumeClaim().build(),
                    VolumeBuilder().withName("plugins").withNewPersistentVolumeClaim()
                        .withClaimName("plugins-${group.name}").endPersistentVolumeClaim().build()
                )
                .endSpec()
                .endTemplate()
                .endSpec().build()
        }

        kubernetesClient.apps().statefulSets().resource(statefulSet).create()
    }

    fun checkAndCreateService(group: Group) {
        var service = kubernetesClient.services().withName("service-${group.name}").get()
        if (service != null) throw ServiceFoundException("service-${group.name}")

        val inDev = environment.activeProfiles.contains("dev")

        service = when {
            group.type == GroupType.PROXY && inDev -> ServiceBuilder().withNewMetadata()
                .withName("service-${group.name}")
                .addToLabels("type", "proxy").endMetadata().withNewSpec().withType("NodePort")
                .addToSelector("app", "server-${group.name}").addNewPort().withPort(25577)
                .withTargetPort(IntOrString(25577)).withNodePort(30565).endPort()
                .endSpec().build()

            group.type == GroupType.PROXY && !inDev -> ServiceBuilder().withNewMetadata()
                .withName("service-${group.name}")
                .addToLabels("type", "proxy").endMetadata().withNewSpec().withClusterIP("None")
                .addToSelector("app", "server-${group.name}").addNewPort().withPort(25577)
                .withTargetPort(IntOrString(25577)).endPort().endSpec().build()

            group.type == GroupType.SERVER -> ServiceBuilder().withNewMetadata().withName("service-${group.name}")
                .addToLabels("type", "server").endMetadata().withNewSpec().withClusterIP("None")
                .addToSelector("app", "server-${group.name}").addNewPort().withPort(25565)
                .withTargetPort(IntOrString(25565)).endPort().endSpec().build()

            else -> {
                throw IllegalArgumentException("something bad happened") // TODO: Add proper handling
            }
        }

        kubernetesClient.services().resource(service).create()
    }
}