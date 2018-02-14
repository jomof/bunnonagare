package com.jomofisher

import com.google.common.truth.Truth.assertThat
import com.jomofisher.function.*
import org.junit.Test

class TreeEditDistance {
    private val fragmentIndexBuilder = FragmentIndexBuilder()

    private fun parse(line: String): OrdinalNode {
        return fragmentIndexBuilder
                .rewriteToOrdinal(parseLispy(line))
    }

    @Test
    fun identity() {
        val result = parse("a(b,c)")
        val distance = distance(result, result)
        assertThat(distance).isEqualTo(0)
    }

    @Test
    fun label() {
        val distance = distance(parse("a"), parse("b"))
        assertThat(distance).isEqualTo(1)
    }

    @Test
    fun repro() {
        val x = parse("((((私,は),(((有名,な),レストラン),で),(食事,を))),(し,ました))")
        val y = parse("(((((こ,の),いす),は),(古,くない)),です)")
        distance(x, y)
    }
}