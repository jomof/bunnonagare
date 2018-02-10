package com.jomofisher

import java.io.File

open class Node

class Label(val label: String) : Node() {
    override fun toString(): String {
        return label
    }

    override fun hashCode(): Int {
        return label.hashCode()
    }
}

class Function(val name: String, val parms: List<Node>) : Node() {
    init {
        if (parms.isEmpty()) throw RuntimeException("should be label")
    }

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

fun parse(file: File): List<Node> {
    return file.readLines().map { parse(it) }.toList()
}

fun parse(line: String): Node {
    return parseFunction(State(line))
}

fun parseFunction(line: String): Function {
    return parse(line) as? Function ?: throw RuntimeException("unexpected")
}

private fun parseFunction(state: State): Node {
    var name = ""
    while (state.pos < state.line.length) {
        val c = state.line[state.pos]
        if (state.inQuote) {
            when (c) {
                '"' -> {
                    name += c
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
                    name += c
                    state.inQuote = true
                    state.pos++
                }
                '(' -> {
                    state.pos++
                    state.parenDepth++
                    val sub = parseList(state)
                    if (sub.isEmpty()) {
                        return Label(name.trim())
                    }
                    return Function(name.trim(), sub)
                }
                ')' -> {
                    state.parenDepth--
                    if (state.parenDepth < 0) {
                        throw RuntimeException("Unmatched right paren: ${state.line}")
                    }
                    return Label(name.trim())
                }
                ',' -> {
                    return Label(name.trim())
                }
                else -> {
                    name += c
                    state.pos++
                }
            }
        }
    }
    return Label(name)
}

private fun parseList(state: State): List<Node> {
    val result = mutableListOf<Node>()
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