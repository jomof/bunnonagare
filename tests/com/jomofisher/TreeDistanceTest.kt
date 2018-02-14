package com.jomofisher

class TreeEditDistance {

//    @Test
//    fun identity() {
//        val result = parseLispy("a(b,c)")
//        val distance = distance(result, result)
//        assertThat(distance).isEqualTo(0)
//    }
//
//    @Test
//    fun label() {
//        val distance = distance(parseLispy("a"), parseLispy("b"))
//        assertThat(distance).isEqualTo(1)
//    }
//
//    @Test
//    fun repro() {
//        val x = parseLispy("((((私,は),(((有名,な),レストラン),で),(食事,を))),(し,ました))")
//        val y = parseLispy("(((((こ,の),いす),は),(古,くない)),です)")
//        val distance = distance(x, y)
//    }
//
//    @Test
//    fun grammar() {
//        val all =
//                parseLispy(File("C:\\Users\\jomof\\IdeaProjects\\bunnonagare\\data\\grammar.txt"))
//                        .filterIsInstance<Function>()
//                        .filter { it.name == "sentence" }
//                        .map { Function("", it.parms.drop(1)) }
//                        .toTypedArray()
//        val from = all
//                .mapIndexed { i, a ->
//                    all
//                            .filterIndexed { j, _ -> i > j }
//                            .map { b -> distance(a, b) }
//                            .toTypedArray()
//                }.toTypedArray()
//        from.mapIndexed { i, to ->
//            to.forEachIndexed { j, distance ->
//                println("${all[i]} -> ${all[j]} = $distance")
//            }
//        }
//    }
}