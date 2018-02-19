package com.jomofisher

import com.jomofisher.collections.*
import com.jomofisher.function.*
import com.jomofisher.function.Function
import java.io.File

class Dialog(val filePath: String, val setting: String, val lines: SList<Function>)

fun readDialog(root: File, file: File): Dialog {
    val parsed = parseLispy(file).notEmpty().mapAs<Function>()
    val setting = parsed.getScalar("setting") ?: ""
    return Dialog(
            file.absolutePath.substring(root.absolutePath.length + 1),
            setting,
            parseLispy(file).notEmpty().mapAs())

}

class Dialogs(val dialogs: SList<Dialog>)

fun Dialogs.allSentences(): SList<Function> {
    return dialogs
            .map {
                it.lines
                        .keepName("line")
                        .notEmpty()
                        .map {
                            val speaker = it.parms[0].component1()
                            val english = it.parms[1].component1()
                            val japanese = createFunction(it.parms.drop(2)!!)
                            japanese
                                    .annotate("speaker", speaker)
                                    .annotate("english", english)
                        }
                        .annotate("setting", it.setting)
                        .annotate("source-file", it.filePath)
            }
            .flatten()
}

fun createDialogFromFolder(root: File, folder: File): Dialogs {

    val result = folder
            .walkTopDown()
            .filter {
                it.isFile
            }
            .map { readDialog(root, it) }
            .toList()
            .toSList()
            .notEmpty()
    return Dialogs(result)
}

