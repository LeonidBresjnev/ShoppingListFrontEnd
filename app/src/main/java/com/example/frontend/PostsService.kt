package com.example.frontend

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json

interface PostsService {

    suspend fun getPosts(): List<ItemResponse>
    suspend fun createPost(itemRequest: ItemRequest) : List<ItemResponse>
    suspend fun deletePost(id: Int) : Boolean

    companion object {
        fun create(): PostsService {
            return PostsServiceImpl(client = HttpClient(Android) {
                install(Logging) { level=LogLevel.INFO}

                install(ContentNegotiation){
                    json()
                }
            }
            )
        }
    }
}