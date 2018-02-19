package com.jomofisher

import com.jomofisher.collections.concat
import com.jomofisher.collections.fairDjikstra
import com.jomofisher.collections.mapAs
import com.jomofisher.collections.toStringMapped
import com.jomofisher.function.*
import com.jomofisher.function.Function
import com.jomofisher.sentences.classify
import com.jomofisher.sentences.readClassifierFile
import com.jomofisher.sentences.readSentences
import org.junit.Test

class ProcessingTest {

    @Test
    fun recreate() {
        val grammarSentences = readSentences(rootFolder, sentencesFile)
        val dialogSentences = createDialogFromFolder(rootFolder, dialogFolder)
                .allSentences()
        val wanikani = readWaniKaniVocab(wanikaniVocab)
        val sentences = (grammarSentences concat dialogSentences)
                .mapAs<Node>()
                .rewrite {
                    it
                }
                .mapAs<Function>()

        val classifiers = readClassifierFile(classifiersFile)
        val classified =
                classifiers.classify(sentences.mapAs())
                        .exposeAnnotations()


        indexedFragmentsFile.writeText("")
        sentenceDistancesFile.writeText("")
        val sentenceIndex =
                readSentenceIndex(indexedFragmentsFile)
                        .appendTopLevel(classified)
        val deepSentenceIndex = sentenceIndex.copy()
        val ordinalSentences = classified.toOrdinal(deepSentenceIndex)
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
}
