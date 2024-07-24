package ru.tfk.makenote

import android.Manifest
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.asFlow
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.tfk.database.model.PhotoModel
import ru.tfk.files.FilesController
import ru.tfk.utils.checkCameraAndWriteExternalStoragePermission
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MakeNoteScreen(
    nav: NavController,
    viewModel: MakeNoteViewModel = hiltViewModel(),
) {
    val titleTextState = remember { mutableStateOf(TextFieldValue("")) }
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    val noteScope = rememberCoroutineScope()
    val listOfImages = remember { mutableStateListOf<PhotoModel>() }

    LaunchedEffect(Unit) {
        noteScope.launch {
            viewModel.noteObs.asFlow().collect {
                listOfImages.addAll(it.listOfImages.toMutableList())
            }
        }
    }
    Column {
        LazyRow {
            items(listOfImages.size) {
                GlideImage(model = listOfImages[it].path, contentDescription = "", modifier = Modifier.size(150.dp))
            }
        }
        TextField(
            value = titleTextState.value,
            onValueChange = {
                titleTextState.value = it
                viewModel.setTitleText(it.toString())
            },
            modifier =
                Modifier
                    .background(Color.White)
                    .fillMaxWidth(),
            label = {
                Text("Название")
            },
        )
        TextField(
            value = textState.value,
            onValueChange = {
                textState.value = it
                viewModel.setText(it.toString())
            },
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.White),
            label = {
                Text("Текст")
            },
        )
    }
    MakeNoteBottomNav(nav)
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun MakeNoteBottomNav(
    nav: NavController,
    viewModel: MakeNoteViewModel = hiltViewModel(),
    filesController: FilesController = FilesController(), // сделать что-то с этим
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val context = LocalContext.current

    val cameraScope = rememberCoroutineScope()
    val requestScope = rememberCoroutineScope { Dispatchers.IO.limitedParallelism(1) }

    val getImageFromCamera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { callback ->
            if (callback) {
                context.let {
                    MediaStore.Images.Media.getBitmap(it.contentResolver, viewModel.cameraUri)
                    viewModel.getImageFromCamera(viewModel.cameraUri.toString())
                    viewModel.saveNote()
                }
            }
        }

    val getImageFromGallery =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { imageUri ->
            imageUri?.let {
                context.contentResolver?.takePersistableUriPermission(
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                )
                viewModel.getImageFromGallery(imageUri)
                viewModel.saveNote()
            }
        }

    // пока не знаю как быть с этой функцией, думаю какую-то часть перенести во вьюмодельку, но нужно будет инжектить filesController
    val makeCameraFileAndOpenCamera = {
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmSS", Locale.getDefault()).format(Date())

        val file =
            filesController.saveToInternalStorage(
                context,
                "image_${Calendar.getInstance().timeInMillis}_$timeStamp.jpg",
            )
        file?.createNewFile()
        file?.let {
            viewModel.cameraUri =
                filesController.getUriForFile(
                    context,
                    file,
                )
        } ?: run {
            throw Exception("Cannot get uri for file")
        }
        cameraScope.launch {
            getImageFromCamera.launch(
                viewModel.cameraUri,
            )
        }
    }
    val request =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            requestScope.launch {
                viewModel.currentPermission.asFlow().collect { currentPermission ->
                    when (currentPermission) {
                        MakeNoteViewModel.Companion.TypeOfPermission.CAMERA -> {
                            makeCameraFileAndOpenCamera()
                        }

                        MakeNoteViewModel.Companion.TypeOfPermission.IMAGE_GALLERY -> {
                            getImageFromGallery.launch(arrayOf("image/*"))
                        }

                        MakeNoteViewModel.Companion.TypeOfPermission.AUDIO -> {
//                    audioRec()
                        }

                        else -> {
                            println("jkghsfhkjsfgd") // ??
                        }
                    }
                }
            }
        }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    top = screenHeight - 100.dp,
                    start = screenWidth / 5,
                    end = screenWidth / 5,
                )
                .clip(shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 10.dp))
                .background(Color.Black),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .padding(10.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier.clickable {
                        getImageFromGallery.launch(arrayOf("image/*"))
                    },
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.image_camera_btn),
                    contentDescription = "",
                )
                Text("Image", color = Color.White)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier.clickable {
                        openCamera(context) {
                            if (!it) {
                                viewModel.setPermission(MakeNoteViewModel.Companion.TypeOfPermission.CAMERA)
                                request.launch(
                                    arrayOf(
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    ),
                                )
                            } else {
                                makeCameraFileAndOpenCamera()
                            }
                        }
                    },
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.camera_btn),
                    contentDescription = "",
                )
                Text("Camera", color = Color.White)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier.clickable {
                    },
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_keyboard_voice_24),
                    contentDescription = "",
                )
                Text("Mic", color = Color.White)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier.clickable {
                    },
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.color_lens_btn),
                    contentDescription = "",
                )
                Text("Draw", color = Color.White)
            }
        }
    }
}

fun openCamera(
    context: Context,
    result: (Boolean) -> Unit,
) {
    if (!context.checkCameraAndWriteExternalStoragePermission()) {
        result(false)
//        request.launch(
//            arrayOf(
//                Manifest.permission.CAMERA,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            ),
//        )
    } else {
        result(true)
//        makeCameraFileAndOpenCamera(it)
    }
}
