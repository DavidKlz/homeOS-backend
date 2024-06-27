package de.dklotz.homeos.controller

import de.dklotz.homeos.dto.FileDTO
import de.dklotz.homeos.dto.FileListDTO
import de.dklotz.homeserver.services.FileService
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/api/file")
class FileController(val service: FileService) {
    @GetMapping("info/{id}")
    fun getFileInfo(@PathVariable id: Long) : FileDTO {
        return service.getFile(id).get()
    }

    @GetMapping("/all")
    fun getAll() : List<FileListDTO> {
        return service.getAllFiles()
    }

    @GetMapping("/{id}")
    @ResponseBody
    fun serveFile(@PathVariable id: Long) : ResponseEntity<ByteArray> {
        val file = service.serveFileInfos(id)
        if(file.isPresent) {
            val headers = HttpHeaders()
            headers.add("Content-Type", file.get().mimetype)
            return ResponseEntity.ok().headers(headers).body(file.get().file)
        }
        return ResponseEntity.notFound().build()
    }

    @PostMapping("/upload")
    fun handleFileUpload(@RequestParam("files") files: Array<MultipartFile>) : ResponseEntity<String> {
        service.storeFile(files);
        return ResponseEntity.ok().build()
    }

    @GetMapping("/sync")
    fun sync() : ResponseEntity<String> {
        service.syncFiles()
        return ResponseEntity.ok().build()
    }
}