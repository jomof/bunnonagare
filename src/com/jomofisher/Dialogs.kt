package com.jomofisher

import com.jomofisher.collections.*
import com.jomofisher.function.*
import com.jomofisher.function.Function
import java.io.File

class Dialogs(var dialogs: SList<Function>?)

fun Dialogs.allSentences(): SList<Function>? {
    return dialogs
            .map {
                val (_, parms) = it
                parms.keepName("line")
            }
            .flatten()
            .map {
                val (_, parms) = it
                Function("sentence", parms.drop(1))
            }
}

fun createDialogFromFolder(folder: File): Dialogs {
    val result = folder
            .walkTopDown()
            .filter {
                it.isFile
            }
            .map {
                Function("dialog", parseLispy(it))
            }
            .toList()
            .toSList()
    return Dialogs(result)
}

