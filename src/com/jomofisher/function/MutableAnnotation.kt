package com.jomofisher.function

import com.jomofisher.collections.SList
import com.jomofisher.collections.map
import com.jomofisher.collections.slistOf
import com.jomofisher.collections.toSList
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class MutableAnnotatedFunction(override val name: String,
                               override val parms: SList<Node>?) : Function() {
    private val annotations: MutableMap<String, String> = mutableMapOf()

    fun annotate(key: String, value: String): MutableAnnotatedFunction {
        annotations[key] = value
        return this
    }

    operator fun get(key: String): String? {
        return annotations[key]
    }

    operator fun set(key: String, value: String) {
        if (annotations.containsKey(key)) {
            throw RuntimeException("can't replace value")
        }
        annotations[key] = value
    }

    fun annotations(): SList<Function>? {
        return annotations
                .map { (key, value) -> createFunction(key, value) }
                .sortedBy { it.toString() }
                .toSList()
    }

    fun applyAnnotationsTo(function: Function): Function {
        val result = function.toMutableAnnotation()
        annotations
                .forEach { (key, value) ->
                    result[key] = value
                }
        return result
    }
}

class MutableAnnotatedLabel(override val label: String) : Label() {
    private val annotations: MutableMap<String, String> = mutableMapOf()

    fun annotate(key: String, value: String): MutableAnnotatedLabel {
        annotations[key] = value
        return this
    }

    operator fun get(key: String): String? {
        return annotations[key]
    }

    operator fun set(key: String, value: String) {
        if (annotations.containsKey(key)) {
            throw RuntimeException("can't replace value")
        }
        annotations[key] = value
    }

    fun annotations(): SList<Function>? {
        return annotations
                .map { (key, value) -> createFunction(key, value) }
                .sortedBy { it.toString() }
                .toSList()
    }
}

fun Function.toMutableAnnotation(): MutableAnnotatedFunction {
    if (this is MutableAnnotatedFunction) {
        return this
    }
    return MutableAnnotatedFunction(this.name, this.parms)
}

fun Label.toMutableAnnotation(): MutableAnnotatedLabel {
    if (this is MutableAnnotatedLabel) {
        return this
    }
    return MutableAnnotatedLabel(this.label)
}

fun Label.annotate(key: String, value: String): Label {
    return toMutableAnnotation()
            .annotate(key, value)
}

fun Function.annotate(key: String, value: String): Function {
    return toMutableAnnotation()
            .annotate(key, value)
}

fun Node.annotate(key: String, value: String): Node {
    return when (this) {
        is Label -> annotate(key, value)
        is Function -> annotate(key, value)
        else -> throw RuntimeException()
    }
}

fun SList<Function>?.annotate(key: String, value: String): SList<Function>? {
    return map { it.annotate(key, value) }
}

fun Node.exposeAnnotation(): Node {
    return when (this) {
        is Label -> exposeAnnotation()
        is Function -> exposeAnnotation()
        else -> throw RuntimeException()
    }
}

fun Label.exposeAnnotation(): Node {
    if (this is MutableAnnotatedLabel) {
        val label = createLabel(label)
        val annotations = annotations()
                ?: return label
        return createFunction("a",
                slistOf(label, createFunction(annotations)))
    }
    return this
}

fun Function.exposeAnnotation(): Function {
    val parms = this.parms.map { it.exposeAnnotation() }
    val original = createFunction(name, parms)
    if (this is MutableAnnotatedFunction) {
        val annotations = annotations()
                ?: return original
        val annotationsNode = createFunction(annotations)
        return createFunction("a", slistOf(original, annotationsNode))
    }
    return original
}

fun SList<Function>?.exposeAnnotations(): SList<Function>? {
    return this.map { it.exposeAnnotation() }
}
