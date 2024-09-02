package de.ogwars.ogcloud.service

import de.ogwars.ogcloud.database.entity.Group
import de.ogwars.ogcloud.database.entity.GroupType
import io.fabric8.kubernetes.api.model.Pod
import io.fabric8.kubernetes.api.model.PodBuilder
import io.fabric8.kubernetes.api.model.VolumeBuilder
import io.fabric8.kubernetes.api.model.VolumeMountBuilder
import io.fabric8.kubernetes.client.KubernetesClient
import org.springframework.stereotype.Service

@Service
class PodService(
    private val kubernetesClient: KubernetesClient
) {

    fun createBusyBoxPod(group: Group): Pod {
        val podBuilder = PodBuilder().withNewMetadata().withGenerateName("file-uploader-")
            .addToLabels("app", "file-uploader").endMetadata().withNewSpec().addNewContainer()
            .withName("uploader").withImage("busybox").withCommand("sleep", "3600")

        val pod = when (group.type) {
            GroupType.PROXY -> podBuilder.withVolumeMounts(
                VolumeMountBuilder().withName("config").withMountPath("/config").build(),
                VolumeMountBuilder().withName("plugins").withMountPath("/plugins").build()
            ).endContainer().withVolumes(
                VolumeBuilder().withName("config").withNewPersistentVolumeClaim()
                    .withClaimName("config-${group.name}")
                    .endPersistentVolumeClaim().build(),
                VolumeBuilder().withName("plugins").withNewPersistentVolumeClaim()
                    .withClaimName("plugins-${group.name}")
                    .endPersistentVolumeClaim().build()
            ).endSpec().build()

            GroupType.SERVER -> podBuilder.withVolumeMounts(
                VolumeMountBuilder().withName("config").withMountPath("/config").build(),
                VolumeMountBuilder().withName("maps").withMountPath("/maps").build(),
                VolumeMountBuilder().withName("plugins").withMountPath("/plugins").build()
            ).endContainer().withVolumes(
                VolumeBuilder().withName("config").withNewPersistentVolumeClaim()
                    .withClaimName("config-${group.name}")
                    .endPersistentVolumeClaim().build(),
                VolumeBuilder().withName("maps").withNewPersistentVolumeClaim()
                    .withClaimName("maps-${group.name}")
                    .endPersistentVolumeClaim().build(),
                VolumeBuilder().withName("plugins").withNewPersistentVolumeClaim()
                    .withClaimName("plugins-${group.name}")
                    .endPersistentVolumeClaim().build()
            ).endSpec().build()
        }

        return kubernetesClient.pods().resource(pod).create()
    }
}