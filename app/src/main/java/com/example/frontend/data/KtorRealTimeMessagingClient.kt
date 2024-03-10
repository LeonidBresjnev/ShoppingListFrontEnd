package com.example.frontend.data

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.contentLength
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.isActive
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class KtorRealTimeMessagingClient(
    private val client: HttpClient) : RealTimeMessagingClient {
    private var session: WebSocketSession? = null

    suspend fun test() {
        val res: HttpResponse = client.get("https://google.com")
        res.contentLength()
    }

    override fun getGameStateStream(): Flow<ShoppingListState> {




        return flow {
            session = client.webSocketSession {
                url(urlString = "ws://192.168.8.3:8080/play")
            }
            val gameStates = session!!
                .incoming
                .consumeAsFlow()
                .filterIsInstance<Frame.Text>()
                .mapNotNull {
                    Json.decodeFromString<ShoppingListState>(string=it.readText())
                }
            emitAll(flow = gameStates)
        }
    }

    override suspend fun sendAction(action: Action) {
        //sendSerialized(Action(action="add", content = newitem))
        session?.outgoing?.send(
            //Frame.Text(Json.encodeToString(newitem))
            element=Frame.Text(Json.encodeToString(action))
        )
    }

    override suspend fun close() {
        session?.close(reason= CloseReason(
            code=CloseReason.Codes.GOING_AWAY,
            message="Good Bye"))
        session = null
    }

    override fun isActive(): Flow<Boolean> {
        return flow {
            emit(value=session?.isActive == true)
        }
    }


    override fun isActive_(): Boolean = session?.isActive == true

/*
    suspend fun sendPing() {
        session?.send(Frame.Ping())
    }*/
}