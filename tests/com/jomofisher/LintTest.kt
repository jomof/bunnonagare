package com.jomofisher

import com.google.common.truth.Truth.assertThat
import com.jomofisher.collections.*
import com.jomofisher.function.Function
import com.jomofisher.function.Node
import com.jomofisher.function.component1
import com.jomofisher.function.component2
import com.jomofisher.sentences.readClassifierFile
import com.jomofisher.sentences.readSentences
import org.junit.Test

class LintTest {

    private fun lintSentences(
            sentences: SList<Function>?,
            check: (Node, Int, Int) -> Unit) {
        sentences.forEach {
            try {
                lintFragment(it, check)
            } catch (e: RuntimeException) {
                if (e is KotlinNullPointerException) {
                    throw e
                }
                throw RuntimeException("sentence '$it': $e")
            }
        }
    }

    private fun lintFragment(
            fragment: Node,
            check: (Node, Int, Int) -> Unit,
            index: Int = 0,
            of: Int = 1) {
        check(fragment, index, of)
        val (_, parms) = fragment
        val size = parms.size()
        parms.forEachIndexed { i, child ->
            lintFragment(child, check, i, size)
        }
    }

    private fun readSentencesFromEveryWhere(): SList<Function>? {
        val grammarSentences = readSentences(rootFolder, sentencesFile)
        val dialogSentences = createDialogFromFolder(rootFolder, dialogFolder)
                .allSentences()
        return grammarSentences concat dialogSentences
    }

    @Test
    fun parenCountsMustMatch() {
        sentencesFile.readLines()
                .forEach {
                    val left = it.filter { it == '(' }.count()
                    val right = it.filter { it == ')' }.count()
                    assertThat(left).named(it).isEqualTo(right)
                }
    }

    @Test
    fun quotesMustBeEven() {
        sentencesFile.readLines()
                .forEach {
                    val left = it.filter { it == '\"' }.count()
                    assertThat(left % 2).named(it).isEqualTo(0)
                }
    }

    @Test
    fun stuffThatCantEndAThing() {
        val ontology = readOntologyFile(ontologyFile)
        val stuff = listOf(
                setOf("ます", "ました", "ません", "さん", "たち", "でした"),
                ontology.leafsUnder("suffix")
        ).flatten()

        lintSentences(readSentencesFromEveryWhere()) { node, _, _ ->
            val (name, _) = node
            stuff.forEach {
                if (name != it && name.endsWith(it)) {
                    throw RuntimeException("$name can't end with $it. It should be broken up.")
                }
            }
        }
    }

    @Test
    fun stuffThatCantStartAThing() {
        val stuff = arrayOf("お")
        lintSentences(readSentencesFromEveryWhere()) { node, _, _ ->
            val (name, _) = node
            stuff.forEach {
                if (name != it && name.startsWith(it)) {
                    if (!name.startsWith("おお")
                            && !name.startsWith("おも")) {
                        throw RuntimeException("$name can't start with $it. It should be broken up.")
                    }
                }
            }
        }
    }

    @Test
    fun iverbStemMustMatchSuffix() {
        val ontology = readOntologyFile(ontologyFile)
        val suffix = ontology.leafsUnder("suffix-verb-i-form")
        val stem = ontology.leafsUnder("stem-verb-i-form")
        assertThat(suffix.size).isGreaterThan(0)
        assertThat(stem.size).isGreaterThan(0)
        lintSentences(readSentencesFromEveryWhere()) { node, _, _ ->
            val (_, parms) = node
            if (parms.size() == 2) {
                val (left, _) = parms!!.value
                val (right, _) = parms.next!!.value
                val leftIsStem = stem.contains(left)
                val rightIsSuffix = suffix.contains(right)
                if (leftIsStem != rightIsSuffix) {
                    throw RuntimeException("stem '$left' does not agree with '$right")
                }
            }
        }
    }

    @Test
    fun stuffTheMustBeLastOfTwoOrOne() {
        val stuffThatMustBeLastOfTwo = arrayOf("です")
        lintSentences(readSentencesFromEveryWhere()) { node, index, of ->
            val (name, _) = node
            if (stuffThatMustBeLastOfTwo.contains(name)) {
                if (of > 2) {
                    throw RuntimeException("$name must be in function of one or two parameters")
                }
                if (index != of - 1) {
                    throw RuntimeException("$name must be last")
                }
            }
        }
    }

    @Test
    fun stuffThaMustBeFirstOfTwo() {
        val stuffThatMustBeLastOfTwo = arrayOf(
                "もう")
        lintSentences(readSentencesFromEveryWhere()) { node, index, of ->
            val (name, _) = node
            if (stuffThatMustBeLastOfTwo.contains(name)) {
                if (of != 2) {
                    throw RuntimeException("$name must be in function of two parameters")
                }
                if (index != 0) {
                    throw RuntimeException("$name must be first")
                }
            }
        }
    }

    @Test
    fun stuffTheMustBeLastOfTwo() {
        val stuffThatMustBeLastOfTwo = arrayOf(
                "で", "ね", "は", "が", "に", "を", "くなかった", "くない", "か", "ました",
                "見ます", "します", "にも", "な", "さん", "たち", "と", "の")
        lintSentences(readSentencesFromEveryWhere()) { node, index, of ->
            val (name, _) = node
            if (stuffThatMustBeLastOfTwo.contains(name)) {
                if (of != 2) {
                    throw RuntimeException("$name must be in function of two parameters")
                }
                if (index != of - 1) {
                    throw RuntimeException("$name must be last")
                }
            }
        }
    }

    @Test
    fun allClassifiersMustBeOntologyLeafs() {
        val ontology = readOntologyFile(ontologyFile)
        val classifiers = readClassifierFile(classifiersFile)
        classifiers.forEach {
            assertThat(ontology.isLeaf(it.production))
                    .named("classifier product ${it.production} is not an ontology leaf")
                    .isTrue()
        }
    }
}