package com.jomofisher

import java.io.File

class Function(untrimmed: String, val parms: List<Function> = listOf()) {
    val name = untrimmed.trim()

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append(name)
        if (!parms.isEmpty()) {
            builder.append("(")
            builder.append(parms.joinToString(","))
            builder.append(")")
        }
        return builder.toString()
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return this.toString() == other.toString()
    }
}

private class State(
        val line: String,
        var pos: Int = 0,
        var inQuote: Boolean = false,
        var parenDepth: Int = 0)

fun parse(file: File): List<Function> {
    return file.readLines().map { parse(it) }.toList()
}

fun parse(line: String): Function {
    return parseFunction(State(line))
}

private fun parseFunction(state: State): Function {
    var name = ""
    while (state.pos < state.line.length) {
        val c = state.line[state.pos]
        if (state.inQuote) {
            when (c) {
                '"' -> {
                    state.inQuote = false
                    state.pos++
                }
                else -> {
                    name += c
                    state.pos++
                }
            }
        } else {
            when (c) {
                '"' -> {
                    state.inQuote = true
                    state.pos++
                }
                '(' -> {
                    state.pos++
                    state.parenDepth++
                    return Function(name, parseList(state))
                }
                ')' -> {
                    state.parenDepth--
                    if (state.parenDepth < 0) {
                        throw RuntimeException("Unmatched right paren: ${state.line}")
                    }
                    return Function(name, listOf())
                }
                ',' -> {
                    return Function(name, listOf())
                }
                else -> {
                    name += c
                    state.pos++
                }
            }
        }
    }
    return Function(name, listOf())
}

private fun parseList(state: State): List<Function> {
    val result = mutableListOf<Function>()
    while (state.pos < state.line.length) {
        when (state.line[state.pos]) {
            ',' -> state.pos++
            ')' -> {
                state.pos++
                return result
            }
            else -> {
                result += parseFunction(state)
            }
        }
    }
    return result
}