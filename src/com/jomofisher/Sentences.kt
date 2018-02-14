package com.jomofisher

import com.jomofisher.collections.SList
import com.jomofisher.collections.filterIsInstanceReversed
import com.jomofisher.collections.filterReversed
import com.jomofisher.function.Function
import com.jomofisher.function.parseLispy
import java.io.File

fun readSentences(file: File): SList<Function>? {
    return parseLispy(file)
            .filterIsInstanceReversed<Function>()
            .filterReversed { it.name == "sentence" }
}
