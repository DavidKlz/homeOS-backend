package de.dklotz.homeos.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class WebSecurity {
    @Bean
    open fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            securityMatcher("/api/**")
            authorizeHttpRequests {
                authorize(anyRequest, permitAll)
            }
            httpBasic { }
        }
        return http.build()
    }
}