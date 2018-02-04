package com.jomofisher

fun mustBeLiteral(function: Function) {
    if (!function.parms.isEmpty()) {
        throw RuntimeException("$function isn't literal")
    }
}