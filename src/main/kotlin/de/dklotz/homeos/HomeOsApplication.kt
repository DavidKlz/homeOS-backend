package de.dklotz.homeos

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HomeOsApplication

fun main(args: Array<String>) {
    runApplication<HomeOsApplication>(*args)
}
