package com.example.frontend.data

import kotlinx.serialization.Serializable


@Serializable
data class Action(val action: String,
                  val content: ShoppingListItem? = null,
    val id: Int? = null) {
    override fun toString(): String = "$action + $content"
}
