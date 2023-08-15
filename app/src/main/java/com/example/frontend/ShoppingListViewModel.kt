package com.example.frontend

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.Action
import com.example.frontend.data.RealTimeMessagingClient
import com.example.frontend.data.ShoppingListItem
import com.example.frontend.data.ShoppingListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.ConnectException

class ShoppingListViewModel(
    private val client: RealTimeMessagingClient
): ViewModel() {

    private val _isConnecting = MutableStateFlow(false)
    private val _showConnectionError = MutableStateFlow(false)
    private val _isActive = MutableStateFlow(value=false)


    val isConnecting = _isConnecting.asStateFlow()
    val showConnectionError = _showConnectionError.asStateFlow()
    val isActive = _isActive.asStateFlow()



    val state = client
        .getGameStateStream()
        .onStart { _isConnecting.value = true }
        .onEach { _isConnecting.value = false }
        .catch { t -> _showConnectionError.value = t is ConnectException }
        .stateIn(
            scope=viewModelScope,
            started=SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
            initialValue = ShoppingListState())


    val isactivestate : StateFlow<Boolean> = client.isActive().debounce(1000L).stateIn(
        scope=viewModelScope,
        started=SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
        initialValue = false)


    fun addToList(newitem: Boolean, item: ShoppingListItem) {
        viewModelScope.launch {
            client.sendAction(Action(action = if (newitem) "add" else "edit", content = item))
        }
    }



    fun removeFromList(id: Int) {
        Log.d(ContentValues.TAG,"remove $id")

        viewModelScope.launch {
            client.sendAction(Action(action="remove", id =  id))
        }
    }
}