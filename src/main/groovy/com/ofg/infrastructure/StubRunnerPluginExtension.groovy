package com.ofg.infrastructure

class StubRunnerPluginExtension {
    
    private static final Integer DEFAULT_ZOOKEEPER_PORT = 2181
    private static final Integer DEFAULT_SERVICE_STOPPING_PORT = 18081
    private static final String DEFAULT_STUB_CONTAINING_REPOSITORY_URL = 'http://repo.4finance.net/nexus/content/repositories/Pipeline'
    public static final String LATEST_VERSION = '+'

    /**
     * Version of the micro-deps dependency {@see <a href="https://github.com/4finance/micro-deps">Micro Deps on Github</a>} 
     */
    String microDepsVersion = LATEST_VERSION

    /**
     * Port at which Zookeeper will set itself up. The default is {@see StubRunnerPluginExtension.DEFAULT_ZOOKEEPER_PORT}
     */
    Integer zookeeperPort = DEFAULT_ZOOKEEPER_PORT

    /**
     * Port at which {@see <a href="https://github.com/4finance/micro-deps">micro-deps'</a>} 
     * running process listens to POST requests to stop itself 
     */
    Integer serviceStoppingPort = DEFAULT_SERVICE_STOPPING_PORT

    /**
     * Url to root path of the repository containing stubs of your applications
     */
    String stubContainingRepositoryUrl = DEFAULT_STUB_CONTAINING_REPOSITORY_URL

    /**
     * File pointing to the JSON configuration of your microservice
     */
    File microserviceJson 
}
