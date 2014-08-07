micro-deps-gradle-plugin
======================

Description

### How it works?


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


### Current build status

[![Build Status](https://travis-ci.org/4finance/micro-deps-gradle-plugin.svg?branch=master)](https://travis-ci.org/4finance/micro-deps-gradle-plugin)


### Changelog

To see what has changed in recent versions of Micro deps gradle plugin see the [CHANGELOG](CHANGELOG.md) 
