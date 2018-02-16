package com.jomofisher.collections

class MutableTriangle<V>(
        private val data: MutableMap2d<Int, Int, V>) {
    private var max: Int = 0

    private fun sort(k1: Int, k2: Int): Pair<Int, Int> {
        return if (k1 <= k2) {
            val currentMax = max
            if (k2 > currentMax) {
                max = k2
            }
            Pair(k1, k2)
        } else {
            sort(k2, k1)
        }
    }

    operator fun set(k1: Int, k2: Int, v: V) {
        val (low, high) = sort(k1, k2)
        data[low, high] = v
    }

    operator fun get(k1: Int, k2: Int): V? {
        val (low, high) = sort(k1, k2)
        return data[low, high]
    }
}

fun <V> mutableTriangleOf(): MutableTriangle<V> {
    return MutableTriangle(mutableMap2dOf())
}