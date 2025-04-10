package com.hotelka.knitlyWants.Data

data class Tutorials(
    val All: MutableMap<String, Blog>? = LinkedHashMap(),
    val Crocheting: MutableMap<String, Blog>? = LinkedHashMap(),
    val Knitting: MutableMap<String, Blog>? = LinkedHashMap()
)