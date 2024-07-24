package ru.tfk.mainscreen

import android.util.TypedValue
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.lightColors
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.tfk.database.model.Folder
import ru.tfk.database.model.Note
import ru.tfk.makenote.MakeNoteScreen
import ru.tfk.search.SearchScreen
import ru.tfk.settings.SettingsScreen
import ru.tfk.ui.NoteItemCard

val lightThemeColors =
    lightColors(
        primary = Color(0xFFFFFFFF), // White primary color
        secondary = Color(0xFF6200EE), // Example secondary color
        background = Color.White, // White background color
        surface = Color.White, // White surface color
        // Other colors as needed
    )

@Composable
fun CustomNavHost() {
    val nav = rememberNavController()
    NavHost(nav, startDestination = "mainscreen") {
        composable("mainscreen") { MainScreen(nav) }
        composable("searchscreen") { SearchScreen(nav) }
        composable("settingsscreen") { SettingsScreen(nav) }
        composable(
            "makenotescreen",
            arguments =
                listOf(
                    navArgument("noteId") {
                        nullable = false
                        defaultValue = -1
                        type = NavType.LongType
                    },
                ),
        ) { MakeNoteScreen(nav) }
    }
}

// @Preview(
//    name = "MainScreen",
//    device = "id:pixel_6",
//    showBackground = true,
//    showSystemUi = true,
//    backgroundColor = 0xFFFFFFFF,
// )
@Composable
fun MainScreen(nav: NavHostController) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    MaterialTheme(colors = lightThemeColors) {
        Scaffold(
            topBar = { AppBar(nav) }, // Убираем bottomBar из Scaffold
        ) { innerPadding ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.White),
            ) {
                Column(
                    modifier = Modifier.padding(innerPadding),
                ) {
                    FolderLayout()
                    NotesLayout(nav)
                }
                BottomBar(nav, modifier = Modifier.align(Alignment.BottomCenter)) // Выравнивание по центру снизу
            }
        }
    }
}

@Composable
fun AppBar(nav: NavHostController) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.width(screenWidth)) {
        Text(text = "1Note", fontSize = 40.sp, modifier = Modifier.padding(start = 20.dp))
        Row {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.search48px),
                contentDescription = "SearchButton",
                modifier =
                    Modifier
                        .width(48.dp)
                        .height(48.dp).clickable {
                            nav.navigate("searchscreen")
                        },
                colorFilter = ColorFilter.tint(Color.Black),
            )
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.settings_icon),
                contentDescription = "SettingsButton",
                modifier =
                    Modifier
                        .width(48.dp)
                        .height(48.dp).clickable {
                            nav.navigate("settingsscreen")
                        },
                colorFilter = ColorFilter.tint(Color.Black),
            )
        }
    }
}

@Composable
fun FolderLayout(viewModel: MainScreenViewModel = hiltViewModel()) {
    val selectedChip by viewModel.selectedChip.collectAsState(-1)
    val chips = viewModel.getAllFolders().collectAsState(initial = emptyList())

    Column {
        Text(
            text = "Папки",
            fontSize = 28.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 15.dp),
        )
        LazyRow(
            modifier = Modifier.padding(start = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(chips.value) {
                MainScreenChip(
                    it,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreenChip(folder: Folder) {
    val context = LocalContext.current

    val resources = context.resources
    val displayMetrics = resources.displayMetrics

    val valueInDp =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            8f,
            displayMetrics,
        )

    Chip(
        onClick = { /*TODO*/ },
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(valueInDp),
        colors =
            ChipDefaults.chipColors(
                backgroundColor = Color.White,
            ),
    ) {
        Text(text = folder.name)
    }
}

@Composable
fun NotesLayout(
    nav: NavController,
    viewModel: NoteViewModel = hiltViewModel(),
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val notes = viewModel.getAllNotesFlow().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier.padding(bottom = screenHeight / 17),
    ) {
        Text(
            text = "Заметки",
            fontSize = 28.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 15.dp),
        )
        LazyColumn {
            items(notes.value) {
                NoteItemCard(it, nav)
            }
        }
    }
}

@Composable
fun NoteCard(note: Note) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    Row(
        modifier =
            Modifier.fillMaxWidth().height(1.dp).background(Color.Black),
    ) {}
    Row(
        modifier =
            Modifier.fillMaxWidth().height(screenHeight / 13)
                .padding(start = 20.dp, end = 20.dp),
    ) {
        Column {
            Text(
                text = checkTitleTextNotNull(note.titleNote),
                fontSize = 25.sp,
                color = Color.Black,
            )
            Text(text = checkTextNotNull(note.textNote), fontSize = 20.sp, color = Color.Gray)
        }
    }
}

fun checkTitleTextNotNull(text: String) = if (text.isEmpty()) "Без названия" else text

fun checkTextNotNull(text: String) = if (text.isEmpty()) "Нет дополнительного текста" else text

@Composable
fun BottomBar(
    nav: NavHostController,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier =
            modifier
                .clip(shape = RoundedCornerShape(25.dp, 25.dp, 0.dp, 0.dp))
                .width(screenWidth)
                .height(screenHeight / 17)
                .background(Color.Black)
                .padding(start = 30.dp, end = 30.dp),
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.note_add_icon),
            contentDescription = "NoteAddButton",
            modifier =
                Modifier
                    .width(48.dp)
                    .height(48.dp)
                    .clickable {
                        nav.navigate("makenotescreen")
                    },
            colorFilter = ColorFilter.tint(Color.White),
        )
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.new_folder_icon),
            contentDescription = "NewFolderButton",
            modifier =
                Modifier
                    .width(48.dp)
                    .height(48.dp),
            colorFilter = ColorFilter.tint(Color.White),
        )
    }
}
