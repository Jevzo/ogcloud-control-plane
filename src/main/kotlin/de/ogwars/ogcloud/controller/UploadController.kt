package de.ogwars.ogcloud.controller

import de.ogwars.ogcloud.service.UploadService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/upload")
class UploadController(
    private val uploadService: UploadService
) {

    @PostMapping("/{group}/plugins")
    fun uploadPlugins(
        @PathVariable group: String,
        @RequestParam("files") files: List<MultipartFile>,
        @RequestParam(name = "restart_pods", required = false, defaultValue = "false") restartPods: Boolean
    ): String {
        uploadService.uploadPlugins(group, files, restartPods)
        return "Plugins uploaded successfully" // TODO: Return a actual meaningful response
    }

    @PostMapping("/{group}/maps")
    fun uploadMaps(
        @PathVariable group: String,
        @RequestParam("files") files: List<MultipartFile>,
        @RequestParam(name = "restart_pods", required = false, defaultValue = "false") restartPods: Boolean
    ): String {
        uploadService.uploadMaps(group, files, restartPods)
        return "Maps uploaded successfully" // TODO: Return a actual meaningful response
    }

    @PostMapping("/{group}/configs")
    fun uploadConfigs(
        @PathVariable group: String,
        @RequestParam("files") files: List<MultipartFile>,
        @RequestParam(name = "restart_pods", required = false, defaultValue = "false") restartPods: Boolean
    ): String {
        uploadService.uploadConfigFiles(group, files, restartPods)
        return "Configs file uploaded successfully" // TODO: Return a actual meaningful response
    }
}