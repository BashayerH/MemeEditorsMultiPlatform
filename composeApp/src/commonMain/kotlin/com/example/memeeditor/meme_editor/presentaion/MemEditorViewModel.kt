
@file:OptIn(ExperimentalUuidApi::class)

package com.example.memeeditor.meme_editor.presentaion

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memeeditor.meme_editor.domain.MemeExporter
import com.example.memeeditor.meme_editor.domain.SaveToStorageStrategy
import com.example.memeeditor.meme_editor.platform.GalleryPermissionDeniedException
import com.example.memeeditor.meme_editor.platform.isGallerySaveSupported
import com.example.memeeditor.meme_editor.platform.saveJpegFileToGallery
import com.example.memeeditor.meme_editor.presentaion.util.PlatformShareSheet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class MemeEditorViewModel(
    private val memeExporter: MemeExporter,
    private val storageStrategy: SaveToStorageStrategy,
    private val shareSheet: PlatformShareSheet
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(MemeEditorState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = MemeEditorState()
        )

    fun onAction(action: MemeEditorAction) {
        when (action) {
            MemeEditorAction.OnAddTextClick ->
                addText()
            MemeEditorAction.OnConfirmLeaveWithoutSaving ->
                confirmLeave()
            MemeEditorAction.OnConfirmReturnHome ->
                confirmReturnHome()
            is MemeEditorAction.OnContainerSizeChange -> updateContainerSize(action.size)
            is MemeEditorAction.OnDeleteMemeTextClick ->
                deleteMemeText(action.id)
            MemeEditorAction.OnDismissLeaveWithoutSaving ->
                dismissConfirmLeaveDialog()
            MemeEditorAction.OnDismissReturnHome ->
                dismissReturnHomeDialog()
            is MemeEditorAction.OnEditMemeText ->
                editMemeText(action.id)
            MemeEditorAction.OnEditSelectedTextClick ->
                editSelectedText()
            MemeEditorAction.OnGoBackClick ->
                attemptToGoBack()
            is MemeEditorAction.OnMemeTextChange ->
                updateMemeText(action.id, action.text)
            is MemeEditorAction.OnMemeTextColorChange ->
                updateMemeTextColor(action.id, action.colorArgb)
            is MemeEditorAction.OnMemeTextTransformChange ->
                transformMemeText(
                id = action.id,
                offset = action.offset,
                rotation = action.rotation,
                scale = action.scale
            )

            is MemeEditorAction.OnSaveMemeClick ->
                saveMeme(action.backgroundImageBytes)
            is MemeEditorAction.OnSaveToGalleryClick ->
                saveMemeToGallery(action.backgroundImageBytes)
            MemeEditorAction.OnExportNotReady ->
                emitNotReady()
            is MemeEditorAction.OnSelectMemeText ->
                selectMemeText(action.id)
            MemeEditorAction.OnTapOutsideSelectedText ->
                unselectMemeText()
            MemeEditorAction.OnUndoDeleteText ->
                undoDeleteText()
            MemeEditorAction.OnClearDeletedTextUndo ->
                clearDeletedTextUndo()
            MemeEditorAction.OnUiMessageConsumed ->
                consumeUiMessage()
        }
    }

    private fun consumeUiMessage() {
        _state.update { it.copy(uiMessage = null) }
    }

    private fun emitNotReady() {
        _state.update { it.copy(uiMessage = MemeEditorUiMessage.ExportNotReady) }
    }

    private fun saveMeme(backgroundImageBytes: ByteArray) {
        if (_state.value.isExporting) return
        viewModelScope.launch {
            _state.update { it.copy(isExporting = true) }
            val result = memeExporter.exportMeme(
                backgroundImageBytes = backgroundImageBytes,
                memeTexts = state.value.memeTexts,
                templateSize = state.value.templateSize,
                saveToStorageStrategy = storageStrategy
            )
            result.fold(
                onSuccess = { path ->
                    withContext(Dispatchers.Main) {
                        shareSheet.shareFile(path)
                    }
                    // Share sheet cancel is not observable on all platforms — do not force return-home.
                    _state.update { s ->
                        s.copy(
                            isExporting = false,
                            isDirty = false,
                            uiMessage = MemeEditorUiMessage.ShareReady,
                        )
                    }
                },
                onFailure = {
                    _state.update { s ->
                        s.copy(
                            isExporting = false,
                            uiMessage = MemeEditorUiMessage.ExportFailed,
                        )
                    }
                },
            )
        }
    }

    private fun saveMemeToGallery(backgroundImageBytes: ByteArray) {
        if (!isGallerySaveSupported()) return
        if (_state.value.isExporting) return
        viewModelScope.launch {
            _state.update { it.copy(isExporting = true) }
            memeExporter
                .exportMeme(
                    backgroundImageBytes = backgroundImageBytes,
                    memeTexts = state.value.memeTexts,
                    templateSize = state.value.templateSize,
                    saveToStorageStrategy = storageStrategy
                )
                .onSuccess { path ->
                    saveJpegFileToGallery(
                        filePath = path,
                        displayName = path.substringAfterLast('/').substringAfterLast('\\')
                    )
                        .onSuccess {
                            _state.update { s ->
                                s.copy(
                                    isExporting = false,
                                    isDirty = false,
                                    uiMessage = MemeEditorUiMessage.GallerySaved,
                                    isOfferingReturnHome = true,
                                )
                            }
                        }
                        .onFailure { error ->
                            _state.update { s ->
                                s.copy(
                                    isExporting = false,
                                    uiMessage = if (error is GalleryPermissionDeniedException) {
                                        MemeEditorUiMessage.GalleryPermissionDenied
                                    } else {
                                        MemeEditorUiMessage.GalleryFailed
                                    },
                                )
                            }
                        }
                }
                .onFailure {
                    _state.update { s ->
                        s.copy(
                            isExporting = false,
                            uiMessage = MemeEditorUiMessage.GalleryFailed,
                        )
                    }
                }
        }
    }

    private fun dismissConfirmLeaveDialog() {
        _state.update { it.copy(
            isLeavingWithoutSaving = false
        ) }
    }

    private fun confirmLeave() {
        _state.update { it.copy(
            isLeavingWithoutSaving = false,
            hasLeftEditor = true
        ) }
    }

    private fun dismissReturnHomeDialog() {
        _state.update { it.copy(isOfferingReturnHome = false) }
    }

    private fun confirmReturnHome() {
        _state.update {
            it.copy(
                isOfferingReturnHome = false,
                hasLeftEditor = true,
            )
        }
    }

    private fun attemptToGoBack() {
        if (!_state.value.isDirty) {
            _state.update { it.copy(hasLeftEditor = true) }
        } else {
            _state.update { it.copy(isLeavingWithoutSaving = true) }
        }
    }

    private fun transformMemeText(
        id: String,
        offset: Offset,
        rotation: Float,
        scale: Float
    ) {
        _state.update {
            val (width, height) = it.templateSize
            it.copy(
                isDirty = true,
                memeTexts = it.memeTexts.map { memeText ->
                    if (memeText.id == id) {
                        memeText.copy(
                            offsetRatioX = offset.x / width,
                            offsetRatioY = offset.y / height,
                            rotation = rotation,
                            scale = scale
                        )
                    } else memeText
                }
            )
        }
    }

    private fun unselectMemeText() {
        _state.update {
            it.copy(
                textBoxInteractionState = TextBoxInteractionState.None
            )
        }
    }

    private fun addText() {
        val id = Uuid.random().toString()

        val memeText = MemeText(
            id = id,
            text = "TAP TO EDIT",
            offsetRatioX = 0.25f,
            offsetRatioY = 0.25f,
            colorArgb = MemeTextColors.DefaultFill,
        )

        _state.update {
            it.copy(
                isDirty = true,
                memeTexts = it.memeTexts + memeText,
                textBoxInteractionState = TextBoxInteractionState.Selected(id)
            )
        }
    }

    private fun deleteMemeText(id: String) {
        _state.update {
            val removed = it.memeTexts.firstOrNull { memeText -> memeText.id == id }
            val clearingSelection = when (val interaction = it.textBoxInteractionState) {
                is TextBoxInteractionState.Selected -> interaction.textBoxId == id
                is TextBoxInteractionState.Editing -> interaction.textBoxId == id
                TextBoxInteractionState.None -> false
            }
            it.copy(
                isDirty = true,
                memeTexts = it.memeTexts.filter { memeText ->
                    memeText.id != id
                },
                textBoxInteractionState = if (clearingSelection) {
                    TextBoxInteractionState.None
                } else {
                    it.textBoxInteractionState
                },
                deletedTextForUndo = removed,
                uiMessage = if (removed != null) MemeEditorUiMessage.TextDeleted else it.uiMessage,
            )
        }
    }

    private fun undoDeleteText() {
        _state.update { state ->
            val deleted = state.deletedTextForUndo ?: return@update state
            state.copy(
                isDirty = true,
                memeTexts = state.memeTexts + deleted,
                deletedTextForUndo = null,
                textBoxInteractionState = TextBoxInteractionState.Selected(deleted.id),
                uiMessage = null,
            )
        }
    }

    private fun clearDeletedTextUndo() {
        _state.update { it.copy(deletedTextForUndo = null) }
    }

    private fun selectMemeText(id: String) {
        _state.update {
            it.copy(
                textBoxInteractionState = TextBoxInteractionState.Selected(id)
            )
        }
    }

    private fun updateMemeText(id: String, text: String) {
        _state.update {
            it.copy(
                isDirty = true,
                memeTexts = it.memeTexts.map { memeText ->
                    if (memeText.id == id) {
                        memeText.copy(text = text)
                    } else memeText
                }
            )
        }
    }

    private fun updateMemeTextColor(id: String, colorArgb: Long) {
        _state.update {
            it.copy(
                isDirty = true,
                memeTexts = it.memeTexts.map { memeText ->
                    if (memeText.id == id) {
                        memeText.copy(colorArgb = colorArgb)
                    } else memeText
                }
            )
        }
    }

    private fun editMemeText(id: String) {
        _state.update {
            it.copy(
                textBoxInteractionState = TextBoxInteractionState.Editing(id)
            )
        }
    }

    private fun editSelectedText() {
        val interaction = _state.value.textBoxInteractionState
        val id = when (interaction) {
            is TextBoxInteractionState.Selected -> interaction.textBoxId
            is TextBoxInteractionState.Editing -> interaction.textBoxId
            TextBoxInteractionState.None -> return
        }
        editMemeText(id)
    }

    private fun updateContainerSize(size: IntSize) {
        _state.update {
            it.copy(
                templateSize = size
            )
        }
    }

}
