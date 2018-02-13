package com.jomofisher

import java.io.File

class Ontology(val forward: SList<Node>?) {
    val backward = forward.invert()
}

fun createOntologyFromFile(file: File): Ontology {
    val lines = file.readLines()
    return Ontology(TreeParser(lines).parse())
}