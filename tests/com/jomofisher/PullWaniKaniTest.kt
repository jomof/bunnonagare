package com.jomofisher

import com.google.common.truth.Truth.assertThat
import com.jomofisher.collections.slistOf
import com.jomofisher.collections.toSList
import com.jomofisher.function.Node
import com.jomofisher.function.createNode
import com.jomofisher.function.writeToFile
import com.wanikani.api.WaniKaniClient
import com.wanikani.api.model.Vocabulary
import org.junit.Test

class PullWaniKaniTest {

    fun getWaniKaniApiKey(): String {
        println("$wanikaniApiKey")
        return wanikaniApiKey.readLines().first()
    }

    @Test
    fun createKeyFolderIfNecessary() {
        uncontrolledRoot.mkdirs()
    }

    @Test
    fun printUserInformation() {
        val client = WaniKaniClient(getWaniKaniApiKey())
        println("${client.userInformation}")
    }

    @Test
    fun studyQueue() {
        val client = WaniKaniClient(getWaniKaniApiKey())
        println("${client.studyQueue}")
    }

    @Test
    fun getLevelProgression() {
        val client = WaniKaniClient(getWaniKaniApiKey())
        println("${client.levelProgression}")
    }

    fun createNode(vocab: Vocabulary): Node {
        assertThat(vocab.character).doesNotContain(",")
        assertThat(vocab.kana).doesNotContain("\"")
        assertThat(vocab.character).doesNotContain("\"")
        assertThat(vocab.meaning).doesNotContain("\"")
        val kana = createNode("",
                vocab.kana
                        .split(",")
                        .map { createNode(it) }
                        .toSList())
        val meaning = createNode("\"${vocab.meaning}\"")
        val character = createNode(vocab.character)
        val level = createNode(vocab.level.toString())

        return createNode("vocab", slistOf(kana, character, meaning, level))
    }

    @Test
    fun getVocabulary() {
        if (wanikaniVocab.isFile) {
            return
        }
        val client = WaniKaniClient(getWaniKaniApiKey())
        val vocab =
                client.getVocabulary()
                        .map { createNode(it) }
                        .toSList()
        vocab.writeToFile(wanikaniVocab)
    }

    @Test
    fun loadWaniKaniData() {
        val waniKani = readWaniKaniVocab(wanikaniVocab)
        println(waniKani)
    }
}
