package com.jomofisher

class SList<T>(val value: T, val next: SList<T>? = null) {
    override fun toString(): String {
        return "[${joinToString(", ")}]"
    }
}

fun <T> SList<T>?.isEmpty(): Boolean {
    return this == null
}

fun <T> SList<T>?.forEach(action: (T) -> Unit): Unit {
    if (this == null) {
        return
    }
    action(value)
    next.forEach(action)
}

fun <T> SList<T>?.forEachIndexed(action: (Int, T) -> Unit): Unit {
    var item = this
    var index = 0
    while (item != null) {
        action(index++, item.value)
        item = item.next
    }
}

fun <T> SList<T>?.joinToString(delimiter: String): String {
    val sb = StringBuilder()

    forEachIndexed { i, v ->
        if (i != 0) {
            sb.append(delimiter)
        }
        sb.append(v)
    }
    return sb.toString()
}

fun <T> slistOf(): SList<T>? {
    return null
}

fun <T> slistOf(value: T): SList<T>? {
    return SList(value)
}

fun <T> SList<T>?.slowlyPostpend(add: T): SList<T>? {
    if (this == null) {
        return SList(add)
    }
    return SList(value, next.slowlyPostpend(add))
}

fun <T> SList<T>?.reversed(): SList<T>? {
    var result = slistOf<T>()
    forEach {
        result = result.push(it)
    }
    return result
}

fun <T> SList<T>?.push(value: T): SList<T>? {
    return SList(value, this)
}

operator fun <T> SList<T>?.plus(value: SList<T>?): SList<T>? {
    var result = value
    reversed().forEach {
        result = result.push(it)
    }
    return result
}

fun <T> slistOf(vararg values: T): SList<T>? {
    var last = slistOf<T>()
    values.toList().reversed().forEach {
        last = SList(it, last)
    }
    return last
}

fun <T1, T2> SList<T1>?.mapReversed(action: (T1) -> T2): SList<T2>? {
    var result = slistOf<T2>()
    var current = this
    while (current != null) {
        result = result.push(action(current.value))
        ++current
    }
    return result
}

fun <T1, T2> SList<T1>?.map(action: (T1) -> T2): SList<T2>? {
    return mapReversed(action).reversed()
}

fun <T1, T2> SList<T1>?.mapIndexedReversed(action: (Int, T1) -> T2): SList<T2>? {
    var result = slistOf<T2>()
    var current = this
    var index = 0
    while (current != null) {
        result = SList(action(index++, current.value), result)
        current = current.next
    }
    return result
}


fun <T1, T2> SList<T1>?.mapIndexed(action: (Int, T1) -> T2): SList<T2>? {
    return mapIndexedReversed(action).reversed()
}

fun <T> SList<SList<T>?>?.flatten(): SList<T>? {
    var result = slistOf<T>()
    forEach { outer ->
        outer.forEach { inner ->
            result = result.push(inner)
        }
    }
    return result.reversed()
}

fun <T> SList<T>?.head(): T {
    return this!!.value
}

fun <T> SList<T>?.drop(n: Int): SList<T>? {
    if (n == 0) {
        return this
    }
    return this!!.next.drop(n - 1)
}

operator fun <T> SList<T>?.inc(): SList<T>? {
    return this!!.next
}

fun <T> SList<T>?.size(): Int {
    var current = this
    var size = 0
    while (current != null) {
        ++current
        ++size
    }
    return size
}

fun <T> Iterable<T>.toSListReversed(): SList<T>? {
    var result = slistOf<T>()
    forEach {
        result = result.push(it)
    }
    return result
}

fun <T> Iterable<T>.toSList(): SList<T>? {
    return reversed().toSListReversed()
}

fun <T> Array<T>.toSListReversed(): SList<T>? {
    var result = slistOf<T>()
    forEach {
        result = result.push(it)
    }
    return result
}

fun <T> Array<T>.toSList(): SList<T>? {
    return reversed().toSListReversed()
}

inline fun <reified T> SList<*>?.filterIsInstanceReversed(): SList<T>? {
    var result = slistOf<T>()
    forEach {
        when (it) {
            is T -> result = result.push(it)
        }
    }
    return result
}

inline fun <reified T> SList<*>?.filterIsInstance(): SList<T>? {
    return filterIsInstanceReversed<T>().reversed()
}

fun <T> SList<T>?.filterReversed(predicate: (T) -> Boolean): SList<T>? {
    var result = slistOf<T>()
    forEach {
        if (predicate(it)) {
            result = result.push(it)
        }
    }
    return result
}

fun <T> SList<T>?.filter(predicate: (T) -> Boolean): SList<T>? {
    return filterReversed(predicate).reversed()
}

inline fun <T> SList<T>?.toList(): List<T> {
    val result = mutableListOf<T>()
    forEach { v ->
        result.add(v)
    }
    return result
}

inline fun <reified T> SList<T>?.toTypedArray(): Array<T> {
    return toList().toTypedArray()
}

fun <T : Comparable<T>> SList<T>?.min(): T? {
    if (this == null) {
        return null
    }
    var min = value
    var current = this
    while (current != null) {
        val e = current.value
        if (min > e) {
            min = e
        }
        ++current
    }
    return min
}

fun <T> SList<T>?.takeReversed(n: Int): SList<T>? {
    var current = this
    var i = 0
    var result = slistOf<T>()
    while (current != null && i < n) {
        result = result.push(current.value)
        ++current
        ++i
    }
    return result
}

fun <T> SList<T>?.take(n: Int): SList<T>? {
    return takeReversed(n).reversed()
}

operator fun <T> SList<T>?.get(i: Int): T {
    var current = this
    var i = i
    while (current != null && i != 0) {
        ++current
        --i
    }
    if (i == 0) {
        return current!!.value
    }
    throw RuntimeException("no such element")
}

operator fun <T> SList<T>?.component1(): T {
    return get(0)
}

operator fun <T> SList<T>?.component2(): T {
    return get(1)
}

operator fun <T> SList<T>?.component3(): T {
    return get(2)
}