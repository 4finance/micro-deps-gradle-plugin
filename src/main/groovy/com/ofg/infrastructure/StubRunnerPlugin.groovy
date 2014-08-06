package com.ofg.infrastructure

import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration

import javax.inject.Inject

/**
 * TODO:
 * 1. Add micro-deps version property to extension
 *      a. If not provided download latest version
 * 2. Add runMocks tasks
 */
@Slf4j
class StubRunnerPlugin implements Plugin<Project> {
    public static final String EXTENSION_NAME = 'stubRunner'
    public static final String RUN_MOCKS_TASK_NAME = 'runMocks'
    public static final String STOP_MOCKS_TASK_NAME = 'stopMocks'
    public static final String MOCK_DEPS_CONFIGURATION_NAME = 'mockDependencies'


    private final LoggerProxy loggerProxy

    @Inject
    StubRunnerPlugin() {
        loggerProxy = new LoggerProxy()
    }

    StubRunnerPlugin(LoggerProxy loggerProxy) {
        this.loggerProxy = loggerProxy
    }

    @Override
    void apply(Project project) {
        createExtensionForPlugin(project)
        createMockDependenciesConfiguration(project)
        appendRunMocksTask(project)
        appendStopMocksTask(project)
    }

    private void createMockDependenciesConfiguration(Project project) {
        project.configurations.create(MOCK_DEPS_CONFIGURATION_NAME)
    }

    private void createExtensionForPlugin(Project project) {
        project.extensions.create(EXTENSION_NAME, StubRunnerPluginExtension)
    }

    private void appendRunMocksTask(Project project) {
        project.task(RUN_MOCKS_TASK_NAME) << { Task task ->

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
