package de.dklotz.homeos.controller

import de.dklotz.homeos.dto.FileDTO
import de.dklotz.homeos.dto.MetaToFileDTO
import de.dklotz.homeos.services.FileService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
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
    fun getAll() : List<FileDTO> {
        return service.getAllFiles()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) : ResponseEntity<Any> {
        val success = service.deleteFile(id)
        if(success) {
            return ResponseEntity(HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
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

    @PutMapping("/meta")
    fun addMetaInfo(@RequestBody metaToFile: MetaToFileDTO) : FileDTO {
        return service.addMetaInfo(id = metaToFile.fileId, metaInfoId = metaToFile.metaId)
    }

    @DeleteMapping("/meta/{fileId}/{metaId}")
    fun removeMetaInfo(@PathVariable fileId: Long, @PathVariable metaId: Long) {
        service.removeMetaInfo(id = fileId, metaInfoId = metaId)
    }
}