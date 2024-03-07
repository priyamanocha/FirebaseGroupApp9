package com.example.firebasegroupapp9

import java.io.Serializable

data class Cart(
    var id: String = "",
    var name: String = "",
    var price: Double = 0.0,
    var quantity: Int = 0,
    var url: String = "",
) : Serializable {
}