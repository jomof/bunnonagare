package com.jomofisher

import java.io.File

private class LispyParser(
        val line: String,
        var pos: Int = 0,
        var inQuote: Boolean = false,
        var parenDepth: Int = 0) {

    internal fun parseNode(): Node {
        var name = ""
        while (pos < line.length) {
            val c = line[pos]
            if (inQuote) {
                when (c) {
                    '"' -> {
                        name += c
                        inQuote = false
                        pos++
                    }
                    else -> {
                        name += c
                        pos++
                    }
                }
            } else {
                when (c) {
                    '"' -> {
                        name += c
                        inQuote = true
                        pos++
                    }
                    '(' -> {
                        pos++
                        parenDepth++
                        val sub = parseList()
                        if (sub.isEmpty()) {
                            return Label(name.trim())
                        }
                        return Function(name.trim(), sub)
                    }
                    ')' -> {
                        parenDepth--
                        if (parenDepth < 0) {
                            throw RuntimeException("Unmatched right paren: $line")
                        }
                        return Label(name.trim())
                    }
                    ',' -> {
                        return Label(name.trim())
                    }
                    else -> {
                        name += c
                        pos++
                    }
                }
            }
        }
        return Label(name)
    }

    private fun parseList(): SList<Node>? {
        var result = slistOf<Node>()
        while (pos < line.length) {
            when (line[pos]) {
                ',' -> pos++
                ')' -> {
                    pos++
                    return result
                }
                else -> {
                    result += parseNode()
                }
            }
        }
        return result
    }
}

fun parseLispy(file: File): List<Node> {
    return file.readLines().map { LispyParser(it).parseNode() }.toList()
}

fun parseLispy(line: String): Node {
    return LispyParser(line).parseNode()
}

fun parseLispyFunction(line: String): Function {
    return LispyParser(line).parseNode() as? Function ?: throw RuntimeException("unexpected")
}

