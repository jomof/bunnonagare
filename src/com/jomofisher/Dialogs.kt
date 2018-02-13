package com.jomofisher

import java.io.File

class Dialogs(var dialogs: SList<Function>?)

fun Dialogs.allSentences(): SList<Function>? {
    return dialogs
            .map {
                var (_, parms) = it
                parms.keepName("line")
            }
            .flatten()
            .map {
                var (_, parms) = it
                Function("sentence", parms.drop(1))
            }
}

fun createDialogFromFolder(folder: File): Dialogs {
    var result = folder
            .walkTopDown()
            .filter { it.isFile }
            .map { Function("dialog", parseLispy(it)) }
            .toList()
            .toSList()
    return Dialogs(result)
}

