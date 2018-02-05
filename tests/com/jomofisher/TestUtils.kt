package com.jomofisher

import com.google.common.truth.Truth
import kotlin.test.fail

fun assertFailsWith(message: String, function: () -> Unit) {
    try {
        function()
    } catch (e: RuntimeException) {
        Truth.assertThat(e).hasMessageThat().isEqualTo(message)
        return
    }
    fail("Expected failure")
}

fun createFunctions(vararg lines: String): List<Function> {
    return lines.map { parse(it) }.toList()
}

fun createTestIsMap(vararg lines: String): Map<String, String> {
    val functions = lines.map { parse(it) }.toList()
    return createIsMap(functions)
}

fun createTestMakesMap(vararg lines: String): Map<String, List<List<String>>> {
    val functions = lines.map { parse(it) }.toList()
    val isMap = createIsMap(functions)
    return createMakesMap(functions, isMap)
}
