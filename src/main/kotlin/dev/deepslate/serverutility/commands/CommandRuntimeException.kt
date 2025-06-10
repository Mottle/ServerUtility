package dev.deepslate.serverutility.commands

class CommandRuntimeException(message: String) : Exception(message) {
    companion object {
        fun of(message: String) = CommandRuntimeException("Command returned with error: $message")
    }
}