package com.example.firebasegroupapp9

import java.io.Serializable

data class Product(
    var id: String = "",
    var name: String = "",
    var manufacturer: String = "",
    var url: String = "",
    var description: String = "",
    var size: String = "",
    var fullDescription: String = "",
    var price: Double = 0.0
) : Serializable
