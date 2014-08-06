package com.ofg.infrastructure

import org.gradle.api.Project

class DependenciesFinder {
    protected static final String MICRO_DEPS_LIB_NAME = 'micro-deps'

    String getMicroDepsFatJarName(Project project) {
        return project.configurations.mockDependencies.find { it.name.startsWith MICRO_DEPS_LIB_NAME }
    }
}
