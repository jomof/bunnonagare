package com.jomofisher

import com.jomofisher.collections.mapAsEmpty
import com.jomofisher.collections.toOneToOne
import com.jomofisher.collections.toTypedArray
import com.jomofisher.function.Function
import com.jomofisher.function.indexByParameter
import com.jomofisher.function.keepName
import com.jomofisher.function.parseLispy
import java.io.File

class WaniKaniVocab(vocab: Array<Function>) {
    private val kanaIndex = vocab.indexByParameter(0)
    private val kanjiIndex = vocab
            .indexByParameter(1)
            .toOneToOne()
}

fun readWaniKaniVocab(file: File): WaniKaniVocab {
    return WaniKaniVocab(parseLispy(file)
            .keepName("vocab")
            .mapAsEmpty<Function>()
            .toTypedArray())
}