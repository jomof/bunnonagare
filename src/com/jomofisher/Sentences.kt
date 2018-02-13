package com.jomofisher

import java.io.File

fun readSentences(file: File): SList<Function>? {
    return parseLispy(file)
            .filterIsInstanceReversed<Function>()
            .filterReversed { it.name == "sentence" }
}
