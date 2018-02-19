package com.jomofisher.function

import com.jomofisher.collections.*
import java.io.File

open class Node

abstract class Label : Node() {
    abstract val label: String

    override fun toString(): String {
        return label
    }

    override fun hashCode(): Int {
        return label.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is Label) {
            return label == other.label
        }
        return false
    }
}

class ImmutableLabel(override val label: String) : Label()

abstract class Function : Node() {
    abstract val name: String
    abstract val parms: SList<Node>?

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append(name)
        builder.append("(")
        builder.append(parms.joinToString(","))
        builder.append(")")
        return builder.toString()
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return this.toString() == other.toString()
    }

}

private class ImmutableFunction(
        override val name: String,
        override val parms: SList<Node>?) : Function()

open class OrdinalNode(val ordinal: Int) : Node()
class OrdinalLabel(ordinal: Int) : OrdinalNode(ordinal) {
    override fun toString(): String {
        return "$ordinal"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is OrdinalLabel) {
            return false
        }
        return this.ordinal == other.ordinal
    }

    override fun hashCode(): Int {
        return ordinal
    }
}

class OrdinalFunction(ordinal: Int, val parms: SList<OrdinalNode>?) : OrdinalNode(ordinal) {
    override fun toString(): String {
        val builder = StringBuilder()
        builder.append(ordinal)
        builder.append("(")
        builder.append(parms.joinToString(","))
        builder.append(")")
        return builder.toString()
    }

    override fun hashCode(): Int {
        return ordinal
    }

    override fun equals(other: Any?): Boolean {
        if (other !is OrdinalFunction) {
            return false
        }
        return this.ordinal == other.ordinal
    }
}

fun createFunction(name: String, first: String, vararg values: String): Function {
    val parms = slistOf(createLabel(first))
            .concat(values.map { createLabel(it) }.toSList())
    return ImmutableFunction(name, parms.mapAs())
}

fun <T : Node> createFunction(name: String, parms: SList<T>?): Function {
    return ImmutableFunction(name, parms.mapAs())
}

fun <T : Node> createFunction(parms: SList<T>?): Function {
    return ImmutableFunction("", parms.mapAs())
}

fun createLabel(name: String): Label {
    return ImmutableLabel(name)
}

fun createFunction(name: String, node: Node): Function {
    return ImmutableFunction(name, slistOf(node))
}

fun <T : Node> createNode(name: String, children: SList<T>? = null): Node {
    if (children == null) {
        return ImmutableLabel(name)
    }
    return ImmutableFunction(name, children.mapAs())
}

fun createNode(ordinal: Int, children: SList<OrdinalNode>?): OrdinalNode {
    if (children == null) {
        return OrdinalLabel(ordinal)
    }
    return OrdinalFunction(ordinal, children)
}

operator fun Node.component1(): String {
    return when (this) {
        is Function -> name
        is Label -> label
        else -> throw RuntimeException("$this")
    }
}

operator fun Node.component2(): SList<Node>? {
    return when (this) {
        is Function -> parms
        is Label -> slistOf()
        else -> throw RuntimeException("$this")
    }
}

operator fun Function.component2(): SList<Node>? {
    return parms
}

fun Node.invert(parent: SList<Node>? = null): SList<Node>? {
    val (name, parms) = this
    val node = createNode(name, parent)
    return parms.invert(slistOf(createLabel(name))) + node
}

fun SList<Node>?.invert(parent: SList<Node>? = null): SList<Node>? {
    return map { it.invert(parent) }.flatten()
}

fun <T : Node> SList<T>?.keepName(match: String): SList<T>? {
    return filter {
        val (name, _) = it
        name == match
    }
}

fun <T : Node> SList<T>?.writeToFile(file: File) {
    file.delete()
    forEach {
        file.appendText("$it\n")
    }
}

fun <T : Node> SList<T>?.toNames(): SList<String>? {
    return map {
        val (name, _) = it
        name
    }
}

fun <T : Node> SList<T>?.getScalar(name: String): String? {
    val named = keepName(name) ?: return null
    return named.mapAs<Function>().takeOnly().parms[0].component1()
}

fun Array<Function>.indexByParameter(parameter: Int): Map<String, Set<Int>> {
    val result: MutableMap<String, MutableSet<Int>> = mutableMapOf()
    mapIndexed { i, f ->
        val param = f.parms[parameter]
        when (param) {
            is Label -> result.upsert(param.label, i)
            is Function ->
                param.parms.forEach {
                    if (it !is Label) {
                        throw RuntimeException("unexpected")
                    }
                    result.upsert(it.label, i)
                }
        }
    }
    return result
}

fun SList<Function>?.toOrdinal(index: FragmentIndexBuilder)
        : Array<OrdinalFunction> {
    return index.rewriteToOrdinals(this).toTypedArray()
}