package de.dklotz.homeos.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties
class AppConfigurations {
    @Bean
    fun serverConfig(): ServerConfig {
        return ServerConfig()
    }
}