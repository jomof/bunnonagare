package com.jomofisher

import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class FunctionKtTest {

    @Test
    fun empty() {
        val result = parse("") as? Label ?: throw RuntimeException("unexpected")
        assertThat(result.label).isEmpty()
    }

    @Test
    fun simpleSentence() {
        val result = parseFunction("sentence(\"x\", (それ, は), 椅子, です)")
        assertThat(result.name).isEqualTo("sentence")
        assertThat(result.parms).hasSize(4)
        val child = result.parms[1] as Function
        assertThat(child.parms).hasSize(2)
    }

    @Test
    fun rightParenOnly() {
        assertFailsWith("Unmatched right paren: )", {
            parseFunction(")")
        })
    }

    @Test
    fun literal() {
        val result = parse("literal") as? Label ?: throw RuntimeException("unexpected")
        assertThat(result.label).isEqualTo("literal")
    }

    @Test
    fun zeroParam() {
        val result = parse("label()") as? Label ?: throw RuntimeException("unexpected")
        assertThat(result.label).isEqualTo("label")
    }

    @Test
    fun oneParam() {
        val result = parseFunction("function(one)")
        assertThat(result.name).isEqualTo("function")
        assertThat(result.parms).hasSize(1)
        assertThat(result.parms[0].toString()).isEqualTo("one")
    }

    @Test
    fun twoParam() {
        val result = parseFunction("function(one,two)")
        assertThat(result.name).isEqualTo("function")
        assertThat(result.parms).hasSize(2)
        assertThat(result.parms[0].toString()).isEqualTo("one")
        assertThat(result.parms[1].toString()).isEqualTo("two")
    }

    @Test
    fun twoParamWithSpaces() {
        val result = parseFunction("function( one , two )")
        assertThat(result.name).isEqualTo("function")
        assertThat(result.parms).hasSize(2)
        assertThat(result.parms[0].toString()).isEqualTo("one")
        assertThat(result.parms[1].toString()).isEqualTo("two")
    }

    @Test
    fun oneFunctionParam() {
        val result = parseFunction("function(one(two))")
        assertThat(result.name).isEqualTo("function")
        assertThat(result.parms).hasSize(1)
        assertThat(result.parms[0].toString()).isEqualTo("one(two)")
        val child = result.parms[0] as Function
        assertThat(child.parms).hasSize(1)
        assertThat(child.parms[0].toString()).isEqualTo("two")
    }

    @Test
    fun oneParamQuoted() {
        val result = parseFunction("function( \"one , two\")")
        assertThat(result.name).isEqualTo("function")
        assertThat(result.parms).hasSize(1)
        assertThat(result.parms[0].toString()).isEqualTo("\"one , two\"")
    }
}