package com.jomofisher

fun mustBeLiteral(function: Function) {
    if (!function.parms.isEmpty()) {
        throw RuntimeException("$function isn't literal")
    }
}

fun mustHaveAtLeastNParameters(function: Function, n: Int) {
    if (function.parms.size < n) {
        throw RuntimeException("expected $function to have at least $n parameters")
    }
}

fun mustHaveAllLiteralParameters(function: Function) {
    function.parms
            .filterNot { it.parms.isEmpty() }
            .forEach {
                throw RuntimeException(
                        "expected $function to have all literal parameters, but '$it' isn't literal")
            }
}
