package com.example.frontend

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.frontend.presentation.sign_in.GoogleAuthUiClient
import com.example.frontend.presentation.signin.SignInScreen
import com.example.frontend.presentation.signin.SignInViewModel
import com.example.frontend.ui.theme.FrontEndTheme
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class MainActivity : ComponentActivity() {
    private val service = RealTimeMessagingClient.create()

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    @OptIn(ExperimentalMaterial3Api::class)
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
                //val isactivestate by viewModel.isactivestate.collectAsState()

                var isactive by remember { mutableStateOf(false) }


                val isConnecting by viewModel.isConnecting.collectAsState()
                val showConnectionError by viewModel.showConnectionError.collectAsState()


                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text(text = "Shopping list") },
                            navigationIcon = {
                                IconButton(onClick = {navHostController.popBackStack()}) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Go back"
                                    )
                                }
                            }
                        )
                    },
                ) {

                    if (showConnectionError) {
                        Box(

                            modifier = Modifier.padding(it).fillMaxSize(),
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
                    } else {
                        Column(modifier = Modifier.padding(it).fillMaxSize()) {
                            LazyColumn {
                                items(items = state.connectedPlayers, key = { item -> item }) {str->
                                    Text(text = str)
                                }
                            }
                            Button(onClick = { isactive = service.isActive_() }) {
                                Text("status er $isactive")
                            }
                            Button(onClick = {
                                runBlocking {
                                    service.close()
                                }
                            }) {
                                Text(text = "Afbryd")
                            }


                            //val posts = viewmodel.posts.observeAsState()
                            NavHost(
                                modifier = Modifier.padding(it),
                                navController = navHostController,
                                startDestination = "sign_in"
                            ) {
                                composable(route = "sign_in") {
                                    val viewModel = viewModel<SignInViewModel>()
                                    val state by viewModel.state.collectAsStateWithLifecycle()

                                    LaunchedEffect(key1 = Unit) {
                                        if (googleAuthUiClient.getSignedInUser() != null) {
                                            navHostController.navigate(route = CurrentList.route)
                                        }
                                    }

                                    val launcher = rememberLauncherForActivityResult(
                                        contract = ActivityResultContracts.StartIntentSenderForResult(),
                                        onResult = { result ->
                                            if (result.resultCode == RESULT_OK) {
                                                lifecycleScope.launch {
                                                    val signInResult =
                                                        googleAuthUiClient.signInWithIntent(
                                                            intent = result.data ?: return@launch
                                                        )
                                                    viewModel.onSignInResult(signInResult)
                                                }
                                            }
                                        }
                                    )

                                    LaunchedEffect(key1 = state.isSignInSuccessful) {
                                        if (state.isSignInSuccessful) {
                                            Toast.makeText(
                                                applicationContext,
                                                "Sign in successful",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            navHostController.navigate(route = CurrentList.route)
                                            viewModel.resetState()
                                        }
                                    }

                                    SignInScreen(
                                        state = state,
                                        onSignInClick = {
                                            lifecycleScope.launch {
                                                val signInIntentSender = googleAuthUiClient.signIn()
                                                launcher.launch(
                                                    IntentSenderRequest.Builder(
                                                        signInIntentSender ?: return@launch
                                                    ).build()
                                                )
                                            }
                                        }
                                    )
                                }

                                composable(route = CurrentList.route) {
                                    Frontpage(
                                        posts = state.currentList,
                                        navController = navHostController,
                                        myViewModel = viewModel,
                                        onSignOut = {
                                            lifecycleScope.launch {
                                                googleAuthUiClient.signOut()
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Signed out",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                navHostController.popBackStack()
                                            }
                                        }
                                    ) { a: ShoppingListItem -> edititem = a.copy() }
                                }

                                composable(
                                    route = NewItem.route + "/{newitem}",
                                    arguments = listOf(
                                        navArgument(name = "newitem") { type = NavType.BoolType }
                                    )) { entry->

                                    val newitem = entry.arguments?.getBoolean("newitem") ?: true
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
    }
}


