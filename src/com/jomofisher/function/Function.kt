package com.jomofisher.function

import com.jomofisher.collections.*
import java.io.File

open class Node

class Label(val label: String) : Node() {
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

class Function(val name: String, val parms: SList<Node>) : Node() {

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

class OrdinalFunction(ordinal: Int, val parms: SList<OrdinalNode>) : OrdinalNode(ordinal) {
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

fun createNode(name: String, children: SList<Node>? = null): Node {
    if (children == null) {
        return Label(name)
    }
    return Function(name, children)
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

fun Node.invert(parent: SList<Node>? = null): SList<Node>? {
    val (name, parms) = this
    return parms.invert(slistOf(Label(name))).push(createNode(name, parent))
}

fun SList<Node>?.invert(parent: SList<Node>? = null): SList<Node>? {
    return mapEmpty { it.invert(parent) }.flattenEmpty()
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

fun <T : Node> SList<T>.toNames(): SList<String> {
    return map {
        val (name, _) = it
        name
    }
}

fun SList<Function>?.toOrdinal(index: FragmentIndexBuilder)
        : Array<OrdinalFunction> {
    return index.rewriteToOrdinals(this).toTypedArray()
}

fun <T : Node> SList<T>?.toLabel(): SList<Label>? {
    return mapEmpty {
        when (it) {
            is Label -> it
            else -> throw RuntimeException("expected labels")
        }
    }
}

fun <T : Node> SList<T>?.toNameText(): SList<String>? {
    return toLabel().mapEmpty {
        it.label
    }
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

//fun <T : Node> SList<T>?
//
//fun Node.rewriteBottomUp(action : (Node) -> Node) : Node {
//    when(this) {
//        is Function -> {
//            val parms = this.parms.rewriteBottomUp()
//        }
//    }
//}
