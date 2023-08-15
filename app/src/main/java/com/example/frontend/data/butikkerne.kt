package com.example.frontend.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.frontend.R


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

)