package com.jomofisher

import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class FunctionKtTest {

    @Test
    fun empty() {
        val result = parse("")
        assertThat(result.name).isEmpty()
        assertThat(result.parms).isEmpty()
    }

    @Test
    fun literal() {
        val result = parse("literal")
        assertThat(result.name).isEqualTo("literal")
        assertThat(result.parms).isEmpty()
    }

    @Test
    fun zeroParam() {
        val result = parse("function()")
        assertThat(result.name).isEqualTo("function")
        assertThat(result.parms).isEmpty()
    }

    @Test
    fun oneParam() {
        val result = parse("function(one)")
        assertThat(result.name).isEqualTo("function")
        assertThat(result.parms).hasSize(1)
        assertThat(result.parms[0].name).isEqualTo("one")
    }

    @Test
    fun twoParam() {
        val result = parse("function(one,two)")
        assertThat(result.name).isEqualTo("function")
        assertThat(result.parms).hasSize(2)
        assertThat(result.parms[0].name).isEqualTo("one")
        assertThat(result.parms[1].name).isEqualTo("two")
    }

    @Test
    fun twoParamWithSpaces() {
        val result = parse("function( one , two )")
        assertThat(result.name).isEqualTo("function")
        assertThat(result.parms).hasSize(2)
        assertThat(result.parms[0].name).isEqualTo("one")
        assertThat(result.parms[1].name).isEqualTo("two")
    }

    @Test
    fun oneFunctionParam() {
        val result = parse("function(one(two))")
        assertThat(result.name).isEqualTo("function")
        assertThat(result.parms).hasSize(1)
        assertThat(result.parms[0].name).isEqualTo("one")
        assertThat(result.parms[0].parms).hasSize(1)
        assertThat(result.parms[0].parms[0].name).isEqualTo("two")
    }

    @Test
    fun oneParamQuoted() {
        val result = parse("function( \"one , two\")")
        assertThat(result.name).isEqualTo("function")
        assertThat(result.parms).hasSize(1)
        assertThat(result.parms[0].name).isEqualTo("one , two")
    }
}