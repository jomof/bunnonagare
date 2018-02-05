package com.jomofisher

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.io.File

class SentenceAnalysisTest {
    @Test
    fun unifyImplicit() {
        val analysis = SentenceAnalysis(createFunctions(
                "is(noun)",
                "is(verb)",
                "is(english)",
                "is(sentence)",
                "makes(sentence, english, noun, verb)"
        ))
        val sentenceAnalysis = analysis.analyzeSentence(
                parse("sentence(hello world, bob, runs)"))
        assertThat(sentenceAnalysis).isNotNull()
        assertThat(sentenceAnalysis).hasSize(3)
        assertThat(sentenceAnalysis[0].toString()).isEqualTo("hello world -> english")
        assertThat(sentenceAnalysis[1].toString()).isEqualTo("bob -> noun")
        assertThat(sentenceAnalysis[2].toString()).isEqualTo("runs -> verb")
    }

    @Test
    fun unifyExplicit() {
        val analysis = SentenceAnalysis(createFunctions(
                "is(noun)",
                "is(english)",
                "is(dog, noun)",
                "is(sentence)",
                "makes(sentence, english, dog)"
        ))
        val sentenceAnalysis = analysis.analyzeSentence(
                parse("sentence(a dog, dog)"))
        assertThat(sentenceAnalysis).isNotNull()
        assertThat(sentenceAnalysis).hasSize(2)
        assertThat(sentenceAnalysis[1].toString()).isEqualTo("dog -> dog")
    }

    @Test
    fun unifyViaIntermediaryMake() {
        val analysis = SentenceAnalysis(createFunctions(
                "is(noun)",
                "is(verb)",
                "is(english)",
                "is(sentence)",
                "is(said, verb)",
                "makes(sentence, english, noun, verb)",
                "makes(sentence, english, noun, said, sentence)"
        ))
        val sentenceAnalysis = analysis.analyzeSentence(
                parse("sentence(\"bob said tom ran\", bob, said, (tom, ran))"))
        assertThat(sentenceAnalysis).isNotNull()
        assertThat(sentenceAnalysis).hasSize(4)
        assertThat(sentenceAnalysis[3].toString()).isEqualTo("(tom -> noun,ran -> verb)")
    }

    @Test
    fun unifyViaInherited() {
        val analysis = SentenceAnalysis(createFunctions(
                "is(noun)",
                "is(adj)",
                "is(sentence)",
                "is(english)",
                "is(i-adj, adj)",
                "is(oishi, i-adj)",
                "makes(sentence, english, adj, noun)"
        ))
        val sentenceAnalysis = analysis.analyzeSentence(
                parse("sentence(\"delicious cake\", oishi, cake)"))
        assertThat(sentenceAnalysis).isNotNull()
        assertThat(sentenceAnalysis).hasSize(3)
        assertThat(sentenceAnalysis[1].toString()).isEqualTo("oishi -> oishi")
    }

    @Test
    fun unifyViaMakesInherited() {
        val analysis = SentenceAnalysis(createFunctions(
                "is(pos)",
                "is(sentence)",
                "is(english)",
                "is(adj, pos)",
                "is(verb, pos)",
                "is(noun, pos)",
                "is(\"です\", verb)",
                "is(i-adj, adj)",
                "is(phrase, pos)",
                "is(wa-phrase, phrase)",
                "is(particle, pos)",
                "is(adj-suffix, pos)",
                "is(\"は\", particle)",
                "is(adj-stem, pos)",
                "is(i-adj-stem, adj-stem)",
                "is(negative-present-casual-i-adj, i-adj)",
                "is(i-adj-suffix, adj-suffix)",
                "is(negative-present-casual-i-adj-suffix, i-adj-suffix)",
                "is(\"くない\", negative-present-casual-i-adj-suffix)",
                "makes(wa-phrase, noun, \"は\")",
                "makes(negative-present-casual-i-adj, i-adj-stem, \"くない\")",
                "makes(sentence, english, wa-phrase, adj, \"です\")"
        ))
        val sentenceAnalysis = analysis.analyzeSentence(
                parse("sentence(\"The car is not big (casual)\", (車, は), (大き, くない), です)"))
        assertThat(sentenceAnalysis).isNotNull()
        assertThat(sentenceAnalysis).hasSize(4)
        assertThat(sentenceAnalysis[0].toString()).isEqualTo("\"The car is not big (casual)\" -> english")
        assertThat(sentenceAnalysis[1].toString()).isEqualTo("(車 -> noun,は -> \"は\")")
        assertThat(sentenceAnalysis[2].toString()).isEqualTo("(大き -> i-adj-stem,くない -> \"くない\")")
        assertThat(sentenceAnalysis[3].toString()).isEqualTo("です -> \"です\"")
    }

    @Test
    fun unifyViaMakesInheritedMissing() {
        val analysis = SentenceAnalysis(createFunctions(
                "is(pos)",
                "is(sentence)",
                "is(english)",
                "is(adj, pos)",
                "is(verb, pos)",
                "is(noun, pos)",
                "is(\"です\", verb)",
                "is(i-adj, adj)",
                "is(phrase, pos)",
                "is(wa-phrase, phrase)",
                "is(particle, pos)",
                "is(adj-suffix, pos)",
                "is(\"は\", particle)",
                "is(adj-stem, pos)",
                "is(i-adj-stem, adj-stem)",
                "is(negative-present-casual-i-adj, i-adj)",
                "is(i-adj-suffix, adj-suffix)",
                "is(negative-present-casual-i-adj-suffix, i-adj-suffix)",
                "is(\"くない\", negative-present-casual-i-adj-suffix)",
                "makes(wa-phrase, noun, \"は\")",
                "makes(negative-present-casual-i-adj, i-adj-stem, \"くない\")",
                "makes(sentence, english, wa-phrase, adj, \"です\")"
        ))
        assertFailsWithContains(
                "could not unify (安,くなかった) with adj", {
            analysis.analyzeSentence(
                    parse("sentence(\"The book was not cheap (casual)\", (本, は), (安, くなかった), です)")
            )
        })
    }

    @Test
    fun againstGrammar() {
        val all = parse(File("C:\\Users\\jomof\\IdeaProjects\\bunnonagare\\grammar.txt"))
        val analysis = SentenceAnalysis(all)
        val result = analysis.analyzeSentence(parse(
                "sentence(\"This pen is blue\", ((こ, の, ペン), は), 青い, です)"
        ))
        assertThat(result).hasSize(4)
    }
}