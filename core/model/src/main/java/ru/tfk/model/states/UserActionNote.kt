package ru.tfk.model.states

sealed class NoteState {
    object Loading : NoteState()

    class Result<T>(val data: T) : NoteState()

    object EmptyResult : NoteState()

    class Error<T>(val error: T) : NoteState()
}

sealed class NotesListUserAction {
    class ShareText<T>(val data: T) : NotesListUserAction()
}

sealed class UserActionNote {
    object SavedNoteToDB : UserActionNote()

    object GetImage : UserActionNote()

    object GetCamera : UserActionNote()

    object GetMicrophone : UserActionNote()

    object GetDraw : UserActionNote()
}
