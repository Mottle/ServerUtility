package dev.deepslate.serverutility.command.lexer

import java.util.*

internal class CommandLexer {

    companion object {
        const val ARG_PREFIX = '%'
        const val STRING_ARG = 's'
        const val INT_ARG = 'i'
        const val FLOAT_ARG = 'f'
        const val BOOL_ARG = 'b'
        const val RESOURCE_LOCATION_ARG = 'r'
        const val ARG_L = '<'
        const val ARG_R = '>'
    }

    private fun scanWord(queue: Queue<Char>): TokenType {
        val sb = StringBuilder()

        while (queue.isNotEmpty()) {
            val c = queue.poll()

            if (c.isWhitespace()) break
            else if (c.isLetter() || c.isDigit() || c == '_') sb.append(c)
            else throw IllegalArgumentException("Command Lexer state in word: Unexpected character: $c")
        }
        return TokenType.Word(sb.toString())
    }

    private fun scanFor(queue: Queue<Char>, constructor: (String) -> TokenType): TokenType {
        val sb = StringBuilder()
        if (queue.poll() != ARG_L) throw IllegalArgumentException("Command Lexer state in argument: Excepted $ARG_L")
        while (queue.isNotEmpty()) {
            val c = queue.poll()
            if (c == ARG_R) return constructor(sb.toString())
            sb.append(c)
        }
        throw IllegalArgumentException("Command Lexer state in argument: Excepted $ARG_R")
    }

    private fun scanArg(queue: Queue<Char>): TokenType {
        if (queue.poll() != ARG_PREFIX) throw IllegalArgumentException("Command Lexer state in argument: Excepted $ARG_PREFIX")
        return when (val c = queue.poll()) {
            STRING_ARG -> scanFor(queue, TokenType::StringArg)
            INT_ARG -> scanFor(queue, TokenType::IntArg)
            FLOAT_ARG -> scanFor(queue, TokenType::FloatArg)
            BOOL_ARG -> scanFor(queue, TokenType::BoolArg)
            RESOURCE_LOCATION_ARG -> scanFor(queue, TokenType::ResourceLocationArg)
            else -> throw IllegalArgumentException("Command Lexer state in argument: Unexpected character: $c")
        }
    }

    fun scan(text: String): List<TokenType> {
        val queue = LinkedList(text.toList())
        val results = arrayListOf<TokenType>()

        while (queue.isNotEmpty()) {
            val c = queue.peek()
            if (c.isWhitespace()) queue.poll()
            else if (c == ARG_PREFIX) {
                results += scanArg(queue)
            } else if (c.isLetter()) {
                results += scanWord(queue)
            } else throw IllegalArgumentException("Command Lexer: Unexpected character: $c")
        }
        return results
    }
}