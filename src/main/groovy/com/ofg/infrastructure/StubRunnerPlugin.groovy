package com.ofg.infrastructure

import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin

import javax.inject.Inject

/**
 * TODO:
 * 1. Add micro-deps version property to extension
 *      a. If not provided download latest version
 */
@Slf4j
class StubRunnerPlugin implements Plugin<Project> {
    protected static final String EXTENSION_NAME = 'stubRunner'
    protected static final String RUN_MOCKS_TASK_NAME = 'runMocks'
    protected static final String STOP_MOCKS_TASK_NAME = 'stopMocks'
    protected static final String MOCK_DEPS_CONFIGURATION_NAME = 'mockDependencies'

    private final LoggerProxy loggerProxy
    private final CommandExecutor commandExecutor
    private final ConfigurationFinder configurationFinder
    private final DependenciesFinder dependenciesFinder

    @Inject
    StubRunnerPlugin() {
        loggerProxy = new LoggerProxy()
        commandExecutor = new CommandExecutor()
        configurationFinder = new ConfigurationFinder()
        dependenciesFinder = new DependenciesFinder()
    }

    StubRunnerPlugin(LoggerProxy loggerProxy, CommandExecutor commandExecutor, ConfigurationFinder configurationFinder, DependenciesFinder dependenciesFinder) {
        this.loggerProxy = loggerProxy
        this.commandExecutor = commandExecutor
        this.configurationFinder = configurationFinder
        this.dependenciesFinder = dependenciesFinder
    }

    @Override
    void apply(Project project) {
        applyGroovyPlugin(project)
        createExtensionForPlugin(project)
        createMockDependenciesConfiguration(project)
        appendRunMocksTask(project)
        appendStopMocksTask(project)
    }

    private void applyGroovyPlugin(Project project) {
        project.plugins.apply(GroovyPlugin)
    }

    private void createExtensionForPlugin(Project project) {
        project.extensions.create(EXTENSION_NAME, StubRunnerPluginExtension)
    }

    private void createMockDependenciesConfiguration(Project project) {
        project.configurations.create(MOCK_DEPS_CONFIGURATION_NAME)
    }

    private void appendRunMocksTask(Project project) {
        project.task(RUN_MOCKS_TASK_NAME) << { Task task ->
            Integer zookeperPort = project.extensions.stubRunner.zookeeperPort
            Integer serviceStoppingPort = project.extensions.stubRunner.serviceStoppingPort
            String stubRepositoryUrl = project.extensions.stubRunner.stubContainingRepositoryUrl
            File microserviceMetadata = configurationFinder.findMicroserviceMetaData(project)
            String microDepsFatJarName = dependenciesFinder.getMicroDepsFatJarName(project)
            GString runMocksCommand = "java -jar $microDepsFatJarName -p $zookeperPort -mp $serviceStoppingPort -f ${microserviceMetadata?.absolutePath} -r $stubRepositoryUrl"
            task.logger.info("Executing command [$runMocksCommand]")
            if(runMocksCommand.values.contains(null)) {
                task.logger.error('You have to provide all parameters of the command!')
                throw new WrongMicroDepsExecutionParams()
            }
            commandExecutor.execute(runMocksCommand)
        }
    }

    private void appendStopMocksTask(Project project) {
        project.task(STOP_MOCKS_TASK_NAME) << { Task task ->
            Integer zookeperPort = project.extensions.stubRunner.zookeeperPort
            String stopMocksUrl = "http://localhost:$zookeperPort/stop"
            task.logger.info("Stopping mocks by calling $stopMocksUrl")
            new HTTPBuilder(stopMocksUrl).get([:])
            task.logger.info("Mocks stopped successfully")
        }
    }

}
