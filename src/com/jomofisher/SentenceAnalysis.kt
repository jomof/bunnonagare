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

    private fun tryUnify(
            make: List<String>,
            literalSentence: Function,
            report: (String) -> Unit): List<Unification>? {
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
                        report,
                        ignoreFirstParm)
                if (unifiedToMake != null) {
                    result += UnificationList(unifiedToMake)
                    continue
                }
                report("could not unify $literalSentenceElement with $makeElement")
                return null
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
            report: (String) -> Unit,
            ignoreFirstParm: Boolean = false): List<Unification>? {
        val makes = getProductiveMakes(makeKey)
        val result = mutableListOf<List<Unification>>()
        for (make in makes) {
            var makeParms = make
            if (ignoreFirstParm) {
                makeParms = makeParms.drop(1)
            }
            val unification: List<Unification>? = tryUnify(makeParms, sentence, report)
            if (unification != null) {
                result.add(unification)
            }
        }
        if (result.size == 1) {
            return result[0]
        }
        if (result.size > 1) {
            report("'$sentence' unifies in multiple ways:")
            result.map { report("- $it") }
        }
        return null
    }

    fun analyzeSentence(sentence: Function): List<Unification> {
        assert(sentence.name == "sentence")
        val builder = StringBuilder()
        return tryUnifyMake(
                "sentence",
                sentence,
                { message -> builder.append("$message\n") })
                ?: throw RuntimeException("could not unify '$sentence': $builder")
    }
}