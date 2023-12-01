package com.example.quizzify.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.quizzify.R

object Route {
    const val TRAINING = "Training"
    const val COMPETITIVE = "Competitive"
    const val PROFILE = "Profile"
}

data class ReplyTopLevelDestination(
    val route: String,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val iconTextId: Int
)

class NavigationActions(private val navController: NavHostController) {

    fun navigateTo(destination: ReplyTopLevelDestination) {
        navController.navigate(destination.route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
}

val TOP_LEVEL_DESTINATIONS = listOf(
    ReplyTopLevelDestination(
        route = Route.TRAINING,
        selectedIcon = R.drawable.training1,
        unselectedIcon = R.drawable.training1,
        iconTextId = R.string.Training
    ),
    ReplyTopLevelDestination(
        route = Route.COMPETITIVE,
        selectedIcon = R.drawable.podium,
        unselectedIcon = R.drawable.podium,
        iconTextId = R.string.Competitive
    ),
    ReplyTopLevelDestination(
        route = Route.PROFILE,
        selectedIcon = R.drawable.profile,
        unselectedIcon = R.drawable.profile,
        iconTextId = R.string.Profile
    )

)
