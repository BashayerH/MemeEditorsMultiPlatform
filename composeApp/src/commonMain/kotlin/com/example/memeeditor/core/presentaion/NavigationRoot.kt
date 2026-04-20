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
fun NavigationRoot(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.MemeGallery

    ){
        composable<Route.MemeGallery>{
            MemesScreen(
                onMemeSelected = {
                    navController.navigate(Route.MemeEditor(it.id))
                }
            )
        }

        composable<Route.MemeEditor>{
            val templateId = it.toRoute<Route.MemeEditor>().templateId
            val template = remember (templateId){
                memesListTemplates.first{it.id == templateId}
            }
            MemeEditorRoot(template= template)

        }

    }

}

