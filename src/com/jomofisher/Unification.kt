package com.jomofisher

open class Unification

class SingleUnification(private val element: Element, private val value: Type) : Unification() {
    override fun toString(): String {
        return "$value -> $element"
    }
}

class UnificationList(val unifications: List<Unification>) : Unification() {
    override fun toString(): String {
        return "(" + unifications.joinToString(",") + ")"
    }
}
