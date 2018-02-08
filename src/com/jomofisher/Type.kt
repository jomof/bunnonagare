package com.jomofisher

open class Element

class Literal(val value: String) : Element() {
    override fun toString(): String {
        return value
    }
}

class Type(val name: String) : Element() {
    override fun toString(): String {
        return name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        when (other) {
            is Type -> return name == other.name
        }
        throw RuntimeException("unexpected")
    }
}
