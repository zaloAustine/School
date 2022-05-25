package com.example.riarafoodapp.data

/**
Created by zaloaustine in 5/24/22.
 */
data class Food(
    val price: String = "",
    val id: String = "",
    val name: String = "",
    val desc: String = "",
    val imageUrl: String = ""
)

data class Cart(val items: String = "", val total: String = "")
data class Order(val foods: List<Food> = listOf(), val total: String = "",val name: String = "",val location: String = "")
