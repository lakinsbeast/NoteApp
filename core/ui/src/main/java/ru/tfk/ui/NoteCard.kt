package ru.tfk.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ru.tfk.database.model.NoteInterface
import ru.tfk.utils.checkTextNotNull
import ru.tfk.utils.checkTitleTextNotNull

@Composable
fun <T> NoteItemCard(
    note: T,
    nav: NavController,
) where T : NoteInterface {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    var sheetMenuState = remember { mutableStateOf(false) }

    Row(
        modifier =
            Modifier
                .width(screenWidth)
                .height(1.dp)
                .background(Color.White),
    ) {}
    Row(
        modifier =
            Modifier
                .width(screenWidth)
                .height(screenHeight / 13)
                .padding(start = 20.dp, end = 20.dp).clickable {
                    nav.navigate("makenotescreen?noteId=${note.id}")
                },
    ) {
        Column {
            Text(
                text = checkTitleTextNotNull(note.titleNote),
                fontSize = 25.sp,
                color = Color.Black,
            )
            Text(text = checkTextNotNull(note.textNote), fontSize = 20.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.weight(1f))
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.three_dots_vertical),
            contentDescription = "",
            Modifier.clickable {
                sheetMenuState.value = true
            },
        )
    }

    if (sheetMenuState.value) {
        NoteMenuSheetScreen(
            dismiss = {
                sheetMenuState.value = false
            },
            menuItemsCallback = {
                println("callback: ${it.name}")
            },
        )
    }
}
