package com.jomofisher

import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class SListTest {

    @Test
    fun simple() {
        val list = slistOf("a", "b", "c")
        assertThat(list.toString()).isEqualTo("[a, b, c]")
    }

    @Test
    fun slowlyPostpend() {
        val list = slistOf("a", "b", "c").slowlyPostpend("d")
        assertThat(list.toString()).isEqualTo("[a, b, c, d]")
    }

    @Test
    fun push() {
        val list = slistOf("a", "b", "c").push("d")
        assertThat(list.toString()).isEqualTo("[d, a, b, c]")
    }

    @Test
    fun reversed() {
        val list = slistOf("a", "b", "c").reversed()
        assertThat(list.toString()).isEqualTo("[c, b, a]")
    }

    @Test
    fun plusList() {
        val list = slistOf("a", "b", "c") + slistOf("d", "e")
        assertThat(list.toString()).isEqualTo("[a, b, c, d, e]")
    }

    @Test
    fun map() {
        val list = slistOf("a", "b", "c").map { "-$it-" }
        assertThat(list.toString()).isEqualTo("[-a-, -b-, -c-]")
    }

    @Test
    fun flatten() {
        val list = slistOf(slistOf("a", "b"), slistOf("c", "d")).flatten()
        assertThat(list.toString()).isEqualTo("[a, b, c, d]")
    }

    @Test
    fun toSList() {
        val list = listOf("a", "b").toSList()
        assertThat(list.toString()).isEqualTo("[a, b]")
    }
}