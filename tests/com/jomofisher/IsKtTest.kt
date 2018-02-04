package com.jomofisher

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.test.fail

internal class IsKtTest {

    private fun createTestIsMap(vararg lines: String): Map<String, String> {
        val functions = lines.map { parse(it) }.toList()
        return createIsMap(functions)
    }

    @Test
    fun singleIs() {
        assertFailsWith(
                "is-function is a literal or parameterless function",
                { createTestIsMap("is") })
    }

    @Test
    fun isWithFunction() {
        assertFailsWith(
                "something(other-thing) isn't literal",
                { createTestIsMap("is(something(other-thing))") })
    }

    @Test
    fun parameterLessIs() {
        assertFailsWith(
                "is-function is a literal or parameterless function",
                { createTestIsMap("is()") })
    }

    @Test
    fun threeParameters() {
        assertFailsWith(
                "is-function is(a,b,c) has too many parameters",
                { createTestIsMap("is(a, b, c)") })
    }

    @Test
    fun singleParameterIs() {
        val map = createTestIsMap("is(something)")
        assertThat(map).hasSize(1)
        assertThat(map).containsEntry("something", "root-of-is")
    }

    @Test
    fun twoParameterIs() {
        val map = createTestIsMap("is(something)", "is(other, something)")
        assertThat(map).hasSize(2)
        assertThat(map).containsEntry("something", "root-of-is")
        assertThat(map).containsEntry("other", "something")
    }

    @Test
    fun repeatedDefinition() {
        assertFailsWith(
                "is-function something is already assigned",
                { createTestIsMap("is(something)", "is(something)") })
    }

    private fun assertFailsWith(message: String, function: () -> Unit) {
        try {
            function()
        } catch (e: RuntimeException) {
            assertThat(e).hasMessageThat().isEqualTo(message)
            return
        }
        fail("Expected failure")


    }
}