package com.jomofisher.sentences

import com.jomofisher.collections.SList
import com.jomofisher.collections.fold
import com.jomofisher.collections.map
import com.jomofisher.collections.mapAs
import com.jomofisher.function.*
import com.jomofisher.function.Function
import java.io.File

class Classifier(val production: String, val pattern: Node)

fun createClassifier(classifier: Node): Classifier {
    val (production, match) = classifier
            .destructure("match($1, $2)", String::class, Node::class)
    return Classifier(production, match)
}

fun createClassifier(classifier: String): Classifier {
    return createClassifier(parseLispy(classifier))
}

fun Classifier.classify(node: Node): Node {
    return node.rewrite {
        val matches = it.destructure(pattern)
        if (matches != null) {
            it.annotate("classification", this.production)
        } else {
            it
        }
    }
}

fun SList<Classifier>?.classify(node: Node): Node {
    return fold(node) { prior, classifier ->
        classifier.classify(prior)
    }
}

fun SList<Classifier>?.classify(nodes: SList<Function>?): SList<Function>? {
    return nodes.map { classify(it) }.mapAs()
}

fun readClassifierFile(file: File): SList<Classifier>? {
    return parseLispy(file)
            .keepName("match")
            .map { createClassifier(it) }
}
