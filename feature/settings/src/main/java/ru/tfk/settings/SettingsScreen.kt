package ru.tfk.settings

import android.content.res.Resources
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

// @Preview(
//    name = "MainScreen",
//    device = "id:pixel_6",
//    showBackground = true,
//    showSystemUi = true,
//    backgroundColor = 0xFFFFFFFF,
// )
@Composable
fun SettingsScreen(nav: NavHostController) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.settings_icon_anim),
    )

    val settingsTopBarBackgroundAlpha = remember { mutableFloatStateOf(-0.0f) }

    Column(
        Modifier
            .background(Color.White)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(screenHeight / 2),
        ) {
            SettingsTopBar(settingsTopBarBackgroundAlpha, composition)
        }
        SettingsBottomSheet(
            nav,
            {
                nav.popBackStack()
            },
            {
                settingsTopBarBackgroundAlpha.floatValue = abs(it.offset.offsetValue)
            },
        )
    }
}

@Composable
fun SettingsTopBar(
    settingsTopBarBackgroundAlpha: MutableFloatState,
    composition: LottieComposition?,
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .alpha(settingsTopBarBackgroundAlpha.floatValue),
    ) {
        Text(
            "Настройки",
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp),
        )
        LottieAnimation(
            modifier =
                Modifier
//                    .height(screenHeight / 3)
//                    .width(screenWidth)
                    .height((settingsTopBarBackgroundAlpha.floatValue * 300).dp)
                    .width((settingsTopBarBackgroundAlpha.floatValue * 300).dp)
                    .align(Alignment.Center)
                    .padding(top = 20.dp)
                    .graphicsLayer {
//                        this.rotationX = settingsTopBarBackgroundAlpha.floatValue * 300
//                        this.rotationY = settingsTopBarBackgroundAlpha.floatValue * 300
                        this.rotationZ = settingsTopBarBackgroundAlpha.floatValue * 100
                    },
//                    .rotate(settingsTopBarBackgroundAlpha.floatValue * 100),
            composition = composition,
        )
    }
}

fun getStatusBarHeight(): Int {
    val resources = Resources.getSystem()
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    nav: NavHostController,
    dismissed: () -> Unit,
    offset: (BottomSheetOffsetValue) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val sheetState =
        rememberModalBottomSheetState()
    val scope = rememberCoroutineScope { Dispatchers.IO }
    var offset by remember { mutableStateOf(BottomSheetOffsetValue(BottomSheetOffset(0.0f, true))) }

    ModalBottomSheet(
        onDismissRequest = {
            dismissed()
            println("dismissed")
        },
        sheetState = sheetState,
        dragHandle = {
            println("created dragHandle")
            SettingsBottomSheetDragHandle(nav, offset)
        },
        tonalElevation = 0.dp,
        scrimColor = Color.Transparent,
        properties =
            ModalBottomSheetDefaults.properties(
                // если focusable = false, то не срабатывает onDismissRequest
                isFocusable = true,
                shouldDismissOnBackPress = true,
            ),
        containerColor = Color.White,
        modifier = Modifier.statusBarsPadding().offset(y = (-50).dp),
    ) {
        println("SettingsBottomSheet created")
        // аналог bottom sheet callback, костыль жесткий)) попробую что-нибудь придумать и сделать по-другому
        LaunchedEffect(sheetState.isVisible) {
            if (sheetState.isVisible) {
                scope.launch {
                    while (true) {
                        offset =
                            BottomSheetOffsetValue(BottomSheetOffset(abs(sheetState.requireOffset() / 1000)))
                        offset(offset)
                        delay(1)
                    }
                }
            }
        }
        SettingsBottomSheetCard("Использовать папки?") {
        }
        SettingsBottomSheetCard("Настроить папки") {
        }
        SettingsBottomSheetCard("Использовать размытие за окнами?") {
        }
        SettingsBottomSheetCard("Использовать размытие самих окон?") {
        }
        Spacer(modifier = Modifier.weight(1f))
//
    }
}

@Composable
fun SettingsBottomSheetDragHandle(
    nav: NavHostController,
    offset: BottomSheetOffsetValue =
        BottomSheetOffsetValue(
            BottomSheetOffset(0.0f, false),
        ),
) {
    val innerOffset = offset
    println("innerOffset: $innerOffset")

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp)
                .height(50.dp).alpha(innerOffset.offset.offsetValue - 0.2f),
    ) {
        Box(
            modifier =
                Modifier
                    .clip(CircleShape)
                    .height(10.dp)
                    .width(30.dp)
                    .border(2.dp, Color.Black).background(Color.Black).align(Alignment.Center),
        )
    }
    if (!innerOffset.offset.firstLaunch) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp)
                    .height(50.dp).alpha(1.2f - innerOffset.offset.offsetValue),
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_arrow_back_24),
                contentDescription = "",
                modifier =
                    Modifier.height(40.dp).width(40.dp).clickable {
                        if (!innerOffset.offset.firstLaunch && innerOffset.offset.offsetValue < 1.0f) {
                            nav.popBackStack()
                        }
                    },
                colorFilter = ColorFilter.tint(Color.Black),
            )
            Text(
                "Настройки",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
fun SettingsBottomSheetCard(
    text: String,
    onCheckedChange: (Boolean) -> Unit,
) {
    var switchState: Boolean by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.padding(20.dp),
        colors =
            CardColors(
                contentColor = Color.White,
                containerColor = Color.White,
                disabledContentColor = Color.White,
                disabledContainerColor = Color.White,
            ),
        border = BorderStroke(2.dp, Color.Black),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(70.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text, fontSize = 20.sp, modifier = Modifier.align(Alignment.CenterVertically))
            Switch(checked = switchState, onCheckedChange = {
                switchState = it
                onCheckedChange(it)
            }, modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}

// сделать что-то с этим
@JvmInline
@Immutable
value class BottomSheetOffsetValue(val offset: BottomSheetOffset)

data class BottomSheetOffset(val offsetValue: Float, val firstLaunch: Boolean = false)
