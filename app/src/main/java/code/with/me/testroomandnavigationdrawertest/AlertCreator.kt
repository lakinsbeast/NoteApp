package code.with.me.testroomandnavigationdrawertest

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.widget.TextView

object AlertCreator {
    inline fun createCameraGivePermissionDialog(
        context: Context,
        crossinline isGiven: (Boolean) -> Unit,
    ) {
        AlertDialog.Builder(context).setTitle("Необходимо разрешение")
            .setMessage(
                "Для создания фотографий в заметках необходим доступ к камере. Пожалуйста, разрешите приложению использовать камеру, чтобы вы могли добавлять фотографии к своим заметкам. Для этого нажмите 'Разрешить' в окне запроса разрешения. Без доступа к камере функция создания фотографий в заметках будет недоступна ",
            )
            .setPositiveButton("Ок") { _, _ ->
                isGiven.invoke(true)
            }.setNegativeButton("Нет") { _, _ ->
                isGiven.invoke(false)
            }
    }

    inline fun createAudioGivePermissionDialog(
        context: Context,
        crossinline isGiven: (Boolean) -> Unit,
    ) {
        val dialog =
            AlertDialog.Builder(context).setTitle("Необходимо разрешение")
                .setMessage(
                    "Для записи аудиосообщений необходимо размерение на запись с микрофона. Пожалуйста, разрешите приложению использовать камеру, чтобы вы могли добавлять фотографии к своим заметкам. Для этого нажмите 'Разрешить' в окне запроса разрешения. Без доступа к микрофону функция записи аудиосообщения в заметках будет недоступна ",
                )
                .setPositiveButton("Ок") { _, _ ->
                    isGiven.invoke(true)
                }.setNegativeButton("Нет") { _, _ ->
                    isGiven.invoke(false)
                }
        dialog.show()
    }

    inline fun createAddFolderMenu(
        context: Context,
        crossinline onNewTagClick: () -> Unit,
        crossinline onNewFolderClick: () -> Unit,
    ) {
        Dialog(context).apply {
            setContentView(R.layout.dialog_fragment_folder_add)
            window?.setBackgroundDrawable(null)
            window?.findViewById<TextView>(R.id.newTagTextView)?.apply {
                setOnClickListener {
                    onNewTagClick.invoke()
                    dismiss()
                }
            }
            window?.findViewById<TextView>(R.id.newFolderTextView)?.apply {
                setOnClickListener {
                    onNewFolderClick.invoke()
                    dismiss()
                }
            }
        }.show()
    }

    inline fun createAddNoteMenu(
        context: Context,
        crossinline onNewTagClick: () -> Unit,
        crossinline onNewNoteClick: () -> Unit,
    ) {
        Dialog(context).apply {
            setContentView(R.layout.dialog_fragment_note_add)
            window?.setBackgroundDrawable(null)
            window?.findViewById<TextView>(R.id.newTagTextView)?.apply {
                setOnClickListener {
                    onNewTagClick.invoke()
                    dismiss()
                }
            }
            window?.findViewById<TextView>(R.id.newFolderTextView)?.apply {
                setOnClickListener {
                    onNewNoteClick.invoke()
                    dismiss()
                }
            }
        }.show()
    }
}
