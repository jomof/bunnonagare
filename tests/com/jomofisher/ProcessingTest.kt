package com.jomofisher

import com.google.common.truth.Truth.assertThat
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
        val sentences = readSentences(sentencesFile)
        val japaneseOnly = getJapaneseOnly(sentences)
        val fragmentIndex = FragmentIndexBuilder()
                .appendFile(indexedFragmentsFile)
                .appendTopLevel(japaneseOnly)
        assertThat(japaneseOnly!!.value.toString()).isEqualTo("(です)")
        assertThat(fragmentIndex.toLookupValue(japaneseOnly.value))
                .isEqualTo(0)
        fragmentIndex.toIndexFunctions().writeToFile(indexedFragmentsFile)

        val distanceTriangle =
                createTriangle(fragmentIndex.getOrderedFragments())
                        .map { (s1, s2) -> distance(s1, s2) }
                        .memoize()

        val distanceTriangleFunctions = distanceTriangle
                .mapRows { i, functions ->
                    Function("distance", functions
                            .map { Label("$it") as Node }
                            .push(Label("$i")))
                }

        distanceTriangleFunctions.writeToFile(sentenceDistancesFile)

        val edges = calculateLearningGraph(distanceTriangle)
        println("$edges")
    }

    private fun calculateLearningGraph(distances: Triangle<Int>): Triangle<Int> {
        val edges = SparseMatrix<Int>(distances.size)
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
