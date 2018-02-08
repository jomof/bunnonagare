package com.jomofisher

import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class MakesKtTest {
    @Test
    fun simplest() {
        val (makesMap, _) = createTestMakesMap(
                "is(a)",
                "is(b)",
                "makes(b, a, a)")
        assertThat(makesMap.size).isEqualTo(1)
        assertThat(makesMap[Type("b")]).hasSize(1)
        assertThat(makesMap[Type("b")]?.get(0))
                .containsExactly(Type("a"), Type("a"))
    }

    @Test
    fun notEnoughParameters2() {
        assertFailsWith("expected makes(b,a) to have at least 3 parameters",
                {
                    createTestMakesMap(
                            "is(a)",
                            "is(b)",
                            "makes(b, a)")
                })
    }

    @Test
    fun notEnoughParameters1() {
        assertFailsWith("expected makes(b) to have at least 2 parameters",
                {
                    createTestMakesMap(
                            "is(a)",
                            "is(b)",
                            "makes(b)")
                })
    }

    @Test
    fun notEnoughParameters0() {
        assertFailsWith("expected makes to have at least 2 parameters",
                {
                    createTestMakesMap(
                            "is(a)",
                            "is(b)",
                            "makes()")
                })
    }

    @Test
    fun functionParameter() {
        assertFailsWith("expected makes(b,a,a(c)) to have all literal parameters, but 'a(c)' isn't literal",
                {
                    createTestMakesMap(
                            "is(a)",
                            "is(b)",
                            "makes(b, a, a(c))")
                })
    }

    @Test
    fun missingIs() {
        assertFailsWith("expected parameters of makes(b,a,a) to be in is-map, but a isn't there",
                {
                    createTestMakesMap(
                            "is(b)",
                            "makes(b, a, a)")
                })
    }
}