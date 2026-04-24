package com.example.memeeditor.meme_editor.presentaion


import androidx.compose.ui.unit.IntSize

sealed interface MemeEditorUiMessage {
    data object GallerySaved : MemeEditorUiMessage
    data object GalleryFailed : MemeEditorUiMessage
}

data class MemeEditorState(
    val templateSize: IntSize = IntSize.Zero,
    val isLeavingWithoutSaving: Boolean = false,
    val textBoxInteractionState: TextBoxInteractionState = TextBoxInteractionState.None,
    val memeTexts: List<MemeText> = emptyList(),
    val hasLeftEditor: Boolean = false,
    val uiMessage: MemeEditorUiMessage? = null,
)


sealed interface TextBoxInteractionState {
    data object None: TextBoxInteractionState
    data class Selected(val textBoxId: String): TextBoxInteractionState
    data class Editing(val textBoxId: String): TextBoxInteractionState
}