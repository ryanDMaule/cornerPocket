package com.example.cornerpocket

import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions

object NavigationUtils {

    fun navigateAndClearBackStack(
        navController: NavController,
        @IdRes actionId: Int,
        @IdRes popUpToId: Int
    ) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(popUpToId, true)
            .build()

        navController.navigate(actionId, null, navOptions)
    }


}