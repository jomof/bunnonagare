package com.jomofisher.function

import com.jomofisher.collections.SList
import com.jomofisher.collections.drop
import com.jomofisher.collections.get
import kotlin.reflect.KClass

private fun unifies(
        pattern: Node,
        node: Node,
        extracted: MutableMap<String, Node>): Boolean {
    return when (pattern) {
        is Label ->
            when {
                pattern.label == "*" -> true
                pattern.label.startsWith("$") -> {
                    extracted[pattern.label.substring(1)] = node
                    true
                }
                else ->
                    when (node) {
                        is Label -> return pattern.label == node.label
                        else -> false
                    }
            }
        is Function ->
            when (node) {
                is Function ->
                    when {
                        pattern.name == node.name ->
                            unifies(pattern.parms, node.parms, extracted)
                        else -> false
                    }
                else -> false
            }
        else -> throw RuntimeException(pattern.toString())
    }
}

private fun unifies(
        patternList: SList<Node>?,
        nodeList: SList<Node>?,
        extracted: MutableMap<String, Node>): Boolean {
    if (patternList == null && nodeList == null) {
        return true
    }
    if (patternList == null || nodeList == null) {
        return false
    }
    if (!unifies(patternList[0], nodeList[0], extracted)) {
        return false
    }
    return unifies(patternList.drop(1), nodeList.drop(1), extracted)
}

fun Node.destructure(pattern: Node): Map<String, Any>? {
    val extracted = mutableMapOf<String, Node>()
    if (unifies(pattern, this, extracted)) {
        return extracted
    }
    return null
}

fun Node.destructure(pattern: String): Map<String, Any>? {
    return destructure(parseLispy(pattern))
}

fun <T : Any> Node.destructure(pattern: String, clazz: KClass<T>): T {
    val extracted = mutableMapOf<String, Node>()
    if (unifies(parseLispy(pattern), this, extracted)) {
        return coerceNode(extracted["1"]!!, clazz)
    }
    throw RuntimeException("not match")
}

fun <T1 : Any, T2 : Any> Node.destructure(
        pattern: String,
        clazz1: KClass<T1>,
        clazz2: KClass<T2>): Pair<T1, T2> {
    val extracted = mutableMapOf<String, Node>()
    if (unifies(parseLispy(pattern), this, extracted)) {
        return Pair(coerceNode(extracted["1"]!!, clazz1),
                coerceNode(extracted["2"]!!, clazz2))
    }
    throw RuntimeException("not match")
}

data class Quad<out A, out B, out C, out D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D) {
    override fun toString(): String = "($first, $second, $third, $fourth)"
}

fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any> Node.destructure(
        pattern: String,
        clazz1: KClass<T1>,
        clazz2: KClass<T2>,
        clazz3: KClass<T3>,
        clazz4: KClass<T4>): Quad<T1, T2, T3, T4> {
    val extracted = mutableMapOf<String, Node>()
    if (unifies(parseLispy(pattern), this, extracted)) {
        return Quad(coerceNode(extracted["1"]!!, clazz1),
                coerceNode(extracted["2"]!!, clazz2),
                coerceNode(extracted["3"]!!, clazz3),
                coerceNode(extracted["4"]!!, clazz4))
    }
    throw RuntimeException("not match")
}

@Suppress("UNCHECKED_CAST")
private fun <T : Any> coerceNode(node: Node, clazz: KClass<T>): T {
    return when (clazz) {
        String::class -> node.toString() as T
        Int::class -> node.toString().toInt() as T
        else -> node as T
    }
}
