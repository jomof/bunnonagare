package com.jomofisher

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

fun <T> Triangle<T>.forEachRow(action: (Int, SList<T>?) -> Unit) {
    for (i in (0 until size)) {
        val rowValues = (0 until size)
                .filter { i >= it }
                .toSListReversed()
                .mapReversed { init(i, it) }
        action(i, rowValues)
    }
}

fun <T1, T2> Triangle<T1>.mapRows(action: (Int, SList<T1>?) -> T2): SList<T2>? {
    var result = slistOf<T2>()
    for (i in (0 until size)) {
        val rowValues = (0 until size)
                .filter { i >= it }
                .toSListReversed()
                .mapReversed { init(i, it) }
        result = result.push(action(i, rowValues))
    }
    return result.reversed()
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

fun <T> createTriangle(array: Array<T>): Triangle<Pair<T, T>> {
    return Triangle(array.size) { i, j ->
        assert(i > j);Pair(array[i], array[j])
    }
}


