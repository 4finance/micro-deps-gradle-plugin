package com.ofg.infrastructure

class CommandExecutor {
    Process execute(String command) {
        return command.execute()
    }
}
