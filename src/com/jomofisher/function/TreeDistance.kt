package com.jomofisher.function

import com.jomofisher.collections.*
import kotlin.math.min

class Empty : OrdinalNode(-1)

class Differ(private val cached: MutableTriangle<Int, Int>) {
    private val deleteCost = 1
    private val insertCost = 1
    private val renameCost = 1

    private fun deleteLeft(t: OrdinalFunction): Pair<OrdinalNode, OrdinalNode> {
        val left = t.parms.head()
        val remainder = t.parms.drop(1)
        if (remainder.isEmpty()) {
            return Pair(left, Empty())
        }
        return when (left) {
            is OrdinalLabel -> Pair(left, createNode(t.ordinal, remainder))
            is OrdinalFunction -> Pair(left, createNode(t.ordinal, left.parms.plus(remainder)))
            else -> throw RuntimeException("unexpected")
        }
    }

    private fun cr(left: OrdinalNode, right: OrdinalNode): Int {
        if (left.ordinal != right.ordinal) {
            return renameCost
        }
        return 0
    }

    private fun memoized(t1: Int, t2: Int, action: () -> Int): Int {
        if (t1 == t2) {
            return 0
        }
        val cachedResult = cached[t1, t2]
        if (cachedResult != null) {
            return cachedResult
        }
        val result = action()
        cached[t1, t2] = result
        return result
    }

    private fun d(t1: OrdinalFunction, t2: OrdinalFunction): Int {
        return memoized(t1.ordinal, t2.ordinal) {
            val (t1Left, t1MinusLeft) = deleteLeft(t1)
            val (t2Left, t2MinusLeft) = deleteLeft(t2)
            val rename = d(t1MinusLeft, t2MinusLeft) + cr(t1Left, t2Left)
            if (rename == 0) {
                0
            } else {
                val delete = d(t1MinusLeft, t2) + deleteCost
                if (delete == 0) {
                    0
                } else {
                    val insert = d(t1, t2MinusLeft) + insertCost
                    min(min(delete, insert), rename)
                }
            }
        }
    }

    private fun d(t1: Empty, t2: OrdinalFunction): Int {
        val (_, remainder) = deleteLeft(t2)
        return d(t1, remainder) + insertCost
    }

    private fun d(t1: OrdinalFunction, t2: OrdinalLabel): Int {
        return memoized(t1.ordinal, t2.ordinal) {
            val (left, remainder) = deleteLeft(t1)
            val delete = d(remainder, t2) + deleteCost
            val frazzle = d(left, t2) + d(remainder, t2)
            min(delete, frazzle)
        }
    }

    private fun d(t1: OrdinalFunction, t2: Empty): Int {
        val (_, remainder) = deleteLeft(t1)
        return d(remainder, t2) + deleteCost
    }

    private fun d(t1: OrdinalFunction, t2: OrdinalNode): Int {
        return when (t2) {
            is OrdinalFunction -> d(t1, t2)
            is OrdinalLabel -> d(t1, t2)
            is Empty -> d(t1, t2)
            else -> throw RuntimeException("unexpected")
        }
    }

    private fun d(t1: OrdinalLabel, t2: OrdinalFunction): Int {
        val (left, remainder) = deleteLeft(t2)
        val delete = d(t1, remainder) + insertCost
        val frazzle = d(t1, left) + d(t1, remainder)
        return min(delete, frazzle)
    }

    private fun d(t1: OrdinalLabel, t2: OrdinalNode): Int {
        return when (t2) {
            is OrdinalFunction -> d(t1, t2)
            is OrdinalLabel -> cr(t1, t2)
            is Empty -> deleteCost
            else -> throw RuntimeException("unexpected")
        }
    }

    private fun d(t1: Empty, t2: OrdinalNode): Int {
        return when (t2) {
            is OrdinalFunction -> d(t1, t2)
            is OrdinalLabel -> insertCost
            is Empty -> 0
            else -> throw RuntimeException("unexpected")
        }
    }

    fun d(t1: OrdinalNode, t2: OrdinalNode): Int {
        return when (t1) {
            is OrdinalFunction -> d(t1, t2)
            is OrdinalLabel -> d(t1, t2)
            is Empty -> d(t1, t2)
            else -> throw RuntimeException("unexpected")
        }
    }
}

fun distance(t1: OrdinalNode, t2: OrdinalNode): Int {
    return Differ(mutableTriangleOf()).d(t1, t2)
}