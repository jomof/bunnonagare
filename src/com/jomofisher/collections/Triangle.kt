package com.jomofisher.collections

import com.jomofisher.function.*
import java.io.File
import kotlin.math.max

class Triangle<out T>(val size: Int, val init: (Int, Int) -> T) {
    override fun toString(): String {
        val sb = StringBuilder()
        forEachRow { i, list -> sb.appendln("$i : $list") }
        return sb.toString()
    }
}

operator fun <T> Triangle<T>.get(i: Int, j: Int): T {
    assert(i < size)
    assert(j < size)
    assert(i <= j)
    return if (i >= j) {
        init(i, j)
    } else {
        init(j, i)
    }
}

fun <T1, T2> Triangle<T1>.map(action: (T1) -> T2): Triangle<T2> {
    return Triangle(size) { i, j ->
        action(init(i, j))
    }
}

fun <T1, T2> Triangle<T1>.flattenIndexed(action: (Int, Int, T1) -> T2): SList<T2>? {
    var result = slistOf<T2>()
    for (i in (size - 1 downTo 0)) {
        (size - 1 downTo 0)
                .asSequence()
                .filter { i >= it }
                .forEach { result = result.push(action(i, it, init(i, it))) }
    }
    return result
}

fun <T> Triangle<T>.forEachRow(action: (Int, SList<T>?) -> Unit) {
    for (i in (0 until size)) {
        val rowValues = (0 until size)
                .filter { i >= it }
                .toSListReversed()
                .mapReversed { init(i, it) }
        action(i, rowValues)
    }
}

inline fun <reified T> Triangle<T>.memoize(): Triangle<T> {
    val arr = Array(size, { i ->
        Array(i + 1, { j ->
            get(i, j)
        })
    })
    return Triangle(size, { i, j ->
        arr[i][j]
    })
}

fun readFunctionTriples(
        file: File,
        name: String): Triangle<Node> {
    val functions = parseLispy(file)
    val sparse = mutableTriangleOf<Int, Node>()
    var size = 0
    functions
            .keepName(name)
            .forEach {
                val (_, parms) = it
                val (i, j) = parms.toNames().map { it.toInt() }
                size = max(size, max(i, j))
                sparse[i, j] = parms[2]
            }
    return Triangle(size + 1, { i, j ->
        val x = sparse[i, j]
        if (x == null) {
            println("$i, $j")
            sparse[i, j]
        }
        x!!
    })
}

fun <T> Triangle<T>.extendToSize(
        newSize: Int,
        other: (Int, Int) -> T): Triangle<T> {
    return Triangle(newSize, { i, j ->
        if (i < size && j < size) {
            init(i, j)
        } else {
            other(i, j)
        }
    })
}

fun <T> Triangle<T>.toStringMapped(action: (T) -> String): String {
    val sb = StringBuilder()
    forEachRow { i, row ->
        sb.append("$i".padStart(4) + ":")
        row.forEachIndexed { j, v ->
            sb.append(action(v))
        }
        sb.appendln()
    }
    return sb.toString()
}


