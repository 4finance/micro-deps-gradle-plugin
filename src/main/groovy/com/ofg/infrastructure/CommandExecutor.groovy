package com.ofg.infrastructure

class CommandExecutor {
    Process execute(String command) {
        return command.execute()
    }

    int waitForAndLogProcessOutput(Process process) {
        def out = new StringBuilder()
        def err = new StringBuilder()
        process.waitForProcessOutput(out, err)
        if (out) println "out:\n$out"
        if (err) println "err:\n$err"
        return process.exitValue()
    }
}
