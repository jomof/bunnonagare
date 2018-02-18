package com.jomofisher.collections

fun <K, V> MutableMap<K, MutableSet<V>>.upsert(key: K, value: V) {
    val set = get(key)
    if (set == null) {
        put(key, mutableSetOf())
        return upsert(key, value)
    }
    set.add(value)
}

fun <K, V> Map<K, Set<V>>.toOneToOne(): Map<K, V> {
    val result: MutableMap<K, V> = mutableMapOf()
    forEach { key, valueSet ->
        if (valueSet.size != 1) {
            throw RuntimeException("expected all size one")
        }
        result[key] = valueSet.first()
    }
    return result
}