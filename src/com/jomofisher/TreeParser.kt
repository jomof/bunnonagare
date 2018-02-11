package com.jomofisher

class TreeParser(private val lines: List<String>) {
    private var line: Int = 0
    private fun parseNode(indent: Int, fix: String): Node {
        val nextPrefix = " ".repeat(indent + 1)
        val name = lines[line].trim()
        ++line
        val prefix = if (fix.isEmpty()) {
            name
        } else {
            fix + "-" + name
        }
        var children = slistOf<Node>()
        while (line < lines.size && lines[line].startsWith(nextPrefix)) {
            children = children.push(parseNode(indent + 1, prefix))
        }
        return createNode(prefix, children.reversed())
    }

    fun parse(): SList<Node>? {
        var result = slistOf<Node>()
        while (line < lines.size) {
            result = result.push(parseNode(0, ""))
        }
        return result.reversed()
    }
}