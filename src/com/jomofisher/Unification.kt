package com.jomofisher

open class Unification

class SingleUnification(private val type: String, private val value: Function) : Unification() {
    override fun toString(): String {
        return "$value -> $type"
    }
}

class UnificationList(val unifications: List<Unification>) : Unification() {
    override fun toString(): String {
        return "(" + unifications.joinToString(",") + ")"
    }
}

class FailedUnification(val message: String) : Unification() {
    override fun toString(): String {
        return message
    }
}