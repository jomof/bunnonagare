package com.jomofisher

import com.jomofisher.collections.*
import com.jomofisher.function.Function
import com.jomofisher.function.keepName
import com.jomofisher.function.parseLispy
import java.io.File

fun readSentences(file: File): SList<Function> {
    return parseLispy(file)
            .keepName("sentence")
            .notEmpty()
            .mapAs()
}

fun getJapaneseOnly(sentences: SList<Function>): SList<Function> {
    return sentences
            .map { Function("", it.parms.drop(1).notEmpty()) }
}
