package com.jomofisher

import java.io.File
import java.util.*

val scanner = Scanner(System.`in`)

fun main(args: Array<String>) {
    val all = parseLispy(File(args[0]))
    println("Hello World ${args[0]}\n")
    val next = scanner.nextLine()
    println("Next = $next\n　")
}