package com.example.frontend.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.sharp.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.frontend.R
import com.example.frontend.ShoppingListViewModel
import com.example.frontend.data.ShoppingListItem
import com.example.frontend.data.butikkerne

/*
data class Butik(val name: String, private val drawable: Int) {
    @Composable
    fun getPainter() : Painter = painterResource(id = drawable)
}

val butikkerne = listOf(
    Butik(name = "Lidl", drawable =  R.drawable.lidl),
    Butik(name= "Føtex", drawable = R.drawable.fotex),
    Butik(name= "Netto", drawable = R.drawable.netto),
    Butik(name= "Super Brugsen", drawable = R.drawable.brugsen),
    Butik(name= "Bilka", drawable = R.drawable.bilka),
    Butik(name= "Rema 1000", drawable = R.drawable.rema1000)

)*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewItem(navController: NavHostController,
myViewModel: ShoppingListViewModel,
            newItem: Boolean = true,
            editItem : ShoppingListItem? = null
) {

    val enheder = listOf("stk.", "liter", "kilo", "gram")

    var item by remember {
        mutableStateOf(value=ShoppingListItem(desc=""))
    }

    if (!newItem && editItem != null) item=editItem.copy()
    var expanded by remember { mutableStateOf(false) }
    var expanded2 by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally) {

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                value = item.desc,
                onValueChange = { value: String -> item = item.copy(desc=value)},
                enabled = true,
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Right,
                    fontSize = 20.sp),
                label = { Text("Indtast produkt") },
                leadingIcon = {
                    Icon(
                        imageVector = if (item.desc.isBlank()) Icons.Sharp.Warning else Icons.Rounded.Check,
                        contentDescription = "Leading Icon"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                value = item.antal.toString(),
                onValueChange = { value: String -> item = item.copy(
                    antal = try {
                        value.toFloat()
                    } catch (e: Exception) {
                        1f
                    }
                )
                                },
                enabled = true,
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Right,
                    fontSize = 20.sp),
                label = { Text("Indtast antal") },
                leadingIcon = {
                    Icon(
                        imageVector = if (item.antal.toString().isBlank()) Icons.Sharp.Warning else Icons.Rounded.Check,
                        contentDescription = "Leading Icon"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )


            ExposedDropdownMenuBox(
                expanded = expanded2,
                onExpandedChange = {expanded2 = !expanded2}) {

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .menuAnchor(),
                    value = item.enhed,
                    onValueChange = {  },
                    enabled = true,
                    readOnly = true,
                    textStyle =  LocalTextStyle.current.copy(
                        textAlign = TextAlign.Right,
                        fontSize = 20.sp),
                    label = { Text("Enhed") },
                    trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded2)},
                    singleLine = true,
                )

                ExposedDropdownMenu(
                    expanded = expanded2,
                    onDismissRequest = { expanded2 = false }) {
                    enheder.forEach {selectedenhed ->
                        DropdownMenuItem(
                            text = { Text(fontSize = 20.sp, text = selectedenhed) },
                            onClick = {
                                item.enhed = selectedenhed
                                expanded2 = false},
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,

                            )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {expanded = !expanded}) {


                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .menuAnchor(),
                    value = item.butik?:"",
                    onValueChange = {  },
                    enabled = true,
                    readOnly = true,
                    textStyle =  LocalTextStyle.current.copy(
                        textAlign = TextAlign.Right,
                        fontSize = 24.sp),
                    label = { Text("Butik") },
                    leadingIcon = {Image(modifier=Modifier.height(height=20.dp),
                        painter= butikkerne.firstOrNull { it.name == item.butik }?.getPainter()?: painterResource(id= R.drawable.ic_launcher_background),
                        contentDescription = "Føtex",
                        contentScale = ContentScale.Fit)},
                    trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)},
singleLine = true,
                    )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }) {
                    butikkerne.forEach {selectedButik ->
                        DropdownMenuItem(
                            leadingIcon = {Image(modifier=Modifier.height(height=24.dp),
                                painter=  selectedButik.getPainter(),
                                contentDescription = "Føtex",
                                contentScale = ContentScale.Fit)},
                            text = { Text(fontSize = 20.sp, text = selectedButik.name) },
                            onClick = {
                                item.butik = selectedButik.name
                                expanded = false},
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,

                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = { navController.navigate(route = CurrentList.route)}) {
                    Text(text = "Tilbage")
                }
                Button(
                    enabled = item.desc.isNotBlank(),
                    onClick = {
                        myViewModel.addToList(newItem, item)
                        navController.navigate(route = CurrentList.route)
                    }) {
                    Text(text = if(newItem) "Tilføj" else "Ok")
                }
            }
        }
    }
}
