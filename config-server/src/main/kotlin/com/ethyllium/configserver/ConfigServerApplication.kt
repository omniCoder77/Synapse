package com.ethyllium.configserver

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource

@SpringBootApplication
@EnableConfigServer
class ConfigServerApplication

fun main(args: Array<String>) {
    val env = dotenv {
        directory = "./"
        ignoreIfMalformed = true
        ignoreIfMissing = true
    }
    runApplication<ConfigServerApplication>(*args) {
        addInitializers(ApplicationContextInitializer<ConfigurableApplicationContext> { context ->
            val config = env.entries().associate { it.key to it.value }
            context.environment.propertySources.addFirst(
                MapPropertySource("dotenvProperties", config)
            )
        })
    }
}
