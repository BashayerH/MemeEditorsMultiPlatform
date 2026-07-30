@file:OptIn(ExperimentalComposeUiApi::class)

package com.example.memeeditor.meme_editor.presentaion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memeeditor.core.presentaion.MemeBackground
import com.example.memeeditor.core.presentaion.MemesTemplate
import com.example.memeeditor.core.theme.MemeCreatorTheme
import com.example.memeeditor.meme_editor.platform.decodeImageBitmap
import com.example.memeeditor.meme_editor.platform.isGallerySaveSupported
import com.example.memeeditor.meme_editor.platform.readFileBytes
import com.example.memeeditor.meme_editor.platform.rememberGallerySavePermission
import com.example.memeeditor.meme_editor.platform.rememberOpenAppSettings
import com.example.memeeditor.meme_editor.presentaion.components.BottomBar
import com.example.memeeditor.meme_editor.presentaion.components.ConfirmationDialog
import com.example.memeeditor.meme_editor.presentaion.components.ConfirmationDialogConfig
import com.example.memeeditor.meme_editor.presentaion.components.DraggableContainer
import com.example.memeeditor.meme_editor.presentaion.components.TextColorPicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import memeeditor.composeapp.generated.resources.Res
import memeeditor.composeapp.generated.resources.back_to_templates
import memeeditor.composeapp.generated.resources.cancel
import memeeditor.composeapp.generated.resources.export_failed
import memeeditor.composeapp.generated.resources.export_not_ready
import memeeditor.composeapp.generated.resources.exporting_meme
import memeeditor.composeapp.generated.resources.gallery_permission_denied
import memeeditor.composeapp.generated.resources.gallery_save_failed
import memeeditor.composeapp.generated.resources.gallery_saved
import memeeditor.composeapp.generated.resources.image_load_failed
import memeeditor.composeapp.generated.resources.leave
import memeeditor.composeapp.generated.resources.leave_editor_message
import memeeditor.composeapp.generated.resources.leave_editor_title
import memeeditor.composeapp.generated.resources.meme_template_01
import memeeditor.composeapp.generated.resources.navigate_back
import memeeditor.composeapp.generated.resources.open_settings
import memeeditor.composeapp.generated.resources.retry_load
import memeeditor.composeapp.generated.resources.return_home_confirm
import memeeditor.composeapp.generated.resources.return_home_message
import memeeditor.composeapp.generated.resources.return_home_stay
import memeeditor.composeapp.generated.resources.return_home_title
import memeeditor.composeapp.generated.resources.share_ready
import memeeditor.composeapp.generated.resources.text_deleted
import memeeditor.composeapp.generated.resources.undo
import org.jetbrains.compose.resources.getDrawableResourceBytes
import org.jetbrains.compose.resources.getSystemResourceEnvironment
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MemeEditorRoot(
    background: MemeBackground,
    onGoBack: () -> Unit,
    viewModel: MemeEditorViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.hasLeftEditor) {
        if (state.hasLeftEditor) {
            onGoBack()
        }
    }

    MemeEditorScreen(
        background = background,
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
fun MemeEditorScreen(
    background: MemeBackground,
    state: MemeEditorState,
    onAction: (MemeEditorAction) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val gallerySavedMessage = stringResource(Res.string.gallery_saved)
    val galleryFailedMessage = stringResource(Res.string.gallery_save_failed)
    val exportFailedMessage = stringResource(Res.string.export_failed)
    val exportNotReadyMessage = stringResource(Res.string.export_not_ready)
    val imageLoadFailedMessage = stringResource(Res.string.image_load_failed)
    val permissionDeniedMessage = stringResource(Res.string.gallery_permission_denied)
    val openSettingsLabel = stringResource(Res.string.open_settings)
    val shareReadyMessage = stringResource(Res.string.share_ready)
    val textDeletedMessage = stringResource(Res.string.text_deleted)
    val undoLabel = stringResource(Res.string.undo)
    val backCd = stringResource(Res.string.navigate_back)
    val exportingCd = stringResource(Res.string.exporting_meme)

    var backgroundBytes by remember(background) { mutableStateOf<ByteArray?>(null) }
    var customBitmap by remember(background) { mutableStateOf<ImageBitmap?>(null) }
    var loading by remember(background) { mutableStateOf(true) }
    var loadFailed by remember(background) { mutableStateOf(false) }
    var loadAttempt by remember(background) { mutableStateOf(0) }
    var pendingGalleryBytes by remember { mutableStateOf<ByteArray?>(null) }

    val openAppSettings = rememberOpenAppSettings()

    val requestGalleryPermission = rememberGallerySavePermission(
        onGranted = {
            pendingGalleryBytes?.let { bytes ->
                onAction(MemeEditorAction.OnSaveToGalleryClick(bytes))
            }
            pendingGalleryBytes = null
        },
        onDenied = {
            pendingGalleryBytes = null
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = permissionDeniedMessage,
                    actionLabel = openSettingsLabel,
                    duration = SnackbarDuration.Long,
                )
                if (result == SnackbarResult.ActionPerformed) {
                    openAppSettings()
                }
            }
        },
    )

    BackHandlerCompat(enabled = true) {
        when {
            state.isOfferingReturnHome -> onAction(MemeEditorAction.OnDismissReturnHome)
            state.isLeavingWithoutSaving -> onAction(MemeEditorAction.OnDismissLeaveWithoutSaving)
            else -> onAction(MemeEditorAction.OnGoBackClick)
        }
    }

    LaunchedEffect(background, loadAttempt) {
        loading = true
        loadFailed = false
        val loadResult = runCatching {
            withContext(Dispatchers.Default) {
                when (background) {
                    is MemeBackground.Template -> {
                        val bytes = getDrawableResourceBytes(
                            environment = getSystemResourceEnvironment(),
                            resource = background.template.drawableResource,
                        )
                        bytes to null
                    }
                    is MemeBackground.CustomImage -> {
                        val bytes = readFileBytes(background.filePath)
                        bytes to decodeImageBitmap(bytes)
                    }
                }
            }
        }
        loadResult.fold(
            onSuccess = { (bytes, bitmap) ->
                backgroundBytes = bytes
                customBitmap = bitmap
                loadFailed = false
            },
            onFailure = {
                backgroundBytes = null
                customBitmap = null
                loadFailed = true
                snackbarHostState.showSnackbar(imageLoadFailedMessage)
            },
        )
        loading = false
    }

    LaunchedEffect(state.uiMessage) {
        when (val msg = state.uiMessage) {
            null -> Unit
            MemeEditorUiMessage.GallerySaved -> {
                snackbarHostState.showSnackbar(gallerySavedMessage)
                onAction(MemeEditorAction.OnUiMessageConsumed)
            }
            MemeEditorUiMessage.GalleryFailed -> {
                snackbarHostState.showSnackbar(galleryFailedMessage)
                onAction(MemeEditorAction.OnUiMessageConsumed)
            }
            MemeEditorUiMessage.GalleryPermissionDenied -> {
                val result = snackbarHostState.showSnackbar(
                    message = permissionDeniedMessage,
                    actionLabel = openSettingsLabel,
                    duration = SnackbarDuration.Long,
                )
                onAction(MemeEditorAction.OnUiMessageConsumed)
                if (result == SnackbarResult.ActionPerformed) {
                    openAppSettings()
                }
            }
            MemeEditorUiMessage.ExportFailed -> {
                snackbarHostState.showSnackbar(exportFailedMessage)
                onAction(MemeEditorAction.OnUiMessageConsumed)
            }
            MemeEditorUiMessage.ExportNotReady -> {
                snackbarHostState.showSnackbar(exportNotReadyMessage)
                onAction(MemeEditorAction.OnUiMessageConsumed)
            }
            MemeEditorUiMessage.ShareReady -> {
                snackbarHostState.showSnackbar(shareReadyMessage)
                onAction(MemeEditorAction.OnUiMessageConsumed)
            }
            MemeEditorUiMessage.ImageLoadFailed -> {
                snackbarHostState.showSnackbar(imageLoadFailedMessage)
                onAction(MemeEditorAction.OnUiMessageConsumed)
            }
            MemeEditorUiMessage.TextDeleted -> {
                val result = snackbarHostState.showSnackbar(
                    message = textDeletedMessage,
                    actionLabel = undoLabel,
                    duration = SnackbarDuration.Short,
                )
                onAction(MemeEditorAction.OnUiMessageConsumed)
                if (result == SnackbarResult.ActionPerformed) {
                    onAction(MemeEditorAction.OnUndoDeleteText)
                } else {
                    onAction(MemeEditorAction.OnClearDeletedTextUndo)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    onAction(MemeEditorAction.OnTapOutsideSelectedText)
                }
            },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            val selectedTextId = when (val interaction = state.textBoxInteractionState) {
                is TextBoxInteractionState.Selected -> interaction.textBoxId
                is TextBoxInteractionState.Editing -> interaction.textBoxId
                TextBoxInteractionState.None -> null
            }
            val selectedText = selectedTextId?.let { id ->
                state.memeTexts.firstOrNull { it.id == id }
            }
            val bytes = backgroundBytes
            val canExport = bytes != null && !loadFailed && !loading
            Column {
                if (selectedText != null) {
                    TextColorPicker(
                        selectedColorArgb = selectedText.colorArgb,
                        onColorSelected = { colorArgb ->
                            onAction(
                                MemeEditorAction.OnMemeTextColorChange(
                                    id = selectedText.id,
                                    colorArgb = colorArgb,
                                )
                            )
                        },
                    )
                }
                BottomBar(
                    onAddTextClick = {
                        onAction(MemeEditorAction.OnAddTextClick)
                    },
                    onEditTextClick = selectedTextId?.let {
                        { onAction(MemeEditorAction.OnEditSelectedTextClick) }
                    },
                    onSaveClick = {
                        if (bytes != null) {
                            onAction(MemeEditorAction.OnSaveMemeClick(bytes))
                        } else {
                            onAction(MemeEditorAction.OnExportNotReady)
                        }
                    },
                    onSaveToGalleryClick = if (isGallerySaveSupported()) {
                        {
                            if (bytes != null) {
                                pendingGalleryBytes = bytes
                                requestGalleryPermission()
                            } else {
                                onAction(MemeEditorAction.OnExportNotReady)
                            }
                        }
                    } else {
                        null
                    },
                    exportEnabled = canExport,
                    actionsEnabled = !state.isExporting && !loading,
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when {
                loading -> CircularProgressIndicator()
                loadFailed -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp),
                    ) {
                        Text(
                            text = imageLoadFailedMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Box(modifier = Modifier.height(16.dp))
                        Button(onClick = { loadAttempt += 1 }) {
                            Text(stringResource(Res.string.retry_load))
                        }
                        Box(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                onAction(MemeEditorAction.OnConfirmLeaveWithoutSaving)
                            }
                        ) {
                            Text(stringResource(Res.string.back_to_templates))
                        }
                    }
                }
                else -> {
                    val windowSize = currentWindowSize()
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Box(modifier = Modifier.background(Color.Black)) {
                            val imageModifier = Modifier
                                .then(
                                    if (windowSize.width > windowSize.height) {
                                        Modifier.fillMaxHeight()
                                    } else {
                                        Modifier.fillMaxWidth()
                                    }
                                )
                                .onSizeChanged {
                                    onAction(MemeEditorAction.OnContainerSizeChange(it))
                                }
                            when (background) {
                                is MemeBackground.Template -> {
                                    Image(
                                        painter = painterResource(background.template.drawableResource),
                                        contentDescription = null,
                                        modifier = imageModifier,
                                        contentScale = ContentScale.Fit,
                                    )
                                }
                                is MemeBackground.CustomImage -> {
                                    val bitmap = customBitmap
                                    if (bitmap != null) {
                                        Image(
                                            bitmap = bitmap,
                                            contentDescription = null,
                                            modifier = imageModifier,
                                            contentScale = ContentScale.Fit,
                                        )
                                    }
                                }
                            }
                            DraggableContainer(
                                children = state.memeTexts,
                                textBoxInteractionState = state.textBoxInteractionState,
                                onChildTransformChanged = { id, offset, rotation, scale ->
                                    onAction(
                                        MemeEditorAction.OnMemeTextTransformChange(
                                            id = id,
                                            offset = offset,
                                            rotation = rotation,
                                            scale = scale,
                                        )
                                    )
                                },
                                onChildClick = {
                                    onAction(MemeEditorAction.OnSelectMemeText(it))
                                },
                                onChildDoubleClick = {
                                    onAction(MemeEditorAction.OnEditMemeText(it))
                                },
                                onChildTextChange = { id, text ->
                                    onAction(MemeEditorAction.OnMemeTextChange(id, text))
                                },
                                onChildDeleteClick = {
                                    onAction(MemeEditorAction.OnDeleteMemeTextClick(it))
                                },
                                modifier = Modifier.matchParentSize(),
                            )
                        }
                    }
                }
            }

            if (state.isExporting) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Box(modifier = Modifier.height(12.dp))
                        Text(
                            text = exportingCd,
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            IconButton(
                onClick = { onAction(MemeEditorAction.OnGoBackClick) },
                modifier = Modifier.align(Alignment.TopStart),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = backCd,
                )
            }
        }
    }

    if (state.isLeavingWithoutSaving) {
        ConfirmationDialog(
            config = ConfirmationDialogConfig(
                title = stringResource(Res.string.leave_editor_title),
                message = stringResource(Res.string.leave_editor_message),
                confirmButtonText = stringResource(Res.string.leave),
                cancelButtonText = stringResource(Res.string.cancel),
                confirmButtonColor = MaterialTheme.colorScheme.secondary,
            ),
            onConfirm = { onAction(MemeEditorAction.OnConfirmLeaveWithoutSaving) },
            onDismiss = { onAction(MemeEditorAction.OnDismissLeaveWithoutSaving) },
        )
    }

    if (state.isOfferingReturnHome) {
        ConfirmationDialog(
            config = ConfirmationDialogConfig(
                title = stringResource(Res.string.return_home_title),
                message = stringResource(Res.string.return_home_message),
                confirmButtonText = stringResource(Res.string.return_home_confirm),
                cancelButtonText = stringResource(Res.string.return_home_stay),
                confirmButtonColor = MaterialTheme.colorScheme.primary,
            ),
            onConfirm = { onAction(MemeEditorAction.OnConfirmReturnHome) },
            onDismiss = { onAction(MemeEditorAction.OnDismissReturnHome) },
        )
    }
}

/**
 * System back / predictive back → same leave path as in-app back.
 * No-op stub on targets without BackHandler (iOS still has in-app back).
 */
@Preview
@Composable
private fun Preview() {
    MemeCreatorTheme {
        MemeEditorScreen(
            background = MemeBackground.Template(
                MemesTemplate(
                    id = "meme_template_01",
                    drawableResource = Res.drawable.meme_template_01,
                )
            ),
            state = MemeEditorState(),
            onAction = {},
        )
    }
}
