package com.jomofisher.collections

import com.jomofisher.function.Node
import com.jomofisher.function.createNode

interface Square<out T> {
    fun size(): Int
    operator fun get(i: Int, j: Int): T
}

class SequenceSquare<out T>(
        private val size: Int,
        private val init: (Int, Int) -> T) : Square<T> {

    override fun get(i: Int, j: Int): T {
        return init(i, j)
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

fun <T> squareOf(array: Array<T>): Square<Pair<T, T>> {
    return SequenceSquare(array.size) { i, j ->
        Pair(array[i], array[j])
    }
}

fun <T> squareOf(size: Int, action: (Int, Int) -> T): Square<T> {
    return SequenceSquare(size, action)
}

fun <T1, T2> Square<T1>.map(action: (T1) -> T2): Square<T2> {
    return SequenceSquare(size()) { i, j ->
        action(this[i, j])
    }
}

fun <T> Square<T>.forEachRow(action: (Int, SList<T>?) -> Unit) {
    for (j in (0 until size())) {
        var row = slistOf<T>()
        for (i in (0 until size())) {
            row += get(i, j)
        }
        action(j, row.reversed())
    }
}

/**
 * Given a triangle that represents a weight graph compute the
 * connection graph that reaches every node. "Fair" means that
 * all of the next nodes that would be found for the current
 * lowest weight will be selected (as opposed to just choosing
 * one).
 */
fun Square<Int>.fairDjikstra(entryPoint: Int = 0): Square<Int> {
    val edges = mutableMap2dOf<Int, Int, Int>()
    val graph = SequenceSquare(
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
                val distance = get(v, other)
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
            edges[from, to] = lowestCostSoFar
            visited.add(to)
        }
    }
    return graph
}

fun Square<Int>.toConnectionTree(
        entryPoint: Int = 0,
        seen: MutableSet<Int> = mutableSetOf()): Node {
    val name = entryPoint.toString()
    val min = (0 until size())
            .map { get(entryPoint, it) }
            .filter { it > 0 }
            .min()!!
    val level = (0 until size())
            .filter { !seen.contains(it) && get(entryPoint, it) == min }
    seen.addAll(level)
    val parms = level.map { to ->
        toConnectionTree(to, seen)
    }.toSList()
    return createNode(name, parms.reversed())
}