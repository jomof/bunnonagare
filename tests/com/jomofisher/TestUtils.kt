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
