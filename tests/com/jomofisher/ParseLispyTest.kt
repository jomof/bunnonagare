package com.jomofisher

import com.google.common.truth.Truth.assertThat
import com.jomofisher.collections.drop
import com.jomofisher.collections.get
import com.jomofisher.collections.size
import com.jomofisher.function.Function
import com.jomofisher.function.Label
import com.jomofisher.function.parseLispy
import com.jomofisher.function.parseLispyFunction
import org.junit.Test

internal class FunctionKtTest {

    @Test
    fun empty() {
        val result = parseLispy("") as? Label ?: throw RuntimeException("unexpected")
        assertThat(result.label).isEmpty()
    }

    @Test
    fun simpleSentence() {
        val result = parseLispyFunction("sentence(\"x\", (それ, は), 椅子, です)")
        assertThat(result.name).isEqualTo("sentence")
        assertThat(result.parms.size()).isEqualTo(4)
        val child = result.parms.drop(1)!!.value as Function
        assertThat(child.parms.size()).isEqualTo(2)
    }

    @Test
    fun rightParenOnly() {
        assertFailsWith("Unmatched right paren: )", {
            parseLispyFunction(")")
        })
    }

    @Test
    fun literal() {
        val result = parseLispy("literal") as? Label ?: throw RuntimeException("unexpected")
        assertThat(result.label).isEqualTo("literal")
    }

    @Test
    fun zeroParam() {
        val result = parseLispy("label()") as? Label ?: throw RuntimeException("unexpected")
        assertThat(result.label).isEqualTo("label")
    }

    @Test
    fun oneParam() {
        val result = parseLispyFunction("function(one)")
        assertThat(result.name).isEqualTo("function")
        assertThat(result.parms.size()).isEqualTo(1)
        assertThat(result.parms[0].toString()).isEqualTo("one")
    }

    @Test
    fun twoParam() {
        val result = parseLispyFunction("function(one,two)")
        assertThat(result.name).isEqualTo("function")
        assertThat(result.parms.size()).isEqualTo(2)
        assertThat(result.parms[0].toString()).isEqualTo("one")
        assertThat(result.parms.drop(1)[0].toString()).isEqualTo("two")
    }

    @Test
    fun twoParamWithSpaces() {
        val result = parseLispyFunction("function( one , two )")
        assertThat(result.toString()).isEqualTo("function(one,two)")
        assertThat(result.name).isEqualTo("function")
        assertThat(result.parms.size()).isEqualTo(2)
        assertThat(result.parms[0].toString()).isEqualTo("one")
        assertThat(result.parms.drop(1)[0].toString()).isEqualTo("two")
    }

    @Test
    fun oneFunctionParam() {
        val result = parseLispyFunction("function(one(two))")
        assertThat(result.name).isEqualTo("function")
        assertThat(result.parms.size()).isEqualTo(1)
        assertThat(result.parms[0].toString()).isEqualTo("one(two)")
        val child = result.parms[0] as Function
        assertThat(child.parms.size()).isEqualTo(1)
        assertThat(child.parms[0].toString()).isEqualTo("two")
    }

    @Test
    fun oneParamQuoted() {
        val result = parseLispyFunction("function( \"one , two\")")
        assertThat(result.name).isEqualTo("function")
        assertThat(result.parms.size()).isEqualTo(1)
        assertThat(result.parms[0].toString()).isEqualTo("\"one , two\"")
    }
}