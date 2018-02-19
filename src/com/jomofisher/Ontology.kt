package com.jomofisher

import com.jomofisher.collections.*
import com.jomofisher.function.*
import com.jomofisher.function.Function
import java.io.File

class Ontology(val forward: SList<Node>) {
    val backward = forward.invert()
    val leafSet = createLeafSet(forward)
}

private fun createLeafSet(forward: SList<Node>): Set<String> {
    return forward
            .map { allLeafsOf(it) }
            .flatten()
            .filter { !it.isEmpty() }
            .toList()
            .toSet()
}

fun Ontology.isLeaf(check: String): Boolean {
    return leafSet.contains(check)
}

private fun allLeafsOf(node: Node): SList<String>? {
    return when (node) {
        is Label -> slistOf(node.label)
        is Function ->
            node
                    .parms
                    .map { allLeafsOf(it) }
                    .flatten()
        else -> throw RuntimeException("$node")
    }
}

private fun leafsUnder(node: Node, name: String): SList<String>? {
    return when (node) {
        is Label -> slistOf()
        is Function ->
            if (node.name == name) {
                allLeafsOf(node)
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

fun readOntologyFile(file: File): Ontology {
    val lines = file.readLines()
    return Ontology(TreeParser(lines).parse()!!)
}

