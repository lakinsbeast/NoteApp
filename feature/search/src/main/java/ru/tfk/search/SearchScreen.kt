package ru.tfk.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.tfk.ui.NoteItemCard

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val textState = remember { mutableStateOf(TextFieldValue("")) }

    val notes = viewModel.noteList.collectAsState(initial = emptyList())
    viewModel.search("")
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            SearchView(textState) { text ->
                viewModel.search(text)
            }
        }
        Column {
            LazyColumn {
                items(
                    notes.value,
                    key = {
                        it.id
                    },
                ) {
                    NoteItemCard(it, navController)
                }
            }
        }
    }
}

@Composable
fun SearchView(
    textState: MutableState<TextFieldValue>,
    onSearch: (String) -> Unit,
) {
    TextField(
        modifier = Modifier.fillMaxWidth().background(Color.White),
        value = textState.value,
        onValueChange = {
            textState.value = it
            onSearch.invoke(it.text)
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier =
                    Modifier
                        .padding(15.dp)
                        .size(24.dp),
            )
        },
        singleLine = true,
    )
}
