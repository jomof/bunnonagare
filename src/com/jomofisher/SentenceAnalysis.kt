package com.jomofisher


class SentenceAnalysis(functions: List<Function>) {
    private val isMap = createIsMap(functions)
    private val reverseIsMap = createReverseIsMap(isMap)
    private val makesMap: Map<Type, List<List<Element>>>
    private val holes: Set<Type>

    class Analyzer(
            val _isMap: Map<Type, Type>,
            val _reverseIsMap: Map<Type, Set<Type>>,
            val _makesMap: Map<Type, List<List<Element>>>,
            val _makeHoles: Set<Type>,
            val impliedIsMap: MutableMap<String, String> = mutableMapOf(),
            val impliedMakes: MutableList<List<String>> = mutableListOf()) {

        private fun <T> whenType(name: String, action: (Type) -> T?): T? {
            val type = Type(name)
            if (_isMap.containsKey(type)) {
                return action(type)
            }
            return null
        }

        private fun getParentType(type: Type): Type {
            return _isMap[type]!!
        }

        private fun isType(type: String): Boolean {
            return _isMap.containsKey(Type(type))
        }

        private fun isTypeOrParent(
                actualType: Type,
                unificationType: Type): Boolean {
            if (actualType == unificationType) {
                return true
            }
            val parentOfActualType = getParentType(actualType)
            return isTypeOrParent(parentOfActualType, unificationType)
        }

        private fun getChildTypeClosure(
                makeElement: Element): Set<Type> {
            val result = mutableSetOf<Type>()
            when (makeElement) {
                is Type -> {
                    result += makeElement
                    val children = _reverseIsMap[makeElement]
                    if (children != null) {
                        for (child in children) {
                            result += getChildTypeClosure(child)
                        }
                    }
                }
            }
            return result
        }

        private fun isMake(type: Type): Boolean {
            return _makesMap.containsKey(type) || _makeHoles.contains(type)
        }

        private fun getMakes(type: Type): List<List<Element>>? {
            return _makesMap[type]
        }

        private fun unifyToIsMapExplicit(
                unificationElement: Element,
                literalSentenceElement: Function): SingleUnification? {
            if (!literalSentenceElement.parms.isEmpty()) {
                return null
            }
            when (unificationElement) {
                is Type ->
                    return whenType(literalSentenceElement.name) {
                        if (isTypeOrParent(it, unificationElement)) {
                            SingleUnification(it, it)
                        } else {
                            null
                        }
                    }
            }
            return null
        }

        private fun unifyToIsMapImplicit(
                makeElement: Element,
                literalSentenceElement: Function): SingleUnification? {
            if (!literalSentenceElement.parms.isEmpty()) {
                return null
            }
            val name = literalSentenceElement.name
            if (isType(name)) {
                return null
            }
            when (makeElement) {
                is Type ->
                    if (!isMake(makeElement)) {
                        return SingleUnification(makeElement, Type(name))
                    }
                is Literal ->
                    if (makeElement.value == name) {
                        return SingleUnification(makeElement, Type(name))
                    }
            }
            return null
        }

        private fun tryUnify(
                make: List<Element>,
                literalSentence: Function,
                report: (String) -> Unit): List<Unification>? {
            if (make.size == literalSentence.parms.size) {
                val result = mutableListOf<Unification>()
                for ((makeElement, literalSentenceElement) in make zip literalSentence.parms) {
                    val unifiedToIsMapExplicit =
                            unifyToIsMapExplicit(makeElement, literalSentenceElement)
                    val unifiedToIsMapImplicit =
                            unifyToIsMapImplicit(makeElement, literalSentenceElement)
                    if (unifiedToIsMapExplicit != null) {
                        result += unifiedToIsMapExplicit
                        continue
                    }
                    if (unifiedToIsMapImplicit != null) {
                        result += unifiedToIsMapImplicit
                        continue
                    }
                    val ignoreFirstParm =
                            when (makeElement) {
                                is Type -> makeElement.name == "sentence"
                                else -> false
                            }
                    val unifiedToMake = tryUnifyMake(
                            makeElement,
                            literalSentenceElement,
                            report,
                            ignoreFirstParm)
                    if (unifiedToMake != null) {
                        result += UnificationList(unifiedToMake)
                        continue
                    }
                    report("could not unify $literalSentenceElement " +
                            "with $makeElement in make(_, ${make.joinToString(",")})")
                    return null
                }
                return result
            }
            return null
        }

        private fun getProductiveMakes(makeElement: Element): List<List<Element>> {
            val productTypesClosure = getChildTypeClosure(makeElement)
            val result = mutableListOf<List<Element>>()

            productTypesClosure
                    .mapNotNull { getMakes(it) }
                    .forEach { result += it }
            return result
        }

        internal fun tryUnifyMake(
                makeElement: Element,
                sentence: Function,
                report: (String) -> Unit,
                ignoreFirstParm: Boolean = false): List<Unification>? {
            val makes = getProductiveMakes(makeElement)
            val result = mutableListOf<List<Unification>>()
            for (make in makes) {
                var makeParms = make
                if (ignoreFirstParm) {
                    makeParms = makeParms.drop(1)
                }
                val unification: List<Unification>? = tryUnify(
                        makeParms,
                        sentence,
                        report)
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
            //var synthetic : List<String> = synthesizeMake(makeKey, sentence, ignoreFirstParm)
            return null
        }

//        private fun synthesizeMake(
//                makeKey: String,
//                sentence: Function,
//                ignoreFirstParm: Boolean): List<String> {
//            return listOf()
//        }
    }

    init {
        val (makesMap, holes) = createMakesMap(functions, isMap)
        this.makesMap = makesMap
        this.holes = holes
        val analyzer = Analyzer(isMap, reverseIsMap, makesMap, holes)
        functions
                .filter { it.name == "sentence" }
                .forEach { analyzeSentence(it, analyzer) }
    }

    fun analyzeSentence(
            sentence: Function,
            analyzer: Analyzer = Analyzer(isMap, reverseIsMap, makesMap, holes)
    ): List<Unification> {
        assert(sentence.name == "sentence")
        val builder = StringBuilder()
        return analyzer.tryUnifyMake(
                Type("sentence"),
                sentence,
                { message -> builder.append("$message\n") })
                ?: throw RuntimeException("could not unify '$sentence':\n$builder")
    }
}