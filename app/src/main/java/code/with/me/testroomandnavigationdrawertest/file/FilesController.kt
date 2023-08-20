package code.with.me.testroomandnavigationdrawertest.file

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
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
                return File(
                    dir,
                    fileName
                ).apply {
                    if (!parentFile?.exists()!!) {
                        parentFile?.mkdirs()
                    }
                    if (!exists()) {
                        mkdirs()
                    }
                    createNewFile()
                }
            }
        } catch (e: Exception) {
            throw e
        }
        return null
    }

    fun getUriForFile(
        context: Context,
        file: File,
        providerName: String = code.with.me.testroomandnavigationdrawertest.Utils.providerName,
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
    private fun makeDirInInternalStorage(
        context: Context,
        fileName: String = dirFileName
    ): File? {
        val dir = File(
            context.filesDir,
            "NotesPhotos"
        )
        return try {
            if (dir.mkdir()) {
                dir
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }

    }
}