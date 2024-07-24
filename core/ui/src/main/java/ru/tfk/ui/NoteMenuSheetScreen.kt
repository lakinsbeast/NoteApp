package ru.tfk.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteMenuSheetScreen(
    dismiss: () -> Unit,
    menuItemsCallback: (NoteItemsCallback) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    val menuItems =
        mapOf(
            NoteItemsCallback.SHARE to ImageVector.vectorResource(R.drawable.share_48px),
            NoteItemsCallback.MOVE to ImageVector.vectorResource(R.drawable.trending_flat_48px),
            NoteItemsCallback.FAVORITE to ImageVector.vectorResource(R.drawable.bookmark_48px),
            NoteItemsCallback.LOCK to ImageVector.vectorResource(R.drawable.lock_48px),
            NoteItemsCallback.DELETE to ImageVector.vectorResource(R.drawable.delete48px),
        )

    ModalBottomSheet(
        onDismissRequest = {
            dismiss()
            println("dismissed")
        },
        sheetState = sheetState,
    ) {
        LazyRow {
            items(count = menuItems.size, key = {
                menuItems.keys.elementAt(it)
            }) { index ->
                val itemKey = menuItems.keys.elementAt(index)
                NoteMenuSheetItem(
                    itemKey,
                    menuItems.getValue(itemKey),
                ) {
                    menuItemsCallback(it)
                }
            }
        }
    }
}

@Composable
fun NoteMenuSheetItem(
    noteItem: NoteItemsCallback,
    vectorResource: ImageVector,
    noteItemCallback: (NoteItemsCallback) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier =
            Modifier.clickable {
                noteItemCallback(noteItem)
            },
    ) {
        Image(
            imageVector = vectorResource,
            contentDescription = "text",
            modifier =
                Modifier
                    .clip(CircleShape)
                    .size(55.dp)
                    .background(
                        Color.Black,
                    ),
        )
        Text(text = noteItem.toRussian(), fontSize = 11.sp)
        Spacer(modifier = Modifier.height(50.dp))
    }
}

enum class NoteItemsCallback() {
    SHARE,
    MOVE,
    FAVORITE,
    LOCK,
    DELETE,
}

fun NoteItemsCallback.toRussian(): String {
    return when (this) {
        NoteItemsCallback.SHARE -> "Поделиться"
        NoteItemsCallback.MOVE -> "Переместить"
        NoteItemsCallback.FAVORITE -> "Избранное"
        NoteItemsCallback.LOCK -> "Блокировка"
        NoteItemsCallback.DELETE -> "Удалить"
    }
}
