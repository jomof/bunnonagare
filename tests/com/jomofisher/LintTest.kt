package com.jomofisher

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
                throw RuntimeException("sentence '$it': ${e.message}")
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
        val stuff = arrayOf("ます", "ました", "ません")
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
    fun stuffTheMustBeMiddleOfThree() {
        val stuffThatMustBeLastOfTwo = arrayOf("の")
        lintSentences(readSentencesFromEveryWhere()) { node, index, of ->
            val (name, _) = node
            if (stuffThatMustBeLastOfTwo.contains(name)) {
                if (of != 3) {
                    throw RuntimeException("$name must be in function of three parameters")
                }
                if (index != 1) {
                    throw RuntimeException("$name must be middle")
                }
            }
        }
    }

    @Test
    fun stuffTheMustBeMiddleOfThreeOrLastOfTwo() {
        val stuffThatMustBeLastOfTwo = arrayOf("と")
        lintSentences(readSentencesFromEveryWhere()) { node, index, of ->
            val (name, _) = node
            if (stuffThatMustBeLastOfTwo.contains(name)) {
                if (of != 3 && of != 2) {
                    throw RuntimeException("$name must be in function of three parameters")
                }
                if (index != 1) {
                    throw RuntimeException("$name must be middle of three or last of two")
                }
            }
        }
    }


    @Test
    fun stuffTheMustBeLastOfTwo() {
        val stuffThatMustBeLastOfTwo = arrayOf(
                "で", "ね", "は", "が", "に", "を", "くなかった", "くない", "か", "ました",
                "見ます", "します", "にも")
        lintSentences(readSentencesFromEveryWhere()) { node, index, of ->
            val (name, _) = node
            if (stuffThatMustBeLastOfTwo.contains(name)) {
                if (of != 2) {
                    throw RuntimeException("$name must be in function of one or two parameters")
                }
                if (index != of - 1) {
                    throw RuntimeException("$name must be last")
                }
            }
        }
    }
}