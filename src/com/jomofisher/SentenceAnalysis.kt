package com.jomofisher

class SentenceAnalysis(functions: List<Function>) {
    private val isMap = createIsMap(functions)
    private val reverseIsMap = createReverseIsMap(isMap)
    private val makesMap = createMakesMap(functions, isMap)

    init {
        functions
                .filter { it.name == "sentence" }
                .forEach { analyzeSentence(it) }
    }

    private fun isType(actualType: String, unificationType: String): Boolean {
        if (actualType == unificationType) {
            return true
        }
        val parentOfActualType = isMap[actualType] ?: return true
        return isType(parentOfActualType, unificationType)
    }

    private fun unifyToIsMapExplicit(unificationType: String, literalSentenceElement: Function)
            : SingleUnification? {
        if (literalSentenceElement.parms.isEmpty()) {
            if (isMap.containsKey(literalSentenceElement.name)) {
                if (isType(literalSentenceElement.name, unificationType)) {
                    return SingleUnification(literalSentenceElement.name,
                            literalSentenceElement)
                }
            }
        }
        return null
    }

    private fun unifyToIsMapImplicit(makeElement: String, literalSentenceElement: Function)
            : SingleUnification? {
        if (literalSentenceElement.parms.isEmpty()) {
            if (!isMap.containsKey(literalSentenceElement.name)) {
                return SingleUnification(makeElement, literalSentenceElement)
            }
        }
        return null
    }


    private fun tryUnify(make: List<String>, literalSentence: Function)
            : List<Unification>? {
        if (make.size == literalSentence.parms.size) {
            val result = mutableListOf<Unification>()
            for ((makeElement, literalSentenceElement) in make zip literalSentence.parms) {
                val unifiedToIsMapExplicit = unifyToIsMapExplicit(makeElement, literalSentenceElement)
                val unifiedToIsMapImplicit = unifyToIsMapImplicit(makeElement, literalSentenceElement)
                if (unifiedToIsMapExplicit != null) {
                    result += unifiedToIsMapExplicit
                    continue
                }
                if (unifiedToIsMapImplicit != null) {
                    result += unifiedToIsMapImplicit
                    continue
                }
                val ignoreFirstParm = makeElement == "sentence"
                val unifiedToMake = tryUnifyMake(
                        makeElement,
                        literalSentenceElement,
                        ignoreFirstParm)
                if (unifiedToMake.isNotEmpty()) {
                    if (unifiedToMake.size == 1) {
                        result += UnificationList(unifiedToMake[0])
                        continue
                    }
                    throw RuntimeException("multiple sub-unifications for '$literalSentenceElement'")
                }
                result += FailedUnification("could not unify $makeElement with $literalSentenceElement")
            }
            return result
        }
        return null
    }

    private fun getChildTypeClosure(type: String): Set<String> {
        val result = mutableSetOf<String>()
        result += type
        val children = reverseIsMap[type] ?: return result
        for (child in children) {
            result += getChildTypeClosure(child)
        }
        return result
    }

    private fun getProductiveMakes(makeKey: String): List<List<String>> {
        val productTypesClosure = getChildTypeClosure(makeKey)
        val result = mutableListOf<List<String>>()

        productTypesClosure
                .mapNotNull { makesMap[it] }
                .forEach { result += it }
        return result
    }

    private fun tryUnifyMake(
            makeKey: String,
            sentence: Function,
            ignoreFirstParm: Boolean = false): List<List<Unification>> {
        val makes = getProductiveMakes(makeKey)
        val result = mutableListOf<List<Unification>>()
        for (make in makes) {
            var makeParms = make
            if (ignoreFirstParm) {
                makeParms = makeParms.drop(1)
            }
            val unification: List<Unification>? = tryUnify(makeParms, sentence)
            if (unification != null) {
                result.add(unification)
            }
        }
        return result
    }

    fun analyzeSentence(sentence: Function): List<Unification> {
        assert(sentence.name == "sentence")
        val result = tryUnifyMake("sentence", sentence)
        if (result.isEmpty()) {
            throw RuntimeException("could not unify '$sentence'")
        }
        if (result.size > 1) {
            throw RuntimeException("'$sentence' unifies in multiple ways")
        }
        throwIfAnyFailures(result[0])
        return result[0]
    }

    private fun throwIfAnyFailures(unifications: List<Unification>) {
        for (unification in unifications) {
            when (unification) {
                is FailedUnification -> throw RuntimeException(unification.message)
                is UnificationList -> throwIfAnyFailures(unification.unifications)
            }
        }
    }

}