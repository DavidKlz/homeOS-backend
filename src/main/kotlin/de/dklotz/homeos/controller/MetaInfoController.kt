package de.dklotz.homeos.controller

import de.dklotz.homeos.dto.MetaInfoDTO
import de.dklotz.homeos.services.MetaInfoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/meta")
class MetaInfoController(val service: MetaInfoService) {
    @GetMapping("/all")
    fun getAllMetaInfo(): List<MetaInfoDTO> {
        return service.getAllMetaInfo()
    }

    @GetMapping("/all/{label}")
    fun getAllMetaInfoByLabel(@PathVariable label: String): List<MetaInfoDTO> {
        return service.getAllMetaInfoByLabel(label)
    }

    @GetMapping("/{id}")
    fun getMetaInfo(@PathVariable id: Long): MetaInfoDTO {
        return service.getMetaInfo(id)
    }

    @PostMapping("/")
    fun safeMetaInfo(metaInfo: MetaInfoDTO) : MetaInfoDTO {
        return service.safeMetaInfo(metaInfo)
    }

    @DeleteMapping("/{id}")
    fun removeMetaInfo(@PathVariable id: Long) : ResponseEntity<Boolean> {
        val success = service.removeMetaInfo(id)
        if(success){
            return ResponseEntity.ok(true)
        }
        return ResponseEntity.notFound().build()
    }
}