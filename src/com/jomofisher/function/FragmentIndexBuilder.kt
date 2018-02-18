package com.jomofisher.function

import com.jomofisher.collections.*
import java.io.File
import kotlin.math.max

class FragmentIndexBuilder(
        val map: MutableMap<Node, Int> = mutableMapOf(),
        val table: MutableMap<Int, Node> = mutableMapOf(),
        var nextLookupValue: Int = 0)

fun FragmentIndexBuilder.createLookupIfNotPresent(value: Node) {
    if (map.containsKey(value)) {
        return
    }
    map[value] = nextLookupValue
    table[nextLookupValue] = value
    ++nextLookupValue
    return createLookupIfNotPresent(value)
}

fun FragmentIndexBuilder.toLookupValue(value: Node): Int {
    return map[value]!!
}

fun FragmentIndexBuilder.appendFile(file: File): FragmentIndexBuilder {
    parseLispy(file)
            .mapAsEmpty<Function>()
            .keepName("indexedFragment")
            .forEach { it ->
                val (index, _) = it.parms.value
                val lookupValue = index.toInt()
                val tree = it.parms.drop(1)!!.value
                assert(!map.containsKey(tree))
                map[tree] = lookupValue
                table[lookupValue] = tree
                nextLookupValue = max(nextLookupValue, lookupValue + 1)
            }
    return this
}

fun <T : Node> FragmentIndexBuilder.appendTopLevel(
        functions: SList<T>?): FragmentIndexBuilder {
    functions.forEach {
        createLookupIfNotPresent(it)
    }
    return this
}

fun FragmentIndexBuilder.getOrderedFragments(): Array<Node> {
    return Array(nextLookupValue) { table[it]!! }
}

fun FragmentIndexBuilder.toIndexFunctions(): SList<Node>? {
    return getOrderedFragments().mapIndexed { index, node ->
        Function("indexedFragment",
                slistOf(
                        Label(index.toString()),
                        node))
    }.toSList()
}

fun FragmentIndexBuilder.rewriteToOrdinal(label: Label): OrdinalLabel {
    createLookupIfNotPresent(label)
    val nodeOrdinal = toLookupValue(label)
    return OrdinalLabel(nodeOrdinal)
}

fun FragmentIndexBuilder.rewriteToOrdinal(function: Function): OrdinalFunction {
    createLookupIfNotPresent(function)
    val nodeOrdinal = toLookupValue(function)
    val ordinalParms = function
            .parms
            .map { rewriteToOrdinal(it) }
            .mapAs<OrdinalNode>()
    return OrdinalFunction(nodeOrdinal, ordinalParms)
}

fun FragmentIndexBuilder.rewriteToOrdinal(node: Node): OrdinalNode {
    return when (node) {
        is Function -> rewriteToOrdinal(node)
        is Label -> rewriteToOrdinal(node)
        else -> throw RuntimeException("$node")
    }
}

fun FragmentIndexBuilder.rewriteToOrdinals(nodes: SList<Function>?): SList<OrdinalFunction>? {
    return nodes.mapEmpty {
        rewriteToOrdinal(it)
    }
}

fun FragmentIndexBuilder.copy(): FragmentIndexBuilder {
    return FragmentIndexBuilder(
            HashMap(map).toMutableMap(),
            HashMap(table).toMutableMap(),
            nextLookupValue)
}

fun readSentenceIndex(file: File): FragmentIndexBuilder {
    return FragmentIndexBuilder().appendFile(file)
}

fun FragmentIndexBuilder.writeSentenceIndex(file: File) {
    toIndexFunctions().writeToFile(file)
}