package com.jomofisher.function

import com.jomofisher.collections.map
import com.jomofisher.collections.notEmpty

fun Node.rewrite(action: (Node) -> Node): Node {
    return when (this) {
        is Label -> action(this)
        is MutableAnnotatedFunction -> {
            var parms = parms.map {
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
                            parms.notEmpty().map {
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
