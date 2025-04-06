package com.hotelka.knitlyWants.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import com.hotelka.knitlyWants.toColor
import com.hotelka.knitlyWants.toHex

@Preview
@Composable
fun ColorPicker(color: Color = Color.White, onColorChanged: (Color) -> Unit = {}) {
    var color by remember { mutableStateOf(color) }
    var hex by remember { mutableStateOf(color.toHex()) }

    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(end = 10.dp)
    ) {
        ClassicColorPicker(modifier = Modifier
            .width(200.dp)
            .height(180.dp),
            color = HsvColor.from(color),
            showAlphaBar = false,
            onColorChanged = { hsvColor ->
                color = hsvColor.toColor()
                hex = color.toHex()
                onColorChanged(hsvColor.toColor())
            })

        Column {
            Box(Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(color)) {

            }
            TextField(
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                value = hex,
                onValueChange = {
                    hex = it
                    it.toColor()?.let { newColor ->
                        val envelope = ColorEnvelope(
                            color = newColor,
                            hexCode = newColor.toHex(),
                            true
                        )
                        color = envelope.color
                        onColorChanged(newColor)
                    }
                },
                colors = TextFieldDefaults.colors(
                    unfocusedTextColor = textColor,
                    focusedTextColor = textColor,
                    focusedContainerColor = white,
                    unfocusedContainerColor = white,
                    unfocusedLabelColor = textColor,
                    focusedLabelColor = textColor,
                ),
                label = { Text("hex#") }
            )
        }
    }
}

