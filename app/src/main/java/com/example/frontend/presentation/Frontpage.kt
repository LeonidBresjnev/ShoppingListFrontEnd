package com.example.frontend.presentation

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun Frontpage(posts: List<ShoppingListItem>,
              navController: NavHostController,
              myViewModel: ShoppingListViewModel,
              edititem: (ShoppingListItem) -> Unit)
 {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(modifier = Modifier.padding(10.dp)) {
            Text(fontSize = 30.sp,
                text="Shopping List"
            )

            LazyColumn {
                items(items = posts, key = { item -> item.id!! }) {

                    Row(modifier = Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically) {
                        Row(modifier=Modifier.weight(4f),verticalAlignment = Alignment.CenterVertically) {
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
                            modifier = Modifier.weight(1f)
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

            OutlinedButton(onClick = {
                //
                // Log.d(ContentValues.TAG,"$retrieve")
                navController.navigate(route = NewItem.route + "/true")}) {
                Text(text= stringResource(R.string.tilfoej))
            }
        }
    }
}