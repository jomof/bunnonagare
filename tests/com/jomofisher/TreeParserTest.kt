package com.jomofisher

import com.google.common.truth.Truth.assertThat
import com.jomofisher.collections.forEach
import com.jomofisher.function.TreeParser
import com.jomofisher.function.invert
import com.jomofisher.function.parseLispy
import org.junit.Test

internal class TreeParserTest {
    @Test
    fun empty() {
        val result = TreeParser(listOf("")).parse()
        assertThat(result.toString()).isEqualTo("[]")
        assertThat(result.invert().toString()).isEqualTo("[]")
    }

    @Test
    fun single() {
        val result = TreeParser(listOf("a")).parse()
        assertThat(result.toString()).isEqualTo("[a]")
        assertThat(result.invert().toString()).isEqualTo("[a]")
    }

    @Test
    fun double() {
        val result = TreeParser(listOf("a", "b")).parse()
        assertThat(result.toString()).isEqualTo("[a, b]")
        assertThat(result.invert().toString()).isEqualTo("[a, b]")
    }

    @Test
    fun child() {
        val result = TreeParser(listOf("a", " b")).parse()
        assertThat(result.toString()).isEqualTo("[a(a-b)]")
        assertThat(result.invert().toString()).isEqualTo("[a, a-b(a)]")
    }

    @Test
    fun doubleChild() {
        val result = TreeParser(listOf("a", " b", " c")).parse()
        assertThat(result.toString()).isEqualTo("[a(a-b,a-c)]")
        assertThat(result.invert().toString()).isEqualTo("[a, a-b(a), a-c(a)]")
    }

    @Test
    fun doubleParent() {
        val result = TreeParser(listOf("a", " b", "c", " d")).parse()
        assertThat(result.toString()).isEqualTo("[a(a-b), c(c-d)]")
        assertThat(result.invert().toString()).isEqualTo("[a, a-b(a), c, c-d(c)]")
    }

    @Test
    fun singleParentSingleChild() {
        val result = TreeParser(listOf("a", " b", "c")).parse()
        assertThat(result.toString()).isEqualTo("[a(a-b), c]")
        assertThat(result.invert().toString()).isEqualTo("[a, a-b(a), c]")
    }

    @Test
    fun inlineLispy() {
        val result = TreeParser(listOf("a", " b", " c(d, e)")).parse()
        assertThat(result.toString()).isEqualTo("[a(a-b,a-c(d,e))]")
        assertThat(result.invert().toString()).isEqualTo("[a, a-b(a), a-c(a), d(a-c), e(a-c)]")
    }

    @Test
    fun nested() {
        val result = parseLispy("a(b(c(d)))")
        assertThat(result.toString()).isEqualTo("a(b(c(d)))")
        assertThat(result.invert().toString()).isEqualTo("[a, b(a), c(b), d(c)]")
    }

    @Test
    fun ontology() {
        val ontology = createOntologyFromFile(ontologyFile)
        ontology
                .forward
                .forEach { println("$it") }
    }
}