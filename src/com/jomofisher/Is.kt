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
    for (function in functions) {
        if (function.name == "is") {
            when (function.parms.size) {
                0 -> throw RuntimeException("is-function is a literal or parameterless function")
                1 -> {
                    val left = function.parms[0]
                    mustBeLiteral(left)
                    mustNotBeInIsMap(map, left.name)
                    map[left.name] = rootOfIs
                }
                2 -> {
                    val left = function.parms[0]
                    val right = function.parms[1]
                    mustBeLiteral(left)
                    mustBeLiteral(right)
                    mustNotBeInIsMap(map, left.name)
                    mustBeInIsMap(map, right.name)
                    map[left.name] = right.name
                }
                else -> throw RuntimeException("is-function $function has too many parameters")
            }
        }
    }
    return map
}

fun mustNotBeInIsMap(map: MutableMap<String, String>, left: String) {
    if (map.containsKey(left)) {
        throw RuntimeException("is-function $left is already assigned")
    }
}
