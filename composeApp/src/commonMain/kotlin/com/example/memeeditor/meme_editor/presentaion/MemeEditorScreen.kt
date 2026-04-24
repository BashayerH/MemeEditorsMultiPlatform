

@file:OptIn(ExperimentalComposeUiApi::class)
package com.example.memeeditor.meme_editor.presentaion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.currentWindowSize

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memeeditor.core.presentaion.MemesTemplate
import com.example.memeeditor.core.theme.MemeCreatorTheme
import com.example.memeeditor.meme_editor.platform.isGallerySaveSupported
import com.example.memeeditor.meme_editor.presentaion.components.BottomBar
import com.example.memeeditor.meme_editor.presentaion.components.ConfirmationDialog
import com.example.memeeditor.meme_editor.presentaion.components.ConfirmationDialogConfig
import com.example.memeeditor.meme_editor.presentaion.components.DraggableContainer
import memeeditor.composeapp.generated.resources.Res
import memeeditor.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MemeEditorRoot(
    template: MemesTemplate,
    onGoBack: () -> Unit,
    viewModel: MemeEditorViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.hasLeftEditor) {
        if(state.hasLeftEditor) {
            onGoBack()
        }
    }


    MemeEditorScreen(
        template = template,
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun MemeEditorScreen(
    template: MemesTemplate,
    state: MemeEditorState,
    onAction: (MemeEditorAction) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val gallerySavedMessage = stringResource(Res.string.gallery_saved)
    val galleryFailedMessage = stringResource(Res.string.gallery_save_failed)

    LaunchedEffect(state.uiMessage, gallerySavedMessage, galleryFailedMessage) {
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
        }
    }


//    NavigationEventHandler(
//        isBackEnabled = !state.isLeavingWithoutSaving,
//        onBack = {
//            onAction(MemeEditorAction.OnGoBackClick)
//            true
//        },
//        initialInfo = NavigationEventHandler.InitialInfo(
//            title = stringResource(Res.string.meme_editor_title)
//        )
//
//    )
//    BackHandler(
//        enabled = !state.isLeavingWithoutSaving
//    ) {
//        onAction(MemeEditorAction.OnGoBackClick)
//    }

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
            BottomBar(
                onAddTextClick = {
                    onAction(MemeEditorAction.OnAddTextClick)
                },
                onSaveClick = {
                    onAction(MemeEditorAction.OnSaveMemeClick(template))
                },
                onSaveToGalleryClick = if (isGallerySaveSupported()) {
                    {
                        onAction(MemeEditorAction.OnSaveToGalleryClick(template))
                    }
                } else {
                    null
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val windowSize = currentWindowSize()
            // Meme coordinates and bitmap export are LTR; forcing LTR here keeps Arabic centered in the
            // text box and matches Android Canvas / StaticLayout (bitmap space is always LTR).
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Box(modifier = Modifier.background(Color.Black)) {
                    Image(
                        painter = painterResource(template.drawableResource),
                        contentDescription = null,
                        modifier = Modifier
                            .then(
                                if(windowSize.width > windowSize.height) {
                                    Modifier.fillMaxHeight()
                                } else Modifier.fillMaxWidth()
                            )
                            .onSizeChanged {
                                onAction(MemeEditorAction.OnContainerSizeChange(it))
                            },
                        contentScale = ContentScale.Fit
                    )
                    DraggableContainer(
                        children = state.memeTexts,
                        textBoxInteractionState = state.textBoxInteractionState,
                        onChildTransformChanged = { id, offset, rotation, scale ->
                            onAction(MemeEditorAction.OnMemeTextTransformChange(
                                id = id,
                                offset = offset,
                                rotation = rotation,
                                scale = scale
                            ))
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
                        modifier = Modifier
                            .matchParentSize()
                    )
                }
            }

            IconButton(
                onClick = {
                    onAction(MemeEditorAction.OnGoBackClick)
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    }

    if(state.isLeavingWithoutSaving) {
        ConfirmationDialog(
            config = ConfirmationDialogConfig(
                title = stringResource(Res.string.leave_editor_title),
                message = stringResource(Res.string.leave_editor_message),
                confirmButtonText = stringResource(Res.string.leave),
                cancelButtonText = stringResource(Res.string.cancel),
                confirmButtonColor = MaterialTheme.colorScheme.secondary
            ),
            onConfirm = {
                onAction(MemeEditorAction.OnConfirmLeaveWithoutSaving)
            },
            onDismiss = {
                onAction(MemeEditorAction.OnDismissLeaveWithoutSaving)
            }
        )
    }
}





@Preview
@Composable
private fun Preview() {
    MemeCreatorTheme {
        MemeEditorScreen(
            template = MemesTemplate(
                id = "meme_template_01",
                drawableResource = Res.drawable.meme_template_01
            ),
            state = MemeEditorState(),
            onAction = {}
        )
    }
}