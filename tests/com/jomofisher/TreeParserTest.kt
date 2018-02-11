package com.jomofisher

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.io.File

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
    fun nested() {
        val result = parseLispy("a(b(c(d)))")
        assertThat(result.toString()).isEqualTo("a(b(c(d)))")
        assertThat(result.invert().toString()).isEqualTo("[a, b(a), c(b), d(c)]")
    }

    @Test
    fun ontology() {
        val lines = File(
                "C:\\Users\\jomof\\IdeaProjects\\bunnonagare\\ontology.txt")
                .readLines()
        TreeParser(lines).parse()
                .invert()
                .forEach { println("$it") }
    }
}