package code.with.me.testroomandnavigationdrawertest

import android.app.AlertDialog
import android.content.Context

object AlertCreator {
    inline fun createCameraGivePermissionDialog(
        context: Context,
        crossinline isGiven: (Boolean) -> Unit
    ) {
        AlertDialog.Builder(context).setTitle("Необходимо разрешение")
            .setMessage("Для создания фотографий в заметках необходим доступ к камере. Пожалуйста, разрешите приложению использовать камеру, чтобы вы могли добавлять фотографии к своим заметкам. Для этого нажмите 'Разрешить' в окне запроса разрешения. Без доступа к камере функция создания фотографий в заметках будет недоступна ")
            .setPositiveButton("Ок") { _, _ ->
                isGiven.invoke(true)
            }.setNegativeButton("Нет") { _, _ ->
                isGiven.invoke(false)
            }
    }
}