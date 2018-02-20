package com.jomofisher.function

import com.jomofisher.collections.map

fun Node.rewrite(action: (Node) -> Node): Node {
    return when (this) {
        is Label -> action(this)
        is MutableAnnotatedFunction -> {
            val parms = parms.map {
                it.rewrite(action)
            }
            return action(applyAnnotationsTo(createFunction(name, parms)))
        }
        is Function -> {
            val rewritten = action(this)
            when (rewritten) {
                is Label -> rewritten
                is Function -> {
                    val (name, parms) = rewritten
                    var result = createFunction(
                            name,
                            parms.map {
                                it.rewrite(action)
                            })
                    if (rewritten is MutableAnnotatedFunction) {
                        result = rewritten.applyAnnotationsTo(result)
                    }
                    result
                }
                else -> throw RuntimeException(rewritten.toString())
            }
        }
        else -> throw RuntimeException()
    }
}
