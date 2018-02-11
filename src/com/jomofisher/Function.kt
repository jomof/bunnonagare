package com.jomofisher

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

fun createNode(name: String, children: SList<Node>?): Node {
    if (children.isEmpty()) {
        return Label(name)
    }
    return Function(name, children)
}

fun Node.destructure(): Pair<String, SList<Node>?> {
    return when (this) {
        is Function -> Pair(name, parms)
        is Label -> Pair(label, slistOf())
        else -> throw RuntimeException("$this")
    }
}

fun Node.invert(parent: SList<Node>? = null): SList<Node>? {
    val (name, parms) = destructure()
    return parms.invert(slistOf(Label(name))).push(createNode(name, parent))
}

fun SList<Node>?.invert(parent: SList<Node>? = null): SList<Node>? {
    return map { it.invert(parent) }.flatten()
}
