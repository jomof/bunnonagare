package com.jomofisher.collections

class MutableMap2d<K1, K2, V>(
        val map: MutableMap<K1, MutableMap<K2, V>> = mutableMapOf()) {

    operator fun set(k1: K1, k2: K2, v: V) {
        val k2Map = map[k1]
        if (k2Map == null) {
            map[k1] = mutableMapOf(k2 to v)
            return
        }
        k2Map[k2] = v
    }

    operator fun get(k1: K1, k2: K2): V? {
        val k2Map = map[k1] ?: return null
        return k2Map[k2]
    }
}

fun <K1, K2, V> mutableMap2dOf(): MutableMap2d<K1, K2, V> {
    return MutableMap2d(mutableMapOf())
}
