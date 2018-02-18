package com.jomofisher

import com.jomofisher.collections.*
import com.jomofisher.function.Function
import com.jomofisher.function.keepName
import com.jomofisher.function.parseLispy
import java.io.File

class Dialogs(var dialogs: SList<Function>)

fun Dialogs.allSentences(): SList<Function> {
    return dialogs
            .map {
                it.parms.keepName("line").notEmpty().mapAs<Function>()
            }
            .flatten()
            .map {
                Function("sentence", it.parms.next.notEmpty())
            }
}

fun createDialogFromFolder(folder: File): Dialogs {
    val result = folder
            .walkTopDown()
            .filter {
                it.isFile
            }
            .map {
                Function("dialog", parseLispy(it).notEmpty())
            }
            .toList()
            .toSList()
            .notEmpty()
    return Dialogs(result)
}

