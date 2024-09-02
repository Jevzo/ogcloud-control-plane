package de.ogwars.ogcloud.service

import de.ogwars.ogcloud.database.entity.Group
import de.ogwars.ogcloud.exception.VolumeFoundException
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.VolumeResourceRequirementsBuilder
import io.fabric8.kubernetes.client.KubernetesClient
import org.springframework.stereotype.Service

@Service
class VolumeService(
    private val kubernetesClient: KubernetesClient
) {

    fun checkAndCreateConfigVolume(group: Group) {
        var configVolume = kubernetesClient.persistentVolumeClaims().withName("config-${group.name}").get()
        if (configVolume != null) throw VolumeFoundException("config-${group.name}")

        configVolume = createVolume("config-${group.name}")
        kubernetesClient.persistentVolumeClaims().resource(configVolume).create()
    }

    fun checkAndCreateMapsVolume(group: Group) {
        var mapsVolume = kubernetesClient.persistentVolumeClaims().withName("maps-${group.name}").get()
        if (mapsVolume != null) throw VolumeFoundException("maps-${group.name}")

        mapsVolume = createVolume("maps-${group.name}")
        kubernetesClient.persistentVolumeClaims().resource(mapsVolume).create()
    }

    fun checkAndCreatePluginsVolume(group: Group) {
        var pluginsVolume = kubernetesClient.persistentVolumeClaims().withName("plugins-${group.name}").get()
        if (pluginsVolume != null) throw VolumeFoundException("plugins-${group.name}")

        pluginsVolume = createVolume("plugins-${group.name}")
        kubernetesClient.persistentVolumeClaims().resource(pluginsVolume).create()
    }

    private fun createVolume(name: String): PersistentVolumeClaim = PersistentVolumeClaimBuilder()
        .withNewMetadata().withName(name).endMetadata()
        .withNewSpec()
        .withAccessModes("ReadWriteOnce")
        .withResources(
            VolumeResourceRequirementsBuilder()
                .addToRequests("storage", Quantity("2Gi"))
                .build()
        )
        .endSpec()
        .build()
}