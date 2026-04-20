package com.example.memeeditor.core.presentaion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController



@Composable
fun NavigationRoot(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home"

    ){

    }

}

