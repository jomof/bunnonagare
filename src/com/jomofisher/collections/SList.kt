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

fun <T> SList<T>?.reversedEmpty(): SList<T>? {
    var result = slistOf<T>()
    forEach {
        result = result.push(it)
    }
    return result
}

fun <T> SList<T>.reversed(): SList<T> {
    return reversedEmpty()!!
}

fun <T> SList<T>?.push(value: T): SList<T> {
    return SList(value, this)
}

infix fun <T> SList<T>.concat(value: SList<T>?): SList<T> {
    var result = value
    reversedEmpty().forEach {
        result = result.push(it)
    }
    return result.notEmpty()
}

fun <T> SList<T>?.concatEmpty(value: SList<T>?): SList<T>? {
    var result = value
    reversedEmpty().forEach {
        result = result.push(it)
    }
    return result
}

fun <T> SList<T>?.notEmpty(): SList<T> {
    return this!!
}

fun <T> slistOf(): SList<T>? {
    return null
}

fun <T> slistOf(first: T, vararg values: T): SList<T> {
    var last = SList(first)
    values.toList().forEach {
        last = SList(it, last)
    }
    return last.reversed()
}

fun <T1, T2> SList<T1>?.mapEmptyReversed(action: (T1) -> T2): SList<T2>? {
    var result = slistOf<T2>()
    var current = this
    while (current != null) {
        result = result.push(action(current.value))
        ++current
    }
    return result
}

fun <T1, T2> SList<T1>?.mapEmpty(action: (T1) -> T2): SList<T2>? {
    return mapEmptyReversed(action).reversedEmpty()
}

fun <T1, T2> SList<T1>.mapReversed(action: (T1) -> T2): SList<T2> {
    return this.mapEmptyReversed(action)!!
}

fun <T1, T2> SList<T1>.map(action: (T1) -> T2): SList<T2> {
    return this.mapEmpty(action)!!
}

fun <T1, T2> SList<T1>?.mapIndexedReversed(action: (Int, T1) -> T2): SList<T2>? {
    var result = slistOf<T2>()
    var current = this
    var n = 0
    while (current != null) {
        result = result.push(action(n, current.value))
        ++current
        ++n
    }
    return result
}

fun <T1, T2> SList<T1>?.mapIndexed(action: (Int, T1) -> T2): SList<T2>? {
    return mapIndexedReversed(action).reversedEmpty()
}

fun <T> SList<SList<T>?>?.flattenEmpty(): SList<T>? {
    var result = slistOf<T>()
    forEach { outer ->
        outer.forEach { inner ->
            result = result.push(inner)
        }
    }
    return result.reversedEmpty()
}

fun <T> SList<SList<T>>.flatten(): SList<T> {
    var result = slistOf<T>()
    forEach { outer ->
        outer.forEach { inner ->
            result = result.push(inner)
        }
    }
    return result!!.reversed()
}

fun <T> SList<T>?.head(): T {
    return this!!.value
}

fun <T> SList<T>.drop(n: Int): SList<T>? {
    if (n == 0) {
        return this
    }
    if (this.next == null) {
        if (n == 1) {
            return this.next
        }
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
    var result = slistOf<T>()
    forEach {
        result = result.push(it)
    }
    return result
}

fun <T> Iterable<T>.toSList(): SList<T>? {
    return reversed().toSListReversed()
}

inline fun <reified T> SList<*>?.mapAsReversedEmpty(): SList<T>? {
    return mapEmptyReversed {
        if (it !is T) {
            throw RuntimeException("$it was not the expected type ")
        }
        it as T
    }
}

inline fun <reified T> SList<*>?.mapAsEmpty(): SList<T>? {
    return mapAsReversedEmpty<T>().reversedEmpty()
}

inline fun <reified T> SList<*>.mapAsReversed(): SList<T> {
    return this.mapAsReversedEmpty()!!
}

inline fun <reified T> SList<*>.mapAs(): SList<T> {
    return this.mapAsEmpty()!!
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
    return filterReversed(predicate).reversedEmpty()
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

fun <T> SList<T>.takeOnly(): T {
    if (next != null) {
        throw RuntimeException("expected exactly one element")
    }
    return value
}

fun <T> SList<T>?.take(n: Int): SList<T>? {
    var result = slistOf<T>()
    var current = this
    var n = n
    while (n != 0 && current != null) {
        result = result.push(current.value)
        ++current
        --n
    }
    return result.reversedEmpty()
}

fun <T, R> SList<T>?.fold(initial: R, operation: (acc: R, T) -> R): R {
    var acc = initial
    forEach {
        acc = operation(acc, it)
    }
    return acc
}