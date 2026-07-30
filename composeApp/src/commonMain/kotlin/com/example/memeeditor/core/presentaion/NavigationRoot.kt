package com.example.memeeditor.core.presentaion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.memeeditor.meme_editor.presentaion.MemeEditorRoot
import com.example.memeeditor.meme_gallery.presentaion.MemesScreen

@Composable
fun NavigationRoot() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.MemeGallery,
    ) {
        composable<Route.MemeGallery> {
            MemesScreen(
                onMemeSelected = {
                    navController.navigate(Route.MemeEditor(templateId = it.id))
                },
                onCustomImagePicked = { path ->
                    navController.navigate(
                        Route.MemeEditor(customImagePath = NavPathCodec.encode(path))
                    )
                },
            )
        }

        composable<Route.MemeEditor> {
            val route = it.toRoute<Route.MemeEditor>()
            val background = remember(route.templateId, route.customImagePath) {
                when {
                    route.customImagePath != null ->
                        MemeBackground.CustomImage(NavPathCodec.decode(route.customImagePath))
                    route.templateId != null -> {
                        val template = memesListTemplates.first { t -> t.id == route.templateId }
                        MemeBackground.Template(template)
                    }
                    else -> error("MemeEditor route requires templateId or customImagePath")
                }
            }
            MemeEditorRoot(
                background = background,
                onGoBack = { navController.navigateUp() },
            )
        }
    }
}
