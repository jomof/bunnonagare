package com.jomofisher

fun createMakesMap(
        functions: List<Function>,
        isMap: Map<Type, Type>): Pair<Map<Type, List<List<Element>>>, Set<Type>> {
    val map = mutableMapOf<Type, MutableList<List<Element>>>()
    val holes = mutableSetOf<Type>()
    for (function in functions) {
        if (function.name == "makes") {
            mustHaveAllLiteralParameters(function)
            mustHaveAtLeastNParameters(function, 2)
            val production = Type(function.parms[0].name)
            if (function.parms.size == 2 && function.parms[1].name == "*") {
                if (!isMap.containsKey(production)) {
                    throw RuntimeException("make production $production was not in isMap")
                }
                holes.add(production)
            } else {
                mustHaveAtLeastNParameters(function, 3)
                mustHaveAllParametersInIsMap(isMap, function)
                val patterns = function
                        .parms
                        .drop(1)
                        .map { elementOf(it.name) }
                        .toList()
                if (map.containsKey(production)) {
                    map[production]?.add(patterns)
                } else {
                    map[production] = mutableListOf(patterns)
                }
            }
        }
    }
    return Pair(map, holes)
}

fun elementOf(name: String): Element {
    if (name[0] != '\"' || name.last() != '\"') {
        return Type(name)
    }
    return Literal(name.substring(1, name.length - 1))
}



