package com.example.memeeditor.meme_editor.presentaion

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

sealed interface MemeEditorAction {
    data object OnGoBackClick : MemeEditorAction
    data object OnConfirmLeaveWithoutSaving : MemeEditorAction
    data object OnDismissLeaveWithoutSaving : MemeEditorAction
    data object OnConfirmReturnHome : MemeEditorAction
    data object OnDismissReturnHome : MemeEditorAction

    data class OnSaveMemeClick(val backgroundImageBytes: ByteArray) : MemeEditorAction
    data class OnSaveToGalleryClick(val backgroundImageBytes: ByteArray) : MemeEditorAction
    data object OnExportNotReady : MemeEditorAction
    data object OnTapOutsideSelectedText : MemeEditorAction

    data object OnAddTextClick : MemeEditorAction
    data class OnSelectMemeText(val id: String) : MemeEditorAction
    data class OnEditMemeText(val id: String) : MemeEditorAction
    data object OnEditSelectedTextClick : MemeEditorAction
    data class OnMemeTextChange(val id: String, val text: String) : MemeEditorAction
    data class OnMemeTextColorChange(val id: String, val colorArgb: Long) : MemeEditorAction
    data class OnDeleteMemeTextClick(val id: String) : MemeEditorAction
    data object OnUndoDeleteText : MemeEditorAction
    data object OnClearDeletedTextUndo : MemeEditorAction

    data class OnMemeTextTransformChange(
        val id: String,
        val offset: Offset,
        val rotation: Float,
        val scale: Float,
    ) : MemeEditorAction

    data class OnContainerSizeChange(val size: IntSize) : MemeEditorAction

    data object OnUiMessageConsumed : MemeEditorAction
}
