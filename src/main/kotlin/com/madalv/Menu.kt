package com.madalv

class Food(val id: Int,
           val name: String,
           val preparationTime: Int,
           val complexity: Int,
           val cookingApparatus: String)

val menu = listOf(
    Food(1, "Pizza", 20, 2, "oven"),
    Food(2, "Salad", 10, 1, "null"),
    Food(3, "Zeama", 7, 1, "stove"),
    Food(4, "Scallop Sashimi with Meyer Lemon Confit", 32, 3, "null"),
    Food(5, "Island Duck with Mulberry Mustard", 35, 3, "oven"),
    Food(6, "Waffles", 10, 1, "stove"),
    Food(7, "Aubergine", 30, 2, "oven"),
    Food(8, "Lasagna", 30, 2, "oven"),
    Food(9, "Burger", 15, 1, "stove"),
    Food(10, "Gyros", 15, 1, "null"),
    Food(11, "Kebab", 15, 1, "null"),
    Food(12, "Unagi Maki", 20, 2, "null"),
    Food(13, "Tobacco Chicken", 30, 2, "oven")
)