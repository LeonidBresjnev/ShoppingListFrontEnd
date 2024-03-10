package com.example.frontend.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.frontend.R
import com.example.frontend.ShoppingListViewModel
import com.example.frontend.data.ShoppingListItem
import com.example.frontend.data.butikkerne
import kotlinx.coroutines.delay

@Composable
fun Frontpage(posts: List<ShoppingListItem>,
              navController: NavHostController,
              myViewModel: ShoppingListViewModel,
              onSignOut: () -> Unit,
              edititem: (ShoppingListItem) -> Unit)
{
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {


        Column(modifier = Modifier) {

            Button(onClick = onSignOut) {
                Text(text = "Sign out")
            }

            Text(
                fontSize = 30.sp,
                text = "Shopping List"
            )

            LazyColumn {
                items(
                    items = posts,
                    key = { item -> item.id!! }
                ) {it->
                    SwipeToDeleteContainer(
                        item = it,
                        onDelete = {myViewModel.removeFromList(it.id!!)}
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(4f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { myViewModel.removeFromList(it.id!!) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Search icon"
                                    )
                                }
                                IconButton(onClick = {
                                    edititem(it)
                                    navController.navigate(route = NewItem.route + "/false")
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit"
                                    )
                                }
                                Text(text = it.desc, fontSize = 24.sp)
                                Text(text = "${it.antal} ${it.enhed}", fontSize = 24.sp)


                            }
                            Image(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(height = 24.dp),

                                alignment = Alignment.CenterEnd,
                                painter = butikkerne.find { a -> it.butik == a.name }?.getPainter()
                                    ?: painterResource(id = R.drawable.ic_launcher_background),
                                contentDescription = it.butik,
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }

            OutlinedButton(onClick = {
                //
                // Log.d(ContentValues.TAG,"$retrieve")
                navController.navigate(route = NewItem.route + "/true")
            }) {
                Text(text = stringResource(R.string.tilfoej))
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDelete: (T) -> Unit,
    animationDuration: Int = 500,
    content: @Composable (T) -> Unit
) {
    var isRemoved by remember {
        mutableStateOf(false)
    }
    val state = rememberDismissState(
        confirmValueChange = { value ->
            if (value == DismissValue.DismissedToStart) {
                isRemoved = true
                true
            } else {
                false
            }
        }
    )

    LaunchedEffect(key1 = isRemoved) {
        if(isRemoved) {
            delay(animationDuration.toLong())
            onDelete(item)
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismiss(
            state = state,
            background = {
                DeleteBackground(swipeDismissState = state)
            },
            dismissContent = { content(item) },
            directions = setOf(DismissDirection.EndToStart)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteBackground(
    swipeDismissState: DismissState
) {
    val color = if (swipeDismissState.dismissDirection == DismissDirection.EndToStart) {
        Color.Red
    } else Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = Color.White
        )
    }
}