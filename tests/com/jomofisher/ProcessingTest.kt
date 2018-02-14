package com.jomofisher

import com.jomofisher.collections.*
import com.jomofisher.function.*
import com.jomofisher.function.Function
import org.junit.Test
import kotlin.math.max
import kotlin.math.min

class ProcessingTest {
    private fun getJapaneseOnly(sentences: SList<Function>?): SList<Function>? {
        return sentences
                .map { Function("", it.parms.drop(1)) }
    }

    @Test
    fun recreate() {
        val grammarSentences = readSentences(sentencesFile)
        val dialogSentences = createDialogFromFolder(dialogFolder)
                .allSentences()
        val sentences = grammarSentences + dialogSentences
        val japaneseOnly = getJapaneseOnly(sentences)
        val sentenceIndex = FragmentIndexBuilder()
                .appendFile(indexedFragmentsFile)
                .appendTopLevel(japaneseOnly)
        val deepSentenceIndex = sentenceIndex.copy()
        val ordinalSentences = japaneseOnly
                .toOrdinal(deepSentenceIndex)
                .toTypedArray()
        val originalDistanceTriangle =
                readFunctionTriples(sentenceDistancesFile, "distance")
                        .map {
                            val (name, _) = it
                            name.toInt()
                        }
        val distanceTriangle =
                originalDistanceTriangle.extendToSize(ordinalSentences.size) { i, j ->
                    println("calculating distance $i, $j")
                    distance(ordinalSentences[i], ordinalSentences[j])
                }.memoize()

        val distanceTriangleFunctions = distanceTriangle
                .flattenIndexed { i, j, distance ->
                    Function("distance",
                            slistOf(
                                    Label(i.toString()),
                                    Label(j.toString()),
                                    Label(distance.toString())))
                }

        sentenceIndex.toIndexFunctions().writeToFile(indexedFragmentsFile)
        distanceTriangleFunctions.writeToFile(sentenceDistancesFile)

        val edges = calculateLearningGraph(distanceTriangle)
        println(edges.toStringMapped {
            if (it == 1) "*" else " "
        })
    }

    private fun calculateLearningGraph(distances: Triangle<Int>): Triangle<Int> {
        val edges = mutableMap2dOf<Int, Int, Int>()
        val graph = Triangle(distances.size, { i, j -> edges[i, j] ?: 0 })
        val visited = mutableSetOf(0)

        while (visited.size < distances.size) {
            var lowestCostSoFar = Int.MAX_VALUE
            var bestCandidates = slistOf<Pair<Int, Int>>()
            for (v in visited) {
                for (other in (0 until distances.size)) {
                    if (visited.contains(other)) {
                        continue
                    }
                    val distance = distances[v, other]
                    if (distance < lowestCostSoFar) {
                        bestCandidates = SList(Pair(v, other))
                        lowestCostSoFar = distance
                    } else if (distance == lowestCostSoFar) {
                        // Record all edges that match the lowest cost
                        bestCandidates = bestCandidates.push(Pair(v, other))
                    }
                }
            }

            bestCandidates.forEach { (from, to) ->
                edges[max(from, to), min(from, to)] = 1
                visited.add(to)
            }
        }
        return graph
    }
}
