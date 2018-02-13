package com.jomofisher

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.io.File

class TreeEditDistance {

    @Test
    fun identity() {
        val result = parseLispy("a(b,c)")
        val distance = distance(result, result)
        assertThat(distance).isEqualTo(0)
    }

    @Test
    fun label() {
        val distance = distance(parseLispy("a"), parseLispy("b"))
        assertThat(distance).isEqualTo(1)
    }

    @Test
    fun grammar() {
        val all =
                parseLispy(File("C:\\Users\\jomof\\IdeaProjects\\bunnonagare\\data\\grammar.txt"))
                        .filterIsInstance<Function>()
                        .filter { it.name == "sentence" }
                        .map { Function("", it.parms.drop(1)) }
                        .toTypedArray()
        val from = all
                .mapIndexed { i, a ->
                    all
                            .filterIndexed { j, _ -> i > j }
                            .map { b -> distance(a, b) }
                            .toTypedArray()
                }.toTypedArray()
        from.mapIndexed { i, to ->
            to.forEachIndexed { j, distance ->
                println("${all[i]} -> ${all[j]} = $distance")
            }
        }
    }
}