package com.example.frontend
import kotlinx.serialization.Serializable

@Serializable
data class ItemResponse(val desc: String,  val id :  Int) {
    override fun toString(): String = "$desc"
}
