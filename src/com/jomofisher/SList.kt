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

operator fun <T> SList<T>?.plus(add: T): SList<T>? {
    if (this == null) {
        return SList(add)
    }
    return SList(value, next + add)
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

fun <T1, T2> SList<T1>?.map(action: (T1) -> T2): SList<T2>? {
    if (this == null) {
        return null
    }
    return SList(action(value), next.map(action))
}

fun <T> SList<SList<T>?>?.flatten(): SList<T>? {
    var result = slistOf<T>()
    forEach { outer ->
        outer.forEach { inner ->
            result += inner
        }
    }
    return result
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

fun <T> Iterable<T>.toSList(): SList<T>? {
    var result = slistOf<T>()
    reversed().forEach {
        result += it
    }
    return result
}