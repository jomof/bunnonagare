package com.jomofisher

import com.jomofisher.collections.*
import com.jomofisher.function.*
import com.jomofisher.function.Function
import com.jomofisher.sentences.annotate
import com.jomofisher.sentences.readClassifierFile
import com.jomofisher.sentences.readSentences
import com.jomofisher.sentences.toSentenceText
import org.junit.Test

class ProcessingTest {

    @Test
    fun recreate() {
        val grammarSentences = readSentences(rootFolder, sentencesFile)
        val dialogSentences = createDialogFromFolder(rootFolder, dialogFolder)
                .allSentences()
        val wanikani = readWaniKaniVocab(wanikaniVocab)
        val classifiers = readClassifierFile(classifiersFile)
        val sentences = (grammarSentences concat dialogSentences)
                .map(wanikani::annotate)
                .map(classifiers::annotate)

        val annotated = sentences.map(Function::exposeAnnotation)

        indexedFragmentsFile.writeText("")
        sentenceDistancesFile.writeText("")
        val sentenceIndex =
                readSentenceIndex(indexedFragmentsFile)
                        .appendTopLevel(annotated)
        val deepSentenceIndex = sentenceIndex.copy()
        val ordinalSentences = annotated.toOrdinal(deepSentenceIndex)
        val distanceTriangle = readDistances(sentenceDistancesFile)
                .fillInNewDistances(ordinalSentences)

        sentenceIndex.writeSentenceIndex(indexedFragmentsFile)
        distanceTriangle.writeDistances(sentenceDistancesFile)

        val edges = distanceTriangle.fairDjikstra()
        val connects = edges.toSquare().toConnectionTree()
        val sentenceArray = sentences.toTypedArray()
        connects.visitIndented(0) { depth, name ->
            val sentence = sentenceArray[name.toInt()].toSentenceText()
            println(sentence)
        }
        println(edges.toStringMapped {
            when (it) {
                0 -> " "
                1, 2 -> "."
                3, 4, 5, 6, 7 -> it.toString()
                else -> "*"
            }
        })
    }
}
