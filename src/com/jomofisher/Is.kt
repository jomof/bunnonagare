package com.jomofisher

val rootOfIsType = Type("root-of-is")

fun mustBeInIsMap(isMap: Map<Type, Type>, type: Type) {
    if (!isMap.containsKey(type)) {
        throw RuntimeException("is-map doesn't contain $type as a value")
    }
}

fun mustHaveAllParametersInIsMap(isMap: Map<Type, Type>, function: Function) {
    function.parms
            .filterNot { isMap.containsKey(Type(it.name)) }
            .forEach { throw RuntimeException("expected parameters of $function to be in is-map, but $it isn't there") }
}

fun createIsMap(functions: List<Function>): Map<Type, Type> {
    val map = mutableMapOf<Type, Type>()
    functions
            .filter { it.name == "is" }
            .forEach {
                when (it.parms.size) {
                    0 -> throw RuntimeException(
                            "is-function is a literal or parameterless function")
                    1 -> {
                        val left = it.parms[0]
                        val typeDef = Type(left.name)
                        mustBeLiteral(left)
                        mustNotBeInIsMap(map, typeDef)
                        map[typeDef] = rootOfIsType
                    }
                    2 -> {
                        val left = it.parms[0]
                        val right = it.parms[1]
                        val leftType = Type(left.name)
                        val rightType = Type(right.name)
                        mustBeLiteral(left)
                        mustBeLiteral(right)
                        mustNotBeInIsMap(map, leftType)
                        mustBeInIsMap(map, rightType)
                        map[leftType] = rightType
                    }
                    else -> throw RuntimeException("is-function $it has too many parameters")
                }
            }
    return map
}

fun createReverseIsMap(isMap: Map<Type, Type>): Map<Type, Set<Type>> {
    val map = mutableMapOf<Type, MutableSet<Type>>()
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

fun mustNotBeInIsMap(map: MutableMap<Type, Type>, typeDef: Type) {
    if (map.containsKey(typeDef)) {
        throw RuntimeException("is-function $typeDef is already assigned")
    }
}
