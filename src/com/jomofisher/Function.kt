package com.jomofisher

import java.io.File

open class Node

class Label(val label: String) : Node() {
    override fun toString(): String {
        return label
    }

    override fun hashCode(): Int {
        return label.hashCode()
    }
}

class Function(val name: String, val parms: SList<Node>?) : Node() {

    init {
        if (parms.isEmpty()) throw RuntimeException("should be label")
    }
    override fun toString(): String {
        val builder = StringBuilder()
        builder.append(name)
        if (!parms.isEmpty()) {
            builder.append("(")
            builder.append(parms.joinToString(","))
            builder.append(")")
        }
        return builder.toString()
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return this.toString() == other.toString()
    }

}

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
    init {
        if (parms.isEmpty()) throw RuntimeException("should be label")
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append(ordinal)
        if (!parms.isEmpty()) {
            builder.append("(")
            builder.append(parms.joinToString(","))
            builder.append(")")
        }
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

fun createNode(name: String, children: SList<Node>?): Node {
    if (children.isEmpty()) {
        return Label(name)
    }
    return Function(name, children)
}

fun createNode(ordinal: Int, children: SList<OrdinalNode>?): OrdinalNode {
    if (children.isEmpty()) {
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

fun Node.invert(parent: SList<Node>? = null): SList<Node>? {
    val (name, parms) = this
    return parms.invert(slistOf(Label(name))).push(createNode(name, parent))
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
