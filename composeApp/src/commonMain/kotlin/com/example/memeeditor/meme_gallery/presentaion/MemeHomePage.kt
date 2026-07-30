package com.example.memeeditor.meme_gallery.presentaion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.memeeditor.core.presentaion.MemesTemplate
import com.example.memeeditor.core.presentaion.memesListTemplates
import com.example.memeeditor.meme_editor.platform.rememberGalleryImagePicker
import kotlinx.coroutines.launch
import memeeditor.composeapp.generated.resources.Res
import memeeditor.composeapp.generated.resources.meme_template_cd
import memeeditor.composeapp.generated.resources.meme_templates
import memeeditor.composeapp.generated.resources.pick_from_gallery
import memeeditor.composeapp.generated.resources.processing_image
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemesScreen(
    onMemeSelected: (MemesTemplate) -> Unit,
    onCustomImagePicked: (filePath: String) -> Unit,
) {
    val displayTemplates = remember { memesListTemplates }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val pickFromGalleryLabel = stringResource(Res.string.pick_from_gallery)
    val templateCd = stringResource(Res.string.meme_template_cd)
    val processingLabel = stringResource(Res.string.processing_image)
    var isProcessingPick by remember { mutableStateOf(false) }

    val launchPicker = rememberGalleryImagePicker(
        onPicked = { path ->
            isProcessingPick = false
            onCustomImagePicked(path)
        },
        onError = { message ->
            isProcessingPick = false
            scope.launch { snackbarHostState.showSnackbar(message) }
        },
        onCancelled = { isProcessingPick = false },
        onProcessingStarted = { isProcessingPick = true },
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(Res.string.meme_templates))
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = launchPicker,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = Color(0xFF21005D),
                elevation = FloatingActionButtonDefaults.elevation(),
            ) {
                Icon(
                    imageVector = Icons.Filled.PhotoLibrary,
                    contentDescription = pickFromGalleryLabel,
                )
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(150.dp),
                contentPadding = PaddingValues(
                    start = innerPadding.calculateLeftPadding(LayoutDirection.Ltr) + 8.dp,
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    end = innerPadding.calculateRightPadding(LayoutDirection.Ltr) + 8.dp,
                    bottom = innerPadding.calculateBottomPadding() + 88.dp,
                ),
                verticalItemSpacing = 16.dp,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(
                    items = displayTemplates,
                    key = { it.id },
                ) { memesTemplate ->
                    Card(
                        onClick = { onMemeSelected(memesTemplate) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    ) {
                        Image(
                            painter = painterResource(memesTemplate.drawableResource),
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxSize(),
                            contentDescription = "$templateCd ${memesTemplate.id}",
                        )
                    }
                }
            }

            if (isProcessingPick) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Text(
                            text = processingLabel,
                            color = Color.White,
                            modifier = Modifier.padding(top = 12.dp),
                        )
                    }
                }
            }
        }
    }
}
