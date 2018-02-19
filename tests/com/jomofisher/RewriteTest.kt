package com.jomofisher

import com.google.common.truth.Truth.assertThat
import com.jomofisher.function.*
import org.junit.Test

class RewriteTest {
    @Test
    fun simplest() {
        val result = createLabel("x")
                .rewrite {
                    val (name, _) = it
                    if (name == "x") {
                        createFunction("a", createLabel("b"))
                    } else {
                        it
                    }
                }
        assertThat(result).isEqualTo(parseLispy("a(b)"))
    }

    @Test
    fun functionOf() {
        val result = parseLispy("f(x)")
                .rewrite {
                    val (name, _) = it
                    if (name == "x") {
                        createFunction("a", createLabel("b"))
                    } else {
                        it
                    }
                }
        assertThat(result).isEqualTo(parseLispy("f(a(b))"))
    }

    @Test
    fun functionOfTwo() {
        val result = parseLispy("f(x,y)")
                .rewrite {
                    val (name, _) = it
                    if (name == "x") {
                        createFunction("a", createLabel("b"))
                    } else {
                        it
                    }
                }
        assertThat(result).isEqualTo(parseLispy("f(a(b),y)"))
    }

    @Test
    fun simplestPreservesAnnotation() {
        /*
        a(f(a(x,(name(x))),a(y,(name(y)))),(name(f)))
         */
        val result = parseLispy("x")
                .rewrite {
                    val (name, _) = it
                    if (name == "x") {
                        it.annotate("name", name)
                    } else {
                        it
                    }
                }
        assertThat(result).isEqualTo(parseLispy("x"))
        assertThat(result.exposeAnnotation()).isEqualTo(parseLispy("a(x,(name(x)))"))
    }

    @Test
    fun simplestFunctionAnnotation() {
        val result = parseLispy("f(x)")
                .rewrite {
                    val (name, _) = it
                    if (name == "x") {
                        it.annotate("name", name)
                    } else {
                        it
                    }
                }
        assertThat(result).isEqualTo(parseLispy("f(x)"))
        assertThat(result.exposeAnnotation()).isEqualTo(parseLispy("f(a(x,(name(x))))"))
    }

    @Test
    fun nestFunctionAnnotation() {
        val result = parseLispy("f(g(x))")
                .rewrite {
                    val (name, _) = it
                    if (name == "x") {
                        it.annotate("name", name)
                    } else {
                        it
                    }
                }
        assertThat(result).isEqualTo(parseLispy("f(g(x))"))
        assertThat(result.exposeAnnotation()).isEqualTo(
                parseLispy("f(g(a(x,(name(x)))))"))
    }
}