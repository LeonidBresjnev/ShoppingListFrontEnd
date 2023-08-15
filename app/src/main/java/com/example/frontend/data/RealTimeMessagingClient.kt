package com.example.frontend.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow

interface RealTimeMessagingClient {
    fun getGameStateStream(): Flow<ShoppingListState>
    suspend fun sendAction(action: Action)
    suspend fun close()
    fun isActive(): Flow<Boolean>
    fun isActive_(): Boolean

    companion object {
        fun create(): RealTimeMessagingClient {
            return KtorRealTimeMessagingClient(client = HttpClient(CIO) {

                install(Logging) { level= LogLevel.INFO}
                install(ContentNegotiation){
                    json()
                }
                install(WebSockets){
                    pingInterval = 5_000
                }
                install(Auth) {

                    basic {
                        credentials {
                            BasicAuthCredentials(username = "Jabbe", password = "Bruno")
                        }
                        realm = "Access to the '/' path"
                    }
                }
            }
            )
        }
    }
}