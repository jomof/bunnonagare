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
                            literalSentenceElement.name)
                }
            }
        }
        return null
    }

    private fun isStringLiteral(value: String): Boolean {
        if (value[0] != '\"') {
            return false
        }
        if (value.last() != '\"') {
            return false
        }
        return true
    }

    private fun getStringLiteral(value: String): String {
        return value.substring(1, value.length - 1)
    }

    private fun unifyToIsMapImplicit(makeElement: String, literalSentenceElement: Function)
            : SingleUnification? {
        if (literalSentenceElement.parms.isEmpty()) {
            if (!isMap.containsKey(literalSentenceElement.name)) {
                if (isStringLiteral(makeElement)) {
                    if (getStringLiteral(makeElement) == literalSentenceElement.name) {
                        return SingleUnification(makeElement, literalSentenceElement.name)
                    }
                    return null
                }
                return SingleUnification(makeElement, literalSentenceElement.name)
            }
        }
        return null
    }

    private fun tryUnify(make: List<String>, literalSentence: Function): List<Unification>? {
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
                if (unifiedToMake != null) {
                    result += UnificationList(unifiedToMake)
                    continue
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
            ignoreFirstParm: Boolean = false): List<Unification>? {
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
        val (failures, successes) = result.partition { hasAnyFailures(it) }
        if (successes.size == 1) {
            return successes[0]
        }
        if (result.size > 1) {
            throw RuntimeException("'$sentence' unifies in multiple ways")
        }
        if (failures.isNotEmpty()) {
            return failures[0]
        }
        return null
    }

    fun analyzeSentence(sentence: Function): List<Unification> {
        assert(sentence.name == "sentence")
        val result = tryUnifyMake("sentence", sentence)!!
        throwIfAnyFailures(result)
        return result
    }

    private fun hasAnyFailures(unifications: List<Unification>): Boolean {
        for (unification in unifications) {
            when (unification) {
                is FailedUnification -> return true
                is UnificationList -> if (hasAnyFailures(unification.unifications)) return true
            }
        }
        return false
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