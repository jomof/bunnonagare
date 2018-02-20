package com.jomofisher.sentences

import com.jomofisher.collections.SList
import com.jomofisher.collections.fold
import com.jomofisher.collections.map
import com.jomofisher.function.*
import com.jomofisher.function.Function
import java.io.File

class Classifier(val production: String, val pattern: Node)

private fun createClassifier(classifier: Node): Classifier {
    val (production, match) = classifier
            .destructure("match($1, $2)", String::class, Node::class)
    return Classifier(production, match)
}

fun createClassifier(classifier: String): Classifier {
    return createClassifier(parseLispy(classifier))
}

fun SList<Classifier>?.annotate(node: Function): Function {
    return fold(node) { prior, classifier ->
        prior.rewrite {
            val matches = it.destructure(classifier.pattern)
            if (matches != null) {
                it.annotate("classification", classifier.production)
            } else {
                it
            }
        } as Function
    }
}

fun readClassifierFile(file: File): SList<Classifier>? {
    return parseLispy(file)
            .keepName("match")
            .map { createClassifier(it) }
}
