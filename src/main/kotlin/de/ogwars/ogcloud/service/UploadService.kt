package de.ogwars.ogcloud.service

import de.ogwars.ogcloud.database.entity.Group
import de.ogwars.ogcloud.database.repository.GroupRepository
import de.ogwars.ogcloud.exception.GroupNotFoundException
import de.ogwars.ogcloud.util.ZipUtil
import io.fabric8.kubernetes.client.KubernetesClient
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class UploadService(
    private val kubernetesClient: KubernetesClient,
    private val podService: PodService,
    private val groupRepository: GroupRepository,
) {

    fun uploadPlugins(groupName: String, files: List<MultipartFile>, restartPods: Boolean) {
        val group = groupRepository.findByName(groupName).orElseThrow {
            throw GroupNotFoundException(groupName)
        }

        files.forEach { file ->
            val tempDir = Files.createTempDirectory("plugins-upload-${group.name}").toFile()
            uploadFiles(processZipFile(tempDir, file), group, "/plugins")
        }

        if (restartPods) {
            kubernetesClient.apps().statefulSets().withName("server-${group.name}").rolling().restart()
        }
    }

    fun uploadMaps(groupName: String, files: List<MultipartFile>, restartPods: Boolean) {
        val group = groupRepository.findByName(groupName).orElseThrow {
            throw GroupNotFoundException(groupName)
        }

        files.forEach { file ->
            val tempDir = Files.createTempDirectory("map-upload-${group.name}").toFile()
            uploadFiles(processZipFile(tempDir, file), group, "/maps")
        }

        if (restartPods) {
            kubernetesClient.apps().statefulSets().withName("server-${group.name}").rolling().restart()
        }
    }

    fun uploadConfigFiles(groupName: String, files: List<MultipartFile>, restartPods: Boolean) {
        val group = groupRepository.findByName(groupName).orElseThrow {
            throw GroupNotFoundException(groupName)
        }

        files.forEach { file ->
            val tempDir = Files.createTempDirectory("config-upload-${group.name}").toFile()
            if (!tempDir.exists()) {
                tempDir.mkdirs()
            }
            val configFile = File(tempDir, file.originalFilename ?: "${UUID.randomUUID()}.yml")
            configFile.createNewFile()
            file.transferTo(configFile)

            uploadFiles(tempDir, group, "/config")
        }

        if (restartPods) {
            kubernetesClient.apps().statefulSets().withName("server-${group.name}").rolling().restart()
        }
    }

    fun uploadFiles(sourceDir: File, group: Group, destPath: String) {
        val pod = podService.createBusyBoxPod(group)
        kubernetesClient.pods().resource(pod).waitUntilReady(2, TimeUnit.MINUTES)

        try {
            sourceDir.walkTopDown().forEach { file ->
                if (file.isFile) {
                    val remotePath = "$destPath/${file.relativeTo(sourceDir).path}".replace("\\", "/")
                    kubernetesClient.pods().withName(pod.metadata.name)
                        .file(remotePath)
                        .upload(file.inputStream())
                }
            }
        } catch (e: Exception) {
            throw e
        } finally {
            kubernetesClient.pods().resource(pod).delete()
            sourceDir.deleteRecursively()
        }
    }

    private fun processZipFile(tempDir: File, file: MultipartFile): File {
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        val zipFile = File(tempDir, file.originalFilename ?: "upload.zip")
        zipFile.createNewFile()
        file.transferTo(zipFile)

        val extractDir = File(tempDir, "extracted")
        ZipUtil.unzipFile(zipFile.absolutePath, extractDir.absolutePath)

        return extractDir
    }
}