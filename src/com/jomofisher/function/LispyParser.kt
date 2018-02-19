package com.jomofisher.function

import com.jomofisher.collections.*
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
                        return createNode(name.trim(), parseList())
                    }
                    ')' -> {
                        parenDepth--
                        if (parenDepth < 0) {
                            throw RuntimeException("Unmatched right paren: $line")
                        }
                        return createLabel(name.trim())
                    }
                    ',' -> {
                        return createLabel(name.trim())
                    }
                    else -> {
                        name += c
                        pos++
                    }
                }
            }
        }
        return createLabel(name)
    }

    private fun parseList(): SList<Node>? {
        var result = slistOf<Node>()
        while (pos < line.length) {
            when (line[pos]) {
                ',' -> pos++
                ')' -> {
                    pos++
                    return result.reversedEmpty()
                }
                else -> {
                    result = result.push(parseNode())
                }
            }
        }
        return result.reversedEmpty()
    }
}

fun parseLispy(file: File): SList<Node>? {
    return file
            .readLines()
            .filter { !it.isEmpty() }
            .map { LispyParser(it).parseNode() }
            .toSList()
}

fun parseLispy(line: String): Node {
    return LispyParser(line).parseNode()
}

fun parseLispyFunction(line: String): Function {
    return LispyParser(line).parseNode() as? Function ?: throw RuntimeException("unexpected")
}

