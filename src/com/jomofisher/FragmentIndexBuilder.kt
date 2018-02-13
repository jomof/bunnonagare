package com.jomofisher

import java.io.File
import kotlin.math.max

class FragmentIndexBuilder {
    val map: MutableMap<Node, Int> = mutableMapOf()
    val table: MutableMap<Int, Node> = mutableMapOf()
    var nextLookupValue = 0
}

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
            .filterIsInstance<Function>()
            .filter { it.name == "indexedFragment" }
            .forEach { it ->
                val (index, _) = it.parms!!.value
                val lookupValue = index.toInt()
                val tree = it.parms.drop(1)!!.value
                assert(!map.containsKey(tree))
                map[tree] = lookupValue
                table[lookupValue] = tree
                nextLookupValue = max(nextLookupValue, lookupValue + 1)
            }
    return this
}

fun <T : Node> FragmentIndexBuilder.appendTopLevel(functions: SList<T>?): FragmentIndexBuilder {
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