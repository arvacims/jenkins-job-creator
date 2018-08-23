package com.github.arvacims.jobcreator

import org.springframework.boot.env.YamlPropertySourceLoader
import org.springframework.core.env.AbstractEnvironment
import org.springframework.core.io.ClassPathResource
import java.io.IOException

class EnvironmentFromResource(location: String = "/application.yml") : AbstractEnvironment() {

    init {
        try {
            val resource = ClassPathResource(location)
            val loadedPropertySources = YamlPropertySourceLoader().load(location, resource)
            loadedPropertySources.forEach { propertySources.addLast(it) }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

}
