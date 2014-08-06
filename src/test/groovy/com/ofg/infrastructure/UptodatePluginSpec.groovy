package com.ofg.infrastructure

import com.ofg.infrastructure.http.WireMockSpec
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.ofg.infrastructure.StubRunnerPlugin.RUN_MOCKS_TASK_NAME
import static com.ofg.infrastructure.StubRunnerPlugin.STOP_MOCKS_TASK_NAME

class UptodatePluginSpec extends WireMockSpec {

    private static final Integer OK_STATUS = 200

    Project project = ProjectBuilder.builder().build()
    LoggerProxy loggerProxy = Mock()
    StubRunnerPlugin plugin = new StubRunnerPlugin(loggerProxy)

    def setup() {
        //def compileConfiguration = project.configurations.create(COMPILE_CONFIGURATION)
        //project.configurations.create(TEST_COMPILE_CONFIGURATION) {extendsFrom compileConfiguration}
        plugin.apply(project)
        project.extensions.stubRunner.zookeeperPort = getHttpServerPort()
    }

    def "should send a request to /stop url when executing stopMocks task"() {
        given:
            stubInteraction(get(urlEqualTo('/stop')), aResponse().withStatus(OK_STATUS))
        when:
            executeStopMocksTask()
        then:
            wireMock.verifyThat(getRequestedFor(urlEqualTo('/stop')))
    }

    private void executeStopMocksTask() {
        project.tasks.getByName(STOP_MOCKS_TASK_NAME).execute()
    }

    private void executeRunMocksTask() {
        project.tasks.getByName(RUN_MOCKS_TASK_NAME).execute()
    }

    private void stubResponseWithADelayOf(int delayInMs) {
        stubInteraction(get(urlMatching('/.*')), aResponse().withFixedDelay(delayInMs))
    }

}
