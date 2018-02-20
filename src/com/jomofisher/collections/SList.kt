package com.jomofisher.collections

class SList<T>(val value: T, val next: SList<T>? = null) {
    override fun toString(): String {
        return "[${joinToString(", ")}]"
    }
}

fun <T> SList<T>?.forEach(action: (T) -> Unit) {
    if (this == null) {
        return
    }
    action(value)
    next.forEach(action)
}

fun <T> SList<T>?.forEachIndexed(action: (Int, T) -> Unit) {
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

fun <T> SList<T>?.slowlyPostpend(add: T): SList<T>? {
    if (this == null) {
        return SList(add)
    }
    return SList(value, next.slowlyPostpend(add))
}

fun <T> SList<T>?.reversed(): SList<T>? {
    var result = slistOf<T>()
    forEach {
        result += it
    }
    return result
}

operator fun <T> SList<T>?.plus(value: T): SList<T> {
    return SList(value, this)
}

infix fun <T> SList<T>?.concat(value: SList<T>?): SList<T>? {
    var result = value
    reversed().forEach {
        result += it
    }
    return result
}

fun <T> slistOf(): SList<T>? {
    return null
}

fun <T> slistOf(first: T, vararg values: T): SList<T>? {
    var last = SList(first)
    values.toList().forEach {
        last = SList(it, last)
    }
    return last.reversed()
}

fun <T1, T2> SList<T1>?.mapReversed(action: (T1) -> T2): SList<T2>? {
    var result = slistOf<T2>()
    var current = this
    while (current != null) {
        result += action(current.value)
        ++current
    }
    return result
}

fun <T1, T2> SList<T1>?.map(action: (T1) -> T2): SList<T2>? {
    return mapReversed(action).reversed()
}

fun <T> SList<SList<T>?>?.flatten(): SList<T>? {
    var result = slistOf<T>()
    forEach { outer ->
        outer.forEach { inner ->
            result += inner
        }
    }
    return result.reversed()
}

fun <T> SList<T>?.drop(n: Int): SList<T>? {
    if (n == 0) {
        return this
    }
    if (this == null) {
        throw RuntimeException("not enough elements to drop, ${n - 1} more needed")
    }
    return this.next.drop(n - 1)
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
    return fold(slistOf()) { prior, value -> prior + value }
}

fun <T> Iterable<T>.toSList(): SList<T>? {
    return reversed().toSListReversed()
}

inline fun <reified T> SList<*>?.mapAsReversed(): SList<T>? {
    return mapReversed {
        if (it !is T) {
            throw RuntimeException("$it was not the expected type ")
        }
        it as T
    }
}

inline fun <reified T> SList<*>?.mapAs(): SList<T>? {
    return mapAsReversed<T>().reversed()
}

fun <T> SList<T>?.filterReversed(predicate: (T) -> Boolean): SList<T>? {
    return fold(slistOf()) { prior, value ->
        if (predicate(value)) prior + value
        else prior
    }
}

fun <T> SList<T>?.filter(predicate: (T) -> Boolean): SList<T>? {
    return filterReversed(predicate).reversed()
}

fun <T> SList<T>?.toList(): List<T> {
    val result = mutableListOf<T>()
    forEach { v ->
        result.add(v)
    }
    return result
}

inline fun <reified T> SList<T>?.toTypedArray(): Array<T> {
    return toList().toTypedArray()
}

operator fun <T> SList<T>?.get(i: Int): T {
    var current = this
    var n = i
    while (current != null && n != 0) {
        ++current
        --n
    }
    if (n == 0) {
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

fun <T> SList<T>?.takeOnly(): T {
    if (this == null || next != null) {
        throw RuntimeException("expected exactly one element")
    }
    return value
}

fun <T, R> SList<T>?.fold(initial: R, operation: (acc: R, T) -> R): R {
    var acc = initial
    forEach {
        acc = operation(acc, it)
    }
    return acc
}