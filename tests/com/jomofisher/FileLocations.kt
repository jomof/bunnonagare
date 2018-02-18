package com.jomofisher

import java.io.File

private val rootFolder = File("C:\\Users\\jomof\\IdeaProjects\\bunnonagare\\data")
val dialogFolder = File(rootFolder, "dialog")
val sentencesFile = File(rootFolder, "grammar.txt")
val ontologyFile = File(rootFolder, "ontology.txt")
val indexedFragmentsFile = File(rootFolder, "indexed-fragments.txt")
val sentenceDistancesFile = File(rootFolder, "distances.txt")
val classifiersFile = File(rootFolder, "classifiers.txt")

internal val uncontrolledRoot = File("C:\\Users\\jomof\\bunnonagare-data")
val wanikaniApiKey = File(uncontrolledRoot, "wani-kani-key.txt")
val wanikaniVocab = File(uncontrolledRoot, "wani-kani-vocab.txt")
