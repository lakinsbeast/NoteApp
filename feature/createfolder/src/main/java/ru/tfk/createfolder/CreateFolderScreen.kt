package ru.tfk.createfolder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ChipDefaults.filterChipColors
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
@Preview(device = "id:pixel_6a", showBackground = true)
fun CreateFolderScreen(
//    viewModel: CreateFolderViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val folderNameTextState = remember { mutableStateOf(TextFieldValue("")) }
    val checkedSwitchState = remember { mutableStateOf(false) }
    Dialog(onDismissRequest = {
    }) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .clip(shape = RoundedCornerShape(16.dp)),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text("Создать папку", color = Color.White, fontSize = 25.sp)
                TextField(
                    colors =
                        textFieldColors(
                            cursorColor = Color.White,
                            unfocusedLabelColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedIndicatorColor = Color.White,
                            focusedIndicatorColor = Color.White,
                        ),
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    value = folderNameTextState.value,
                    onValueChange = {
                        folderNameTextState.value = it
                    },
                    label = {
                        Text("Введите название папки")
                    },
                )
                Text(modifier = Modifier.padding(top = 20.dp), text = "Готовые названия для папки", color = Color.White, fontSize = 20.sp)
                // TODO chipgroup
                GenerateRandomChip()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                ) {
                    Text("Избранная папка", color = Color.White, fontSize = 20.sp)
                    Switch(checked = checkedSwitchState.value, onCheckedChange = {
                        checkedSwitchState.value = it
                    })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Отмена", color = Color.White)
                    }
                    Button(onClick = { }) {
                        Text(text = "Создать", color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GenerateRandomChip(
    viewModel: CreateFolderViewModel = hiltViewModel()
) {
    var selectedFirstChip by remember { mutableStateOf(false) }
    var selectedSecondChip by remember { mutableStateOf(false) }
    var selectedThirdChip by remember { mutableStateOf(false) }
    var selectedFourthChip by remember { mutableStateOf(false) }

    LazyRow {
        items(4) { index ->
            FilterChip(
                selected =
                    when (index) {
                        0 -> {
                            selectedFirstChip
                        }
                        1 -> {
                            selectedSecondChip
                        }
                        2 -> {
                            selectedThirdChip
                        }
                        3 -> {
                            selectedFourthChip
                        }

                        else -> {
                            false
                        }
                    },
                onClick = {
                    when (index) {
                        0 -> {
                            selectedFirstChip = !selectedFirstChip
                            selectedSecondChip = false
                            selectedThirdChip = false
                            selectedFourthChip = false
                        }
                        1 -> {
                            selectedSecondChip = !selectedSecondChip
                            selectedFirstChip = false
                            selectedThirdChip = false
                            selectedFourthChip = false
                        }
                        2 -> {
                            selectedThirdChip = !selectedThirdChip
                            selectedSecondChip = false
                            selectedFirstChip = false
                            selectedFourthChip = false
                        }
                        3 -> {
                            selectedFourthChip = !selectedFourthChip
                            selectedSecondChip = false
                            selectedThirdChip = false
                            selectedFirstChip = false
                        }
                    }
                },
                modifier =
                when (index) {
                    0 -> {
                        Modifier.padding(end = 5.dp)
                    }
                    3 -> {
                        Modifier.padding(start = 5.dp)
                    }
                    else -> {
                        Modifier.padding(horizontal = 5.dp)
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors =
                    filterChipColors(
                        contentColor = Color.White,
                        selectedContentColor = Color.Black,
                        backgroundColor = Color.DarkGray,
                        selectedBackgroundColor = Color.White,
                    ),
            ) {
                Text(folderNames[folderNames.indices.random()])
            }
        }
    }
}

/** заготовленные имена папок, на рандом достаются 4 штуки */
val folderNames =
    mutableListOf(
        "🏞️ Путешествия",
        "🍽️ Еда и рецепты",
        "🎥 Развлечения",
        "💡 Идеи и планы",
        "🚗 Транспорт",
        "🎓 Образование",
        "🎂 События и праздники",
        "💰 Финансы",
        "📅 Планирование",
        "🎨 Творчество",
        "📰 Новости",
        "🏋️ Фитнес и здоровье",
        "🏡 Дом и быт",
        "🌍 Планы на путешествия",
        "📚 Книги и чтение",
        "🎯 Цели и достижения",
        "🎵 Музыка",
        "💼 Работа и задачи",
        "🌆 Городская жизнь",
        "🌱 Хобби и увлечения",
        "💖 Личное",
        "👨‍👩‍👧‍👦 Семья и друзья",
        "🐶 Питомцы",
        "🛠️ DIY и ремонт",
        "🛍️ Покупки и шопинг",
        "💻 Технологии",
        "🩺 Медицина и здоровье",
        "🏛️ Культура и искусство",
        "🌳 Природа и экология",
        "🚀 Наука и космос",
        "🙏 Благотворительность",
        "🎧 Подкасты и аудиокниги",
        "🎬 Кино и сериалы",
        "🎮 Игры",
        "🎤 Музыкальные инструменты",
        "✍️ Письмо и журналистика",
        "📸 Фотография",
        "💃🕺 Танцы",
        "🤸‍♀️ Спорт",
        "🧘‍♀️ Медитация и йога",
    )
