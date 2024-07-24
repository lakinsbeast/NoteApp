package ru.tfk.files

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class FilesController {
    companion object {
        const val dirFileName = "NotesPhoto"
    }

    /**
     *
     **/
    fun saveToInternalStorage(
        context: Context,
        fileName: String = System.currentTimeMillis().toString(),
    ): File? {
        try {
            val dir = makeDirInInternalStorage(context)
            if (dir != null) {
                println("dirinsave not nul!!")
                return File(
                    dir,
                    fileName,
                ).apply {
                    if (!parentFile?.exists()!!) {
                        parentFile?.mkdirs()
                    }
                    if (!exists()) {
                        createNewFile()
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
        return null
    }

    // FileProvider используется, когда нужно предоставить другим приложениям доступ к моим файлам
    fun getUriForFile(
        context: Context,
        file: File,
        providerName: String = "code.with.me.testroomandnavigationdrawertest.ui.MainActivity.provider",
    ): Uri {
        return FileProvider.getUriForFile(
            context,
            providerName,
            file,
        )
    }

    /**
     * создает папку :)
     **/
    fun makeDirInInternalStorage(
        context: Context,
        fileName: String = dirFileName,
    ): File? {
        val dir =
            File(
                context.filesDir,
                "NotesPhotos",
            )

        if (!dir.exists()) {
            if (dir.mkdirs()) {
                println("mkDir!")
            } else {
                println("not mkDir1!")
                return null
            }
        } else {
            println("dir exists, not create!")
        }

        return dir
    }
}
