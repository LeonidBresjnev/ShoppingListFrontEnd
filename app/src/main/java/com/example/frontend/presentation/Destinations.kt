package com.example.frontend.presentation

interface Destinations {
    val route: String
}

object CurrentList : Destinations {
    override val route = "CurrentList"
}

object NewItem : Destinations {
    override val route = "NewItem"
}