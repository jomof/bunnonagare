package com.jomofisher

import com.google.common.truth.Truth.assertThat
import com.jomofisher.collections.SequenceTriangle
import com.jomofisher.collections.fairDjikstra
import com.jomofisher.collections.forEachIndexed
import com.jomofisher.collections.memoize
import org.junit.Test

internal class TriangleTest {
    @Test
    fun testToString() {
        val result = SequenceTriangle(0) { i, j -> i + j }
        val djikstraPath = result.fairDjikstra()
        result.toString()
        djikstraPath.toString()
    }

    @Test
    fun fairDjikstraEmpty() {
        val empty = SequenceTriangle(0) { i, j -> 0 }
        val djikstraPath = empty.fairDjikstra()
        assertThat(djikstraPath.size()).isEqualTo(0)
    }

    @Test
    fun fairDjikstraOne() {
        val empty = SequenceTriangle(1) { i, j -> 1 }
        val djikstraPath = empty.fairDjikstra()
        assertThat(djikstraPath.size()).isEqualTo(1)
        assertThat(djikstraPath[0, 0]).isEqualTo(0)
    }

    @Test
    fun fairDjikstraTwoFullyConnected() {
        val costs = SequenceTriangle(2) { i, j -> 1 }
        val djikstraPath = costs.fairDjikstra()
        assertThat(djikstraPath.size()).isEqualTo(2)
        assertThat(djikstraPath[0, 0]).isEqualTo(0)
        assertThat(djikstraPath[0, 1]).isEqualTo(1)
    }

    @Test
    fun fairDjikstraThreeFullyConnected() {
        val empty = SequenceTriangle(3) { i, j -> 1 }
        val djikstraPath = empty.fairDjikstra()
        assertThat(djikstraPath.size()).isEqualTo(3)
        assertThat(djikstraPath[0, 0]).isEqualTo(0)
        assertThat(djikstraPath[0, 1]).isEqualTo(1)
        assertThat(djikstraPath[0, 2]).isEqualTo(1)
    }

    @Test
    fun fairDjikstraThreePartlyConnected() {
        val empty = SequenceTriangle(3) { i, j ->
            i - j + 10
        }
        val djikstraPath = empty.fairDjikstra()
        assertThat(djikstraPath.size()).isEqualTo(3)
        assertThat(djikstraPath[0, 0]).isEqualTo(0)
        assertThat(djikstraPath[0, 1]).isEqualTo(9)
        assertThat(djikstraPath[0, 2]).isEqualTo(8)
        assertThat(djikstraPath[1, 2]).isEqualTo(9)
    }

    @Test
    fun memoizeRoundTrip() {
        val triangle = SequenceTriangle(2) { i, j -> i + 17 * j }
        val memoized = triangle.memoize()
        triangle.forEachIndexed { i, j, t ->
            assertThat(memoized[i, j]).isEqualTo(t)
        }
        memoized.forEachIndexed { i, j, t ->
            assertThat(triangle[i, j]).isEqualTo(t)
        }
    }
}