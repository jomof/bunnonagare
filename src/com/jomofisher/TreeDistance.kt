package com.jomofisher

import kotlin.math.min

class Empty : Node()

class Differ {
    private val deleteCost = 1
    private val insertCost = 1
    private val renameCost = 1

    private fun deleteLeft(t: Function): Pair<Node, Node> {
        val left = t.parms.head()
        val remainder = t.parms.drop(1)
        if (remainder.isEmpty()) {
            return Pair(left, Empty())
        }
        return when (left) {
            is Label -> Pair(left, createNode(t.name, remainder))
            is Function -> Pair(left, createNode(t.name, left.parms.plus(remainder)))
            else -> throw RuntimeException("unexpected")
        }
    }

    private fun cr(left: Node, right: Node): Int {
        if (left is Label && right is Label && left.label != right.label) {
            return renameCost
        }
        return 0
    }

    private fun d(t1: Function, t2: Function): Int {
        val (t1Left, t1MinusLeft) = deleteLeft(t1)
        val (t2Left, t2MinusLeft) = deleteLeft(t2)
        val delete = d(t1MinusLeft, t2) + deleteCost
        val insert = d(t1, t2MinusLeft) + insertCost
        val rename = d(t1MinusLeft, t2MinusLeft) + cr(t1Left, t2Left)
        return min(min(delete, insert), rename)
    }

    private fun d(t1: Empty, t2: Function): Int {
        val (_, remainder) = deleteLeft(t2)
        return d(t1, remainder) + insertCost
    }

    private fun d(t1: Function, t2: Label): Int {
        val (left, remainder) = deleteLeft(t1)
        val delete = d(remainder, t2) + deleteCost
        val frazzle = d(left, t2) + d(remainder, t2)
        return min(delete, frazzle)
    }

    private fun d(t1: Function, t2: Empty): Int {
        val (_, remainder) = deleteLeft(t1)
        return d(remainder, t2) + deleteCost
    }

    private fun d(t1: Function, t2: Node): Int {
        return when (t2) {
            is Function -> d(t1, t2)
            is Label -> d(t1, t2)
            is Empty -> d(t1, t2)
            else -> throw RuntimeException("unexpected")
        }
    }

    private fun d(t1: Label, t2: Function): Int {
        val (left, remainder) = deleteLeft(t2)
        val delete = d(t1, remainder) + insertCost
        val frazzle = d(t1, left) + d(t1, remainder)
        return min(delete, frazzle)
    }

    private fun d(t1: Label, t2: Node): Int {
        return when (t2) {
            is Function -> d(t1, t2)
            is Label -> cr(t1, t2)
            is Empty -> deleteCost
            else -> throw RuntimeException("unexpected")
        }
    }

    private fun d(t1: Empty, t2: Node): Int {
        return when (t2) {
            is Function -> d(t1, t2)
            is Label -> insertCost
            is Empty -> 0
            else -> throw RuntimeException("unexpected")
        }
    }

    fun d(t1: Node, t2: Node): Int {
        return when (t1) {
            is Function -> d(t1, t2)
            is Label -> d(t1, t2)
            is Empty -> d(t1, t2)
            else -> throw RuntimeException("unexpected")
        }
    }
}

fun distance(t1: Node, t2: Node): Int {
    return Differ().d(t1, t2)
}