package com.jomofisher

import com.jomofisher.collections.*
import com.jomofisher.function.Function
import com.jomofisher.function.Label
import com.jomofisher.function.Node
import com.jomofisher.function.TreeParser
import java.io.File

class Ontology(val forward: SList<Node>) {
    //val backward = forward.invert()
}

private fun allLeafsOf(node: Node): SList<String>? {
    return when (node) {
        is Label -> slistOf(node.label)
        is Function ->
            node
                    .parms
                    .map { allLeafsOf(it) }
                    .flattenEmpty()
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
                        .flattenEmpty()
            }
        else -> throw RuntimeException("$node")
    }
}

fun Ontology.leafsUnder(name: String): Set<String> {
    val result = mutableSetOf<String>()
    result.addAll(forward
            .map { leafsUnder(it, name) }
            .flattenEmpty()
            .toList())
    return result
}

fun createOntologyFromFile(file: File): Ontology {
    val lines = file.readLines()
    return Ontology(TreeParser(lines).parse()!!)
}

