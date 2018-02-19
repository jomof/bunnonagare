package com.jomofisher.function

import com.jomofisher.collections.SList
import com.jomofisher.collections.drop
import com.jomofisher.collections.head
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
    if (!unifies(patternList.head(), nodeList.head(), extracted)) {
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

@Suppress("UNCHECKED_CAST")
private fun <T : Any> coerceNode(node: Node, clazz: KClass<T>): T {
    return when (clazz) {
        String::class -> node.toString() as T
        else -> node as T
    }
}
