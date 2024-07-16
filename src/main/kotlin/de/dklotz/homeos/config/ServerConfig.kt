package de.dklotz.homeos.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "source")
data class ServerConfig(
    var root: String = ""
)
