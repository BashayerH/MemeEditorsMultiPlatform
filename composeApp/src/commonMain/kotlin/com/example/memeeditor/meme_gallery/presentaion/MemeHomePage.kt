package com.example.memeeditor.meme_gallery.presentaion

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.memeeditor.core.presentaion.MemesTemplate
import com.example.memeeditor.core.presentaion.memesListTemplates
import memeeditor.composeapp.generated.resources.Res
import memeeditor.composeapp.generated.resources.meme_templates
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemesScreen(

    onMemeSelected: (MemesTemplate) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(Res.string.meme_templates))
                }
            )

        }
    ) { innerPadding ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(150.dp),
            contentPadding = PaddingValues(
                start = innerPadding.calculateLeftPadding(LayoutDirection.Ltr) + 8.dp ,
                top = innerPadding.calculateTopPadding() + 8.dp,
                end = innerPadding.calculateRightPadding(LayoutDirection.Ltr) + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 8.dp
            ),
            verticalItemSpacing = 16.dp,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ){
            items(
                items = memesListTemplates,
                key = {it.id}
            ){ memesTemplate ->
                Card(
                    onClick = { onMemeSelected(memesTemplate) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ){
                    Image(
                        painter = painterResource(memesTemplate.drawableResource),
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = null
                    )
                }


            }
        }


    }
}




