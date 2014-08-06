package com.ofg.infrastructure

class StubRunnerPluginExtension {
    
    private static final Integer DEFAULT_ZOOKEEPER_PORT = 2181
    private static final Integer DEFAULT_SERVICE_STOPPING_PORT = 18081
    
    /**
     * Version of the micro-deps dependency {@see <a href="https://github.com/4finance/micro-deps">Micro Deps on Github</a>} 
     */
    String microDepsVersion

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
    String stubContainingRepositoryUrl

    /**
     * 
     */
    File microserviceJson 
}
