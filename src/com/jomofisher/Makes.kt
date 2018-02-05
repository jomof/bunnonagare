package com.jomofisher

fun createMakesMap(
        functions: List<Function>,
        isMap: Map<String, String>): Map<String, List<List<String>>> {
    val map = mutableMapOf<String, MutableList<List<String>>>()
    for (function in functions) {
        if (function.name == "makes") {
            mustHaveAtLeastNParameters(function, 3)
            mustHaveAllLiteralParameters(function)
            mustHaveAllParametersInIsMap(isMap, function)
            val production = function.parms[0].name
            val patterns = function
                    .parms
                    .drop(1)
                    .map { it.name }
                    .toList()
            if (map.containsKey(production)) {
                map[production]?.add(patterns)
            } else {
                map[production] = mutableListOf(patterns)
            }
        }
    }
    return map
}



