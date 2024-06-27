package de.dklotz.homeos.controller

import de.dklotz.homeserver.services.VaultFileService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/vault/file")
class VaultFileController(val service: VaultFileService) {
}