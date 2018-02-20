package com.jomofisher

import com.google.common.truth.Truth.assertThat
import com.jomofisher.collections.fairDjikstra
import com.jomofisher.collections.squareOf
import com.jomofisher.collections.toConnectionTree
import com.jomofisher.function.visitIndented
import org.junit.Test

internal class SquareTest {
    @Test
    fun testToString() {
        val square = squareOf(2) { i, j ->
            Pair(i, j)
        }
        assertThat(square.toString()).isEqualTo(
                "0 : [(0, 0), (1, 0)]\r\n" +
                        "1 : [(0, 1), (1, 1)]\r\n")
    }

    @Test
    fun testDjikstra() {
        val square = squareOf(20) { i, j ->
            (i + j * 17) % 5 + 1
        }
        val coverage = square.fairDjikstra()
        val connections = coverage.toConnectionTree()
        println("$square")
        println("$coverage")
        println("$connections")
        connections.visitIndented(0) { depth, name ->
            println("$name".padStart(depth * 2 + 1))
        }
    }
}
