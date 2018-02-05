package com.jomofisher

const val rootOfIs = "root-of-is"

fun mustBeInIsMap(isMap: Map<String, String>, key: String) {
    if (!isMap.containsKey(key)) {
        throw RuntimeException("is-map doesn't contain $key as a value")
    }
}

fun mustHaveAllParametersInIsMap(isMap: Map<String, String>, function: Function) {
    function.parms
            .filterNot { isMap.containsKey(it.name) }
            .forEach { throw RuntimeException("expected parameters of $function to be in is-map, but $it isn't there") }
}

fun createIsMap(functions: List<Function>): Map<String, String> {
    val map = mutableMapOf<String, String>()
    functions
            .filter { it.name == "is" }
            .forEach {
                when (it.parms.size) {
                    0 -> throw RuntimeException("is-function is a literal or parameterless function")
                    1 -> {
                        val left = it.parms[0]
                        mustBeLiteral(left)
                        mustNotBeInIsMap(map, left.name)
                        map[left.name] = rootOfIs
                    }
                    2 -> {
                        val left = it.parms[0]
                        val right = it.parms[1]
                        mustBeLiteral(left)
                        mustBeLiteral(right)
                        mustNotBeInIsMap(map, left.name)
                        mustBeInIsMap(map, right.name)
                        map[left.name] = right.name
                    }
                    else -> throw RuntimeException("is-function $it has too many parameters")
                }
            }
    return map
}

fun createReverseIsMap(isMap: Map<String, String>): Map<String, Set<String>> {
    val map = mutableMapOf<String, MutableSet<String>>()
    for ((child, parent) in isMap) {
        var childSet = map[parent]
        if (childSet == null) {
            childSet = mutableSetOf()
            map[parent] = childSet
        }
        childSet.add(child)
    }
    return map
}

fun mustNotBeInIsMap(map: MutableMap<String, String>, left: String) {
    if (map.containsKey(left)) {
        throw RuntimeException("is-function $left is already assigned")
    }
}
