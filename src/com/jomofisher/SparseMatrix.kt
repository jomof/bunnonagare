package com.jomofisher

class SparseMatrix<T>(val size: Int) {
    val map: MutableMap<Int, MutableMap<Int, T>> = mutableMapOf()
    operator fun set(i: Int, j: Int, v: T) {
        assert(i < size)
        assert(j < size)
        val row = map[i]
        if (row == null) {
            map[i] = mutableMapOf()
            return set(i, j, v)
        }
        map[i]!![j] = v
    }

    operator fun get(i: Int, j: Int): T? {
        val row = map[i] ?: return null
        return row[j]
    }
}
