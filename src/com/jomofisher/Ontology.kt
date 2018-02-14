package com.jomofisher

import java.io.File

class Ontology(val forward: SList<Node>?) {
    val backward = forward.invert()
}

private fun allLeafs(node: Node): SList<String>? {
    return when (node) {
        is Label -> slistOf(node.label)
        is Function ->
            node
                    .parms
                    .map { allLeafs(it) }
                    .flatten()
        else -> throw RuntimeException("$node")
    }
}

private fun leafsUnder(node: Node, name: String): SList<String>? {
    return when (node) {
        is Label -> slistOf()
        is Function ->
            if (node.name == name) {
                allLeafs(node)
            } else {
                node
                        .parms
                        .map { leafsUnder(it, name) }
                        .flatten()
            }
        else -> throw RuntimeException("$node")
    }
}

fun Ontology.leafsUnder(name: String): Set<String> {
    val result = mutableSetOf<String>()
    result.addAll(forward
            .map { leafsUnder(it, name) }
            .flatten()
            .toList())
    return result
}

fun createOntologyFromFile(file: File): Ontology {
    val lines = file.readLines()
    return Ontology(TreeParser(lines).parse())
}

