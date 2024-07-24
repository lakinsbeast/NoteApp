package ru.tfk.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameFolderSheetScreen() {
    val sheetState = rememberModalBottomSheetState()
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    val closeSheetScope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = {
            println("dismissed")
        },
        sheetState = sheetState,
    ) {
        Column(Modifier.fillMaxWidth()) {
            Text(text = "Изменить название папки")
            TextField(
                modifier =
                    Modifier.fillMaxWidth().height(250.dp).background(Color.White).clip(
                        RoundedCornerShape(15.dp),
                    ).border(1.dp, Color.Black, RoundedCornerShape(15.dp)),
                value = textState.value,
                onValueChange = {
                    textState.value = it
                },
                label = {
                    Text("Название")
                },
            )
        }
        Button(
            onClick = {
                closeSheetScope.launch {
                    sheetState.hide()
                }
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.border(1.dp, Color.Black, RoundedCornerShape(15.dp)),
        ) {
//
        }
    }
}
