package com.jomofisher

import java.io.File

private val rootFolder = File("C:\\Users\\jomof\\IdeaProjects\\bunnonagare\\data")
val sentencesFile = File(rootFolder, "grammar.txt")
val ontologyFile = File(rootFolder, "ontology.txt")
val indexedFragmentsFile = File(rootFolder, "indexed-fragments.txt")
val sentenceDistancesFile = File(rootFolder, "distances.txt")
val dialogFolder = File(rootFolder, "dialog")