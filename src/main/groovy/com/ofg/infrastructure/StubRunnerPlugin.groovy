package com.ofg.infrastructure

import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Dependency
import org.gradle.api.plugins.GroovyPlugin

import javax.inject.Inject

@Slf4j
class StubRunnerPlugin implements Plugin<Project> {
    @PackageScope static final String EXTENSION_NAME = 'stubRunner'
    @PackageScope static final String RUN_MOCKS_TASK_NAME = 'runMocks'
    @PackageScope static final String STOP_MOCKS_TASK_NAME = 'stopMocks'
    @PackageScope static final String MOCK_DEPS_CONFIGURATION_NAME = 'mockDependencies'
    @PackageScope static final String MICROSERVICE_GROUP_NAME = 'microservice'
    @PackageScope static final String WRONG_PARAMS_EXCEPTION_MESSAGE = 'You have to provide all parameters of the command!'
    @PackageScope static final String MICRO_DEPS_GROUP_NAME = 'com.ofg'
    @PackageScope static final String MICRO_DEPS_ARTIFACT_NAME = 'micro-deps'
    @PackageScope static final String FAT_JAR_SUFFIX = 'fatJar'

    private final LoggerProxy loggerProxy
    private final CommandExecutor commandExecutor
    private final MicroserviceConfigurationFinder configurationFinder
    private final DependenciesFinder dependenciesFinder

    @Inject
    StubRunnerPlugin() {
        loggerProxy = new LoggerProxy()
        commandExecutor = new CommandExecutor()
        configurationFinder = new MicroserviceConfigurationFinder()
        dependenciesFinder = new DependenciesFinder()
    }

    StubRunnerPlugin(LoggerProxy loggerProxy, CommandExecutor commandExecutor, MicroserviceConfigurationFinder configurationFinder, DependenciesFinder dependenciesFinder) {
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
        addMicroDepsDependencyToProject(project)
        appendRunMocksTask(project)
        appendStopMocksTask(project)
    }

    private void applyGroovyPlugin(Project project) {
        log.debug('Applying Groovy plugin')
        project.plugins.apply(GroovyPlugin)
    }

    private void createExtensionForPlugin(Project project) {
        log.debug("Creating $StubRunnerPluginExtension extension")
        project.extensions.create(EXTENSION_NAME, StubRunnerPluginExtension)
    }

    private void createMockDependenciesConfiguration(Project project) {
        log.debug("Creating $MOCK_DEPS_CONFIGURATION_NAME configuration if it hasn't been already created")
        project.configurations.maybeCreate(MOCK_DEPS_CONFIGURATION_NAME)
    }

    private void addMicroDepsDependencyToProject(Project project) {
        project.gradle.projectsEvaluated {
            log.debug("Checking if artifact with group: [$MICRO_DEPS_GROUP_NAME] and name: [$MICRO_DEPS_ARTIFACT_NAME] is present")
            Dependency microDepsDependency = project.configurations.mockDependencies.allDependencies.find {
                it.group == MICRO_DEPS_GROUP_NAME && it.name == MICRO_DEPS_ARTIFACT_NAME
            }
            if (!microDepsDependency) {
                String stubRunnerMicroDepsVersion = project.extensions.stubRunner.microDepsVersion
                log.debug("Artifact is not present - adding version [$stubRunnerMicroDepsVersion] from extension")
                project.dependencies.mockDependencies("$MICRO_DEPS_GROUP_NAME:$MICRO_DEPS_ARTIFACT_NAME:$stubRunnerMicroDepsVersion:$FAT_JAR_SUFFIX")
            }
        }
    }

    private void appendRunMocksTask(Project project) {
        Task createdTask = project.tasks.create(RUN_MOCKS_TASK_NAME) << { Task task ->
            Integer zookeperPort = project.extensions.stubRunner.zookeeperPort
            Integer serviceStoppingPort = project.extensions.stubRunner.serviceStoppingPort
            String stubRepositoryUrl = project.extensions.stubRunner.stubContainingRepositoryUrl
            File microserviceMetadata = configurationFinder.findMicroserviceMetaData(project)
            String microDepsFatJarName = dependenciesFinder.getMicroDepsFatJarName(project)
            GString runMocksCommand = "java -jar $microDepsFatJarName -p $zookeperPort -mp $serviceStoppingPort -f ${microserviceMetadata?.absolutePath} -r $stubRepositoryUrl"
            task.logger.info("Executing command [$runMocksCommand]")
            if (runMocksCommand.values.contains(null)) {
                task.logger.error(WRONG_PARAMS_EXCEPTION_MESSAGE)
                throw new WrongMicroDepsExecutionParams("$WRONG_PARAMS_EXCEPTION_MESSAGE Your command looked like this [$runMocksCommand]")
            }
            commandExecutor.execute(runMocksCommand)
        }
        createdTask.group = MICROSERVICE_GROUP_NAME
        createdTask.description = 'Downloads micro-deps fat jar, grabs stub dependencies from the provided repository and starts local Zookeeper and stubs'
    }

    private void appendStopMocksTask(Project project) {
        Task createdTask = project.task(STOP_MOCKS_TASK_NAME) << { Task task ->
            Integer zookeperPort = project.extensions.stubRunner.zookeeperPort
            String stopMocksUrl = "http://localhost:$zookeperPort/stop"
            task.logger.info("Stopping mocks by calling $stopMocksUrl")
            new HTTPBuilder(stopMocksUrl).get([:])
            task.logger.info("Mocks stopped successfully")
        }
        createdTask.group = MICROSERVICE_GROUP_NAME
        createdTask.description = 'Sends a request to the started micro-deps server to stop all executed stubs, zookeeper instance and itself'
    }

}
