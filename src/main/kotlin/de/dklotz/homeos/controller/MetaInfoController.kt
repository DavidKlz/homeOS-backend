package de.dklotz.homeos.controller

import de.dklotz.homeserver.services.MetaInfoService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/meta")
class MetaInfoController(val service: MetaInfoService) {
}