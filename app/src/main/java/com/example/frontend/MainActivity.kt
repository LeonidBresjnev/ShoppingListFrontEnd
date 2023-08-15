package com.example.frontend

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.frontend.data.RealTimeMessagingClient
import com.example.frontend.data.ShoppingListItem
import com.example.frontend.presentation.CurrentList
import com.example.frontend.presentation.Frontpage
import com.example.frontend.presentation.NewItem
import com.example.frontend.ui.theme.FrontEndTheme
import kotlinx.coroutines.runBlocking


class MainActivity : ComponentActivity() {
    private val service = RealTimeMessagingClient.create()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var edititem: ShoppingListItem? = null
        setContent {
/*   val posts = produceState<List<ItemResponse>>(initialValue = emptyList()) {
                recompose = !recompose
                Log.d(ContentValues.TAG,"shoppinglist get")
                value = service.getPosts()
            }
*/
            FrontEndTheme {
                val navHostController = rememberNavController()
                //val viewmodel = viewModel<MyViewModel>()

                val viewModel = ShoppingListViewModel(service)
                val state by viewModel.state.collectAsState()
                val isactivestate by viewModel.isactivestate.collectAsState()

                var isactive by remember { mutableStateOf(false) }


                val isConnecting by viewModel.isConnecting.collectAsState()
                val showConnectionError by viewModel.showConnectionError.collectAsState()
                if (showConnectionError) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Couldn't connect to the server",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    if (isConnecting) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    return@FrontEndTheme
                }
                Column {
                    LazyColumn {
                        items(items = state.connectedPlayers, key = { item -> item }) {
                            Text(text=it)
                        }
                        }
                    Button(onClick = {isactive = service.isActive_()}) {
                        Text("status er $isactive")
                    }
                    Button(onClick = {
                        runBlocking {
                            service.close()
                        }
                        }) {
                        Text(text="Afbryd")
                    }


                    //val posts = viewmodel.posts.observeAsState()
                    NavHost(
                        navController = navHostController,
                        startDestination = CurrentList.route
                    ) {
                        composable(route = CurrentList.route) {
                            Frontpage(
                                posts = state.currentList,
                                navController = navHostController,
                                myViewModel = viewModel
                            ) { a: ShoppingListItem -> edititem = a.copy() }
                        }
                        composable(
                            route = NewItem.route + "/{newitem}",
                            arguments = listOf(
                                navArgument(name = "newitem") { type = NavType.BoolType }
                            )) {

                            val newitem = it.arguments?.getBoolean("newitem") ?: true
                            Log.d(ContentValues.TAG, "$newitem")
                            NewItem(
                                navController = navHostController,
                                myViewModel = viewModel,
                                newItem = newitem,
                                editItem = edititem
                            )
                        }
                    }
                }
            }
        }
    }
}


