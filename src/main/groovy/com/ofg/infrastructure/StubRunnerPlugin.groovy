package com.ofg.infrastructure

import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration

import javax.inject.Inject

/**
 * TODO: 
 * 1. Add mockDependencies configuration
 * 2. Add micro-deps version property to extension
 *      a. If not provided download latest version
 * 3. Add zookeeperport, zookeepermockport, repositoryurl to extension
 *      a. Add support for passing filepath to microservices.json(optional)
 * 4. Add runMocks and stopMocks tasks
 */
@Slf4j
class StubRunnerPlugin implements Plugin<Project> {
    static final String EXTENSION_NAME = 'stubRunner'

    @Inject
    StubRunnerPlugin() {
        loggerProxy = new LoggerProxy()
    }

    StubRunnerPlugin(LoggerProxy loggerProxy) {
        this.loggerProxy = loggerProxy
    }

    @Override
    void apply(Project project) {
        project.extensions.create(EXTENSION_NAME, StubRunnerPluginExtension)
        project.task(EXTENSION_NAME) << { Task task ->
            NewVersionFinder newVersionFinder = new MavenNewVersionFinder(project.extensions.uptodate)
            List<Dependency> dependencies = getDependencies(project)
            List<Dependency> dependenciesWithNewVersions = newVersionFinder.findNewer(dependencies)
            if (dependenciesWithNewVersions.isEmpty()) {
                loggerProxy.info(task.logger, NO_NEW_VERSIONS_MESSAGE)
            } else {
                loggerProxy.warn(task.logger, NEW_VERSIONS_MESSAGE_HEAD + dependenciesWithNewVersions.join('\n'))
            }
        }
    }

    private List<Dependency> getDependencies(Project project) {
        ConfigurationFilter configurationFilter = new ConfigurationFilter(project)
        Set<Configuration> configurations = configurationFilter.getConfigurations(project.extensions.uptodate.configurations)
        return getDependencies(configurations)
    }

    private List<Dependency> getDependencies(Set<Configuration> configurations) {
        log.debug("Getting dependencies for configurations [$configurations]")
        return configurations.collectNested { conf ->            
            conf.dependencies.collect { dep ->
                log.debug("Collecting dependency with group: [$dep.group] name: [$dep.name] and version: [$dep.version]")    
                new Dependency(dep.group, dep.name, dep.version) 
            }
        }.flatten().unique()
    }
}
