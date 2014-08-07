package com.ofg.infrastructure

import org.gradle.api.Project

class MicroserviceConfigurationFinder {

    protected static final String DEFAULT_MICROSERVICE_CONFIGURATION_FILE_NAME = 'microservice.json'

    File findMicroserviceMetaData(Project project) {
        return project.sourceSets.main.resources.find { it.name == DEFAULT_MICROSERVICE_CONFIGURATION_FILE_NAME }
    }

}
