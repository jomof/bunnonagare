package com.jomofisher

import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class SListTest {

    @Test
    fun simple() {
        var list = slistOf("a", "b", "c")
        assertThat(list.toString()).isEqualTo("[a, b, c]")
    }

    @Test
    fun plusElement() {
        var list = slistOf("a", "b", "c") + "d"
        assertThat(list.toString()).isEqualTo("[a, b, c, d]")
    }

    @Test
    fun push() {
        var list = slistOf("a", "b", "c").push("d")
        assertThat(list.toString()).isEqualTo("[d, a, b, c]")
    }

    @Test
    fun reversed() {
        var list = slistOf("a", "b", "c").reversed()
        assertThat(list.toString()).isEqualTo("[c, b, a]")
    }

    @Test
    fun plusList() {
        var list = slistOf("a", "b", "c") + slistOf("d", "e")
        assertThat(list.toString()).isEqualTo("[a, b, c, d, e]")
    }

    @Test
    fun map() {
        var list = slistOf("a", "b", "c").map { "-$it-" }
        assertThat(list.toString()).isEqualTo("[-a-, -b-, -c-]")
    }

    @Test
    fun flatten() {
        var list = slistOf(slistOf("a", "b"), slistOf("c", "d"))
        assertThat(list.toString()).isEqualTo("[[a, b], [c, d]]")
    }
}