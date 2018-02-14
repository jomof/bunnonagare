package com.jomofisher.collections

class MutableTriangle<in K : Comparable<K>, V>(
        private val data: MutableMap2d<K, K, V>) {
    operator fun set(k1: K, k2: K, v: V) {
        val low = if (k1 < k2) k1 else k2
        val high = if (k1 < k2) k2 else k1
        data[low, high] = v
    }

    operator fun get(k1: K, k2: K): V? {
        val low = if (k1 < k2) k1 else k2
        val high = if (k1 < k2) k2 else k1
        return data[low, high]
    }
}

fun <K : Comparable<K>, V> mutableTriangleOf(): MutableTriangle<K, V> {
    return MutableTriangle(mutableMap2dOf())
}