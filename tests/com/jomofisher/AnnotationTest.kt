package com.jomofisher

import com.google.common.truth.Truth.assertThat
import com.jomofisher.function.annotate
import com.jomofisher.function.createFunction
import com.jomofisher.function.createLabel
import com.jomofisher.function.toMutableAnnotation
import org.junit.Test

class AnnotationTest {
    @Test
    fun testLabel() {
        val result = createLabel("x")
                .annotate("key", "value")
        assertThat(result.toString()).isEqualTo("x")
        assertThat(result.toMutableAnnotation()["key"]).isEqualTo("value")
    }

    @Test
    fun testSingleFunction() {
        val result = createFunction("", "x")
                .annotate("key", "value")
        assertThat(result.toString()).isEqualTo("(x)")
        assertThat(result.toMutableAnnotation()["key"]).isEqualTo("value")
    }

    @Test
    fun testNestedFunction() {
        val result = createFunction("", createFunction("", "x"))
                .annotate("key", "value")
        assertThat(result.toString()).isEqualTo("((x))")
        assertThat(result.toMutableAnnotation()["key"]).isEqualTo("value")
    }

    @Test
    fun testSecondAnnotation() {
        val result = createLabel("x")
                .annotate("key", "value")
                .annotate("key2", "value2")
        assertThat(result.toString()).isEqualTo("x")
        assertThat(result.toMutableAnnotation()["key"]).isEqualTo("value")
        assertThat(result.toMutableAnnotation()["key2"]).isEqualTo("value2")
    }
}