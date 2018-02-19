package com.jomofisher.sentences

import com.jomofisher.collections.*
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
    return fold(node) { node, classifier ->
        classifier.classify(node)
    }
}

fun SList<Classifier>?.classify(nodes: SList<Function>): SList<Function> {
    return nodes.map { classify(it) }.mapAs()
}

fun readClassifierFile(file: File): SList<Classifier>? {
    return parseLispy(file)
            .keepName("match")
            .mapEmpty { createClassifier(it) }
}

//private fun classify(classifiers: SList<Node>?, function: Node): Node {
//    var result = function
//    classifiers
//            .keepName("match")
//            .forEach {
//                result = classify(it, result)
//            }
//    return result
//}

//fun classifySentences(
//        classifiersFile: File,
//        sentences: SList<Function>): SList<Function> {
//    val classifiers = parseLispy(classifiersFile)
//    var sentences = sentences
//    classifiers
//            .keepName("match")
//            .forEach { classifier ->
//                val (_, classifierParms) = classifier
//                val (productionNode, classifierNode) = classifierParms
//                val (production, _) = productionNode
//                sentences = sentences.map { node ->
//                    node.rewrite {
//                        if (unifies(classifierNode, createFunction(slistOf(it)))) {
//                            it.annotate("classification", production)
//                        } else {
//                            it
//                        }
//                    }
//                }.mapAs()
//            }
//    return sentences
//}