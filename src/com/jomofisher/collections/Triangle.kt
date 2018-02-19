package com.jomofisher.collections

import com.jomofisher.function.*
import com.jomofisher.function.Function
import java.io.File
import kotlin.math.max
import kotlin.math.min

interface Triangle<out T> {
    fun size(): Int
    operator fun get(i: Int, j: Int): T
}

class SequenceTriangle<out T>(
        private val size: Int,
        private val init: (Int, Int) -> T) : Triangle<T> {

    override fun get(i: Int, j: Int): T {
        if (i >= size) throw RuntimeException("out of range")
        if (j >= size) throw RuntimeException("out of range")
        return if (i <= j) {
            init(i, j)
        } else {
            throw RuntimeException("order exception")
        }
    }

    override fun size(): Int {
        return size
    }

    override fun toString(): String {
        val sb = StringBuilder()
        forEachRow { i, list -> sb.appendln("$i : $list") }
        return sb.toString()
    }
}

fun <T1, T2> Triangle<T1>.map(action: (T1) -> T2): Triangle<T2> {
    return SequenceTriangle(size()) { i, j ->
        action(this[i, j])
    }
}

fun <T> Triangle<T>.forEachIndexed(action: (Int, Int, T) -> Unit) {
    for (j in (0 until size())) {
        for (i in (0 until j + 1)) {
            action(i, j, get(i, j))
        }
    }
}
fun <T1, T2> Triangle<T1>.flattenIndexed(action: (Int, Int, T1) -> T2): SList<T2>? {
    var result = slistOf<T2>()
    forEachIndexed { i, j, t ->
        result += action(i, j, t)
    }
    return result.reversed()
}

fun <T> Triangle<T>.forEachRow(action: (Int, SList<T>?) -> Unit) {
    for (j in (0 until size())) {
        var row = slistOf<T>()
        for (i in (0 until j + 1)) {
            row += get(i, j)
        }
        action(j, row.reversed())
    }
}

inline fun <reified T> Triangle<T>.memoize(): Triangle<T> {
    val arr = Array(size(), { j ->
        Array(j + 1, { i ->
            get(i, j)
        })
    })
    return SequenceTriangle(size(), { i, j ->
        val sub = arr[j]
        sub[i]
    })
}

fun <T> Triangle<T>.getBiDirectional(i: Int, j: Int): T {
    if (i <= j) {
        return get(i, j)
    }
    return get(j, i)
}

fun readFunctionTriples(
        file: File,
        name: String): Triangle<Node> {
    val functions = parseLispy(file).mapAs<Function>()
    val sparse = mutableTriangleOf<Node>()
    var size = -1
    functions
            .keepName(name)
            .forEach {
                val (i, j) = it.parms.toNames().map { it.toInt() }
                size = max(size, max(i, j))
                sparse[i, j] = it.parms[2]
            }
    return SequenceTriangle(size + 1, { i, j ->
        sparse[i, j]!!
    })
}

fun <T> Triangle<T>.extendToSize(
        newSize: Int,
        other: (Int, Int) -> T): Triangle<T> {
    return SequenceTriangle(newSize, { i, j ->
        if (i < size() && j < size()) {
            this[i, j]
        } else {
            other(i, j)
        }
    })
}

fun <T> Triangle<T>.toStringMapped(action: (T) -> String): String {
    val sb = StringBuilder()
    forEachRow { i, row ->
        sb.append("$i".padStart(4) + ":")
        row.forEachIndexed { _, v ->
            sb.append(action(v))
        }
        sb.appendln()
    }
    return sb.toString()
}

/**
 * Given a triangle that represents a weight graph compute the
 * connection graph that reaches every node. "Fair" means that
 * all of the next nodes that would be found for the current
 * lowest weight will be selected (as opposed to just choosing
 * one).
 */
fun Triangle<Int>.fairDjikstra(entryPoint: Int = 0): Triangle<Int> {
    val edges = mutableMap2dOf<Int, Int, Int>()
    val graph = SequenceTriangle(
            this.size(),
            { i, j -> edges[i, j] ?: 0 })
    val visited = mutableSetOf(entryPoint)

    while (visited.size < size()) {
        var lowestCostSoFar = Int.MAX_VALUE
        var bestCandidates = slistOf<Pair<Int, Int>>()
        for (v in visited) {
            for (other in (0 until size())) {
                if (visited.contains(other)) {
                    continue
                }
                val distance = getBiDirectional(v, other)
                if (distance < lowestCostSoFar) {
                    bestCandidates = SList(Pair(v, other))
                    lowestCostSoFar = distance
                } else if (distance == lowestCostSoFar) {
                    // Record all edges that match the lowest cost
                    bestCandidates += Pair(v, other)
                }
            }
        }

        bestCandidates.forEach { (from, to) ->
            edges[min(from, to), max(from, to)] = lowestCostSoFar
            visited.add(to)
        }
    }
    return graph
}



