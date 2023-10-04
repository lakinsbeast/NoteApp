package code.with.me.testroomandnavigationdrawertest.file

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import code.with.me.testroomandnavigationdrawertest.data.Utils.println
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import javax.inject.Inject

class FilesController @Inject constructor() {

    companion object {
        const val dirFileName = "NotesPhoto"
    }

    /**
     *
     **/
    fun saveToInternalStorage(
        context: Context,
        fileName: String = System.currentTimeMillis().toString()
    ): File? {
        try {
            val dir = makeDirInInternalStorage(context)
            if (dir != null) {
                "dirinsave not nul!!".println()
                return File(
                    dir,
                    fileName
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

    //FileProvider используется, когда нужно предоставить другим приложениям доступ к моим файлам
    fun getUriForFile(
        context: Context,
        file: File,
        providerName: String = code.with.me.testroomandnavigationdrawertest.data.Utils.providerName,
    ): Uri? {
        return FileProvider.getUriForFile(
            context,
            providerName,
            file
        )
    }

    /**
     * создает папку :)
     **/
    fun makeDirInInternalStorage(
        context: Context,
        fileName: String = dirFileName
    ): File? {
        val dir = File(
            context.filesDir,
            "NotesPhotos"
        )

        if (!dir.exists()) {
            if (dir.mkdirs()) {
                "mkDir!".println()
            } else {
                "not mkDir1!".println()
                return null
            }
        } else {
            "dir exists, not creating!".println()
        }

        return dir
    }

}