package com.jomofisher

import com.jomofisher.collections.joinToString
import com.jomofisher.collections.mapAs
import com.jomofisher.collections.toOneToOne
import com.jomofisher.collections.toTypedArray
import com.jomofisher.function.*
import com.jomofisher.function.Function
import com.jomofisher.sentences.toSentenceText
import java.io.File

class WaniKaniVocab(vocab: Array<Function>) {
    private val kanaIndex = vocab.indexByParameter(0)
    val kanjiIndex = vocab
            .indexByParameter(1)
            .toOneToOne()
    val vocab = vocab.map {
        Vocab(it)
    }
}

class Vocab(function: Node) {
    var kana: Function
    var kanji: String
    var english: String
    var level: Int

    init {
        val (kana, kanji, english, level) = function.destructure(
                "vocab($1,$2,$3,$4)",
                Function::class,
                String::class,
                String::class,
                Int::class)
        this.kana = kana
        this.kanji = kanji
        this.english = english
        this.level = level
    }

    override fun toString() = "$kana,$kanji,$english,$level"
}

fun WaniKaniVocab.annotate(node: Function): Function {
    return node.rewrite {
        val sentence = it.toSentenceText()
        val index = kanjiIndex[sentence]
        if (index != null) {
            val vocab = vocab[index]
            it
                    .annotate("wani-kani-level", vocab.level.toString())
                    .annotate("wani-kani-kana", vocab.kana.parms.joinToString(","))
        } else {
            it
        }
    } as Function
}

fun readWaniKaniVocab(file: File): WaniKaniVocab {
    return WaniKaniVocab(parseLispy(file)
            .keepName("vocab")
            .mapAs<Function>()
            .toTypedArray())
}