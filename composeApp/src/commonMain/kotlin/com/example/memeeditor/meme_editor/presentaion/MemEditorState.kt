package com.example.memeeditor.meme_editor.presentaion


import androidx.compose.ui.unit.IntSize

sealed interface MemeEditorUiMessage {
    data object GallerySaved : MemeEditorUiMessage
    data object GalleryFailed : MemeEditorUiMessage
    data object GalleryPermissionDenied : MemeEditorUiMessage
    data object ExportFailed : MemeEditorUiMessage
    data object ExportNotReady : MemeEditorUiMessage
    data object ShareReady : MemeEditorUiMessage
    data object ImageLoadFailed : MemeEditorUiMessage
    data object TextDeleted : MemeEditorUiMessage
}

data class MemeEditorState(
    val templateSize: IntSize = IntSize.Zero,
    val isLeavingWithoutSaving: Boolean = false,
    val isOfferingReturnHome: Boolean = false,
    val isExporting: Boolean = false,
    /** True when texts/transforms differ from last successful export/save. */
    val isDirty: Boolean = false,
    val textBoxInteractionState: TextBoxInteractionState = TextBoxInteractionState.None,
    val memeTexts: List<MemeText> = emptyList(),
    val deletedTextForUndo: MemeText? = null,
    val hasLeftEditor: Boolean = false,
    val uiMessage: MemeEditorUiMessage? = null,
)


sealed interface TextBoxInteractionState {
    data object None: TextBoxInteractionState
    data class Selected(val textBoxId: String): TextBoxInteractionState
    data class Editing(val textBoxId: String): TextBoxInteractionState
}
