package com.jomofisher.sentences

import com.jomofisher.collections.*
import com.jomofisher.function.*
import com.jomofisher.function.Function
import java.io.File

fun readSentences(root: File, file: File): SList<Function>? {
    val annotation = file.absolutePath.substring(root.absolutePath.length + 1)
    return parseLispy(file)
            .keepName("sentence")
            .mapAs<Function>()
            .map { toAnnotatedSentence(it) }
            .annotate("source-file", annotation)
}

private fun toAnnotatedSentence(sentence: Function): Function {
    val parms = sentence.parms
    val (english, _) = parms[0]
    val japanese = parms.drop(1)
    val japaneseNode = createFunction(japanese)
    return japaneseNode.annotate("english", english)
}

fun Node.toSentenceText(): String {
    return StringBuilder()
            .appendFlattened(this)
            .toString()


}
