package com.ofg.infrastructure

import org.gradle.api.Project

class MicroserviceConfigurationFinder {

    protected static final String DEFAULT_MICROSERVICE_CONFIGURATION_FILE_NAME = 'microservice.json'

    File findMicroserviceMetaData(Project project) {
        File microserviceJson = project.sourceSets.main.resources.find { it.name == DEFAULT_MICROSERVICE_CONFIGURATION_FILE_NAME }
        if (!microserviceJson || !microserviceJson.exists()) {
            throw new FileNotFoundException("Configuration file [$DEFAULT_MICROSERVICE_CONFIGURATION_FILE_NAME] hasn't been found on classpath")
        }
        return microserviceJson
    }

}
