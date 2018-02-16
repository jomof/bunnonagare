package com.jomofisher

import com.jomofisher.collections.*
import com.jomofisher.function.*
import com.jomofisher.function.Function
import java.io.File

fun readDistances(file: File): Triangle<Int> {
    return readFunctionTriples(file, "distance")
            .map {
                val (name, _) = it
                name.toInt()
            }.memoize()
}

fun Triangle<Int>.writeDistances(file: File) {
    flattenIndexed { i, j, distance ->
        Function("distance",
                slistOf(
                        Label(i.toString()),
                        Label(j.toString()),
                        Label(distance.toString())))
    }
            .writeToFile(file)
}

fun Triangle<Int>.fillInNewDistances(
        ordinalSentences: Array<OrdinalFunction>): Triangle<Int> {
    return extendToSize(ordinalSentences.size) { i, j ->
        distance(ordinalSentences[i], ordinalSentences[j])
    }.memoize()
}