package com.jomofisher

import com.google.common.truth.Truth.assertThat
import com.jomofisher.function.Node
import com.jomofisher.function.createLabel
import com.jomofisher.function.destructure
import com.jomofisher.function.parseLispy
import org.junit.Test

class DestructuringTest {
    @Test
    fun testSimpleMatch() {
        val extraction = parseLispy("a(b, c)")
                .destructure("a($1, $2)")
        assertThat(extraction).hasSize(2)
        assertThat(extraction).containsEntry("1", createLabel("b"))
        assertThat(extraction).containsEntry("2", createLabel("c"))
    }

    @Test
    fun testSimpleNoMatch() {
        val extraction = parseLispy("x(b, c)")
                .destructure("a($1, $2)")
        assertThat(extraction).isNull()
    }

    @Test
    fun testSimpleTyped() {
        val extraction = parseLispy("match(a, b)")
                .destructure("match($1, $2)", String::class)
        assertThat(extraction).isEqualTo("a")
    }

    @Test
    fun testDoubleTyped() {
        val (one, two) = parseLispy("match(a, b(c))")
                .destructure("match($1, $2)", String::class, Node::class)
        assertThat(one).isEqualTo("a")
        assertThat(two).isEqualTo(parseLispy("b(c)"))
    }

    @Test
    fun literalMatch() {
        val matches = parseLispy("a")
                .destructure("a")
        assertThat(matches).isNotNull()
    }
}