package com.example.frontend


import android.content.ContentValues
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType


class PostsServiceImpl(private val client: HttpClient) : PostsService {

    override suspend fun getPosts(): List<ItemResponse> {
        return try {
            client.get(urlString=HttpRoutes.POSTS).body()
            /*
            client.get {
                url(HttpRoutes.POSTS)

            }*/
        } catch (e: RedirectResponseException) {
            // 3xx
            println("Error: ${e.response.status.description}")
            emptyList()
        } catch (e: ClientRequestException) {
            // 4xx
            println("Error: ${e.response.status.description}")
            emptyList()
        } catch (e: ServerResponseException) {
            // 5xx
            println("Error: ${e.response.status.description}")
            emptyList()
        } catch (e: Exception) {
            // 3xx
            println("Error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun createPost(itemRequest: ItemRequest): List<ItemResponse> {
        return try {
            println("create!")
            Log.d(ContentValues.TAG,"create post")
            client.post(urlString=HttpRoutes.POSTS) {
                contentType(ContentType.Application.Json)
                setBody(itemRequest)
            }.body()
            /*{
                url(HttpRoutes.POSTS)
                contentType(ContentType.Application.Json)
                body = itemRequest }*/
        } catch (e: RedirectResponseException) {
            // 3xx
            println("Error: ${e.response.status.description}")
            emptyList()
        } catch (e: ClientRequestException) {
            // 4xx
            println("Error: ${e.response.status.description}")
            emptyList()
        } catch (e: ServerResponseException) {
            // 5xx
            println("Error: ${e.response.status.description}")
            emptyList()
        } catch (e: Exception) {
            // 3xx
            println("Error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun deletePost(id: Int): Boolean {
        return try {
            return (client.delete(urlString = HttpRoutes.POSTS + "/$id").status == HttpStatusCode.OK)
        } catch (e: RedirectResponseException) {
            // 3xx
            println("Error: ${e.response.status.description}")
            false
        } catch (e: ClientRequestException) {
            // 4xx
            println("Error: ${e.response.status.description}")
            false
        } catch (e: ServerResponseException) {
            // 5xx
            println("Error: ${e.response.status.description}")
            false
        } catch (e: Exception) {
            // 3xx
            println("Error: ${e.message}")
            false
        }
    }
}
