package com.jomofisher

const val rootOfIs = "root-of-is"

fun createIsMap(functions: List<Function>): Map<String, String> {
    val map = mutableMapOf<String, String>()
    for (function in functions) {
        if (function.name == "is") {
            when (function.parms.size) {
                0 -> throw RuntimeException("is-function is a literal or parameterless function")
                1 -> {
                    val left = function.parms[0]
                    mustBeLiteral(left)
                    mapMustNotContain(map, left.name)
                    map[left.name] = rootOfIs
                }
                2 -> {
                    val left = function.parms[0]
                    val right = function.parms[1]
                    mustBeLiteral(left)
                    mustBeLiteral(right)
                    mustContainRight(map, right.name)
                    map[left.name] = right.name
                }
                else -> throw RuntimeException("is-function $function has too many parameters")
            }
        }
    }
    return map
}

fun mapMustNotContain(map: MutableMap<String, String>, left: String) {
    if (map.containsKey(left)) {
        throw RuntimeException("is-function $left is already assigned")
    }
}

private fun mustContainRight(map: Map<String, String>, left: String) {
    if (!map.containsKey(left)) {
        throw RuntimeException("is-map doesn't contain $left as a root")
    }
}
