micro-deps-gradle-plugin
======================

Gradle plugin that adds run/stub mocks tasks and proper configrations to your projects

### How it works?

The plugin does the following things:

* applies **'java' plugin** if it hasn't been done so already
* creates **'stubrunner' extension**
* adds **'stubrunner'** configuration** if it hasn't been done so already
* adds **'com.ofg:micro-deps:VERSION:fatJar'** dependency as follows 
(VERSION can be passed either from extension or latest will be chosen):
```
dependencies {
    mockDependencies 'com.ofg:micro-deps:VERSION:fatJar'
}

```
* appends **'runMocks' task** that downloads micro-deps dependency from jcenter and starts all the necessary stubs
(the stubs to be downloaded are taken from *microservice.json* configuration file)
* appends **'stopMocks' task** that stops all the started stubs

### How to install it?

#### Step 1: Add dependency to jcenter and to the plugin

```
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.ofg:micro-deps-gradle-plugin:0.0.1'
    }
}
```

#### Step 2: Add the plugin to your build (gradle.build)

```
   apply plugin: 'com.ofg.infrastructure.stubrunner'
```

#### Step 3: Execute runMocks task

```
   gradle runMocks
```

#### Step 4: Execute stopMocks task

```
   gradle stopMocks
```

### Configuration

You can configure the plugin via the **stubRunner** extension (below you can find its params with their default values)

```
stubRunner {

    /**
     * Version of the micro-deps dependency {@see <a href="https://github.com/4finance/micro-deps">Micro Deps on Github</a>} 
     */
    microDepsVersion = '+' //defaults to latest

    /**
     * Port at which Zookeeper will set itself up. The default is {@see StubRunnerPluginExtension.DEFAULT_ZOOKEEPER_PORT}
     */
    zookeeperPort = 2181

    /**
     * Port at which {@see <a href="https://github.com/4finance/micro-deps">micro-deps'</a>} 
     * running process listens to POST requests to stop itself 
     */
    serviceStoppingPort = 18081

    /**
     * Url to root path of the repository containing stubs of your applications
     */
    stubContainingRepositoryUrl = 'http://repo.4finance.net/nexus/content/repositories/Pipeline'

    /**
     * File pointing to the JSON configuration of your microservice
     */
    microserviceJson // TODO: not yet implemented :)
}
```

### Current build status

[![Build Status](https://travis-ci.org/4finance/micro-deps-gradle-plugin.svg?branch=master)](https://travis-ci.org/4finance/micro-deps-gradle-plugin)


### Changelog

To see what has changed in recent versions of Micro deps gradle plugin see the [CHANGELOG](CHANGELOG.md) 
