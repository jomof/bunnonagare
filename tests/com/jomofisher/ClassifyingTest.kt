package com.jomofisher

import com.google.common.truth.Truth.assertThat
import com.jomofisher.collections.slistOf
import com.jomofisher.function.Function
import com.jomofisher.function.exposeAnnotation
import com.jomofisher.function.parseLispy
import com.jomofisher.sentences.annotate
import com.jomofisher.sentences.createClassifier
import org.junit.Test

class ClassifyingTest {
    @Test
    fun testMultipleSimple() {
        val classifiers = slistOf(
                createClassifier("match(p1, c(*,e))"),
                createClassifier("match(p2, d(*,e))"),
                createClassifier("match(p3, x(*,*))")
        )
        val tree = parseLispy("c(x,e)") as Function
        val rewritten = classifiers.annotate(tree)
        assertThat(rewritten.toString()).isEqualTo("c(x,e)")
        assertThat(rewritten.exposeAnnotation()).isEqualTo(
                parseLispy("a(c(x,e),(classification(p1)))"))
    }

    @Test
    fun testMultipleDouble() {
        val classifiers = slistOf(
                createClassifier("match(p1, c(*,e))"),
                createClassifier("match(p2, d(*,e))"),
                createClassifier("match(p3, x(*,*))")
        )
        val tree = parseLispy("x(c(x,e), d(x,e))") as Function
        val rewritten = classifiers.annotate(tree)
        assertThat(rewritten.toString()).isEqualTo("x(c(x,e),d(x,e))")
        assertThat(rewritten.exposeAnnotation()).isEqualTo(
                parseLispy("a(x(a(c(x,e),(classification(p1))),a(d(x,e),(classification(p2)))),(classification(p3)))"))
    }
}