package com.jomofisher

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LintTest {

    private fun lintSentences(
            sentences: SList<Function>?,
            check: (Node, Int, Int) -> Unit) {
        sentences.forEach {
            try {
                val (_, parms) = it
                lintFragment(Function("", parms.drop(1)), check)
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
        val grammarSentences = readSentences(sentencesFile)
        val dialogSentences = createDialogFromFolder(dialogFolder)
                .allSentences()
        return grammarSentences + dialogSentences
    }

    @Test
    fun stuffThatCantEndAThing() {
        val stuff = arrayOf("ます", "ました", "ません", "さん", "たち")
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
    fun iverbStemMustMatchSuffix() {
        val ontology = createOntologyFromFile(ontologyFile)
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
}