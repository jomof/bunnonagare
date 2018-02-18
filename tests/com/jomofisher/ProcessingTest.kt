package com.jomofisher

import com.jomofisher.collections.*
import com.jomofisher.function.*
import com.jomofisher.function.Function
import org.junit.Test

class ProcessingTest {

    @Test
    fun recreate() {
        val grammarSentences = readSentences(sentencesFile)
        val dialogSentences = createDialogFromFolder(dialogFolder)
                .allSentences()
        val sentences = grammarSentences concat dialogSentences
        val japaneseOnly = classify(getJapaneseOnly(sentences))

        val sentenceIndex = readSentenceIndex(indexedFragmentsFile)
                .appendTopLevel(japaneseOnly)
        val deepSentenceIndex = sentenceIndex.copy()
        val ordinalSentences = japaneseOnly.toOrdinal(deepSentenceIndex)
        val distanceTriangle = readDistances(sentenceDistancesFile)
                .fillInNewDistances(ordinalSentences)

        sentenceIndex.writeSentenceIndex(indexedFragmentsFile)
        distanceTriangle.writeDistances(sentenceDistancesFile)

        val edges = distanceTriangle.fairDjikstra()
        println(edges.toStringMapped {
            when (it) {
                0 -> " "
                1, 2 -> "."
                3, 4, 5, 6, 7 -> it.toString()
                else -> "*"
            }
        })
    }

    private fun classify(classifier: Node, sentenceFragment: Node): Node {
        val (_, classifierParms) = classifier
        val (productionNode, classifierNode) = classifierParms
        val (production, _) = productionNode
        val (name, sentenceParms) = sentenceFragment
        val classifiedFunctionParms =
                sentenceParms.mapEmpty { classify(classifier, it) }
        if (unifies(classifierNode, sentenceFragment)) {
            return createNode(production, classifiedFunctionParms)
        }
        return createNode(name, classifiedFunctionParms)
    }

    private fun unifies(classifierNode: Node, sentenceFragment: Node): Boolean {
        val (classifierName, classifierParms) = classifierNode
        if (classifierName == "*") {
            return true
        }
        val (sentenceFragmentName, sentenceFragmentParms) = sentenceFragment
        if (classifierName != sentenceFragmentName) {
            return false
        }
        if (classifierParms.size() != sentenceFragmentParms.size()) {
            return false
        }
        return unifies(classifierParms, sentenceFragmentParms)
    }

    private fun unifies(
            classifierParms: SList<Node>?,
            sentenceFragmentParms: SList<Node>?): Boolean {
        if (classifierParms == null && sentenceFragmentParms == null) {
            return true
        }
        if (classifierParms == null || sentenceFragmentParms == null) {
            return false
        }
        if (!unifies(classifierParms.head(), sentenceFragmentParms.head())) {
            return false
        }
        return unifies(classifierParms.drop(1), sentenceFragmentParms.drop(1))
    }

    private fun classify(classifiers: SList<Node>?, function: Node): Node {
        var result = function
        classifiers
                .keepName("match")
                .forEach {
                    result = classify(it, result)
        }
        return result
    }

    private fun classify(sentences: SList<Function>): SList<Function> {
        val classifiers = parseLispy(classifiersFile)
        return sentences
                .map { classify(classifiers, it) }
                .mapAs()
    }


}
