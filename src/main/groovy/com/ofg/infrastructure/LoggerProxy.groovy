package com.ofg.infrastructure

import org.slf4j.Logger

class LoggerProxy {
    void warn(Logger logger, String warning) {
        logger.warn(warning)
    }
    void info(Logger logger, String warning) {
        logger.info(warning)
    }
}
