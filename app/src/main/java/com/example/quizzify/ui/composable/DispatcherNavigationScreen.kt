package com.example.quizzify.ui.composable

/**
 * Components to build the navbar
 * and the dispatcher for all the pages in the home page.
 * */

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import com.example.quizzify.domainLayer.gameMaster.EndlessGraphViewModel
import com.example.quizzify.domainLayer.gameMaster.OnlineGraphViewModel
import com.example.quizzify.domainLayer.gameMaster.ProfileViewModel
import com.example.quizzify.domainLayer.gameMaster.QuizzifyHomeViewModel
import com.example.quizzify.ui.navigation.*
import com.example.quizzify.ui.util.*
import kotlinx.coroutines.launch

@Composable
fun DispatcherNavigationScreen(
    windowSize: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    viewModel: QuizzifyHomeViewModel,
    profileViewModel: ProfileViewModel,
    endlessGraphViewModel: EndlessGraphViewModel,
    onlineGraphViewModel: OnlineGraphViewModel,
) {
    /**
     * This will help us select type of navigation and content type depending on window size and
     * fold state of the device.
     */
    val navigationType: NavigationType

    /**
     * We are using display's folding features to map the device postures a fold is in.
     * In the state of folding device If it's half fold in BookPosture we want to avoid content
     * at the crease/hinge
     */
    val foldingFeature = displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()

    val foldingDevicePosture = when {
        isBookPosture(foldingFeature) ->
            DevicePosture.BookPosture(foldingFeature.bounds)

        isSeparating(foldingFeature) ->
            DevicePosture.Separating(foldingFeature.bounds, foldingFeature.orientation)

        else -> DevicePosture.NormalPosture
    }

    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            navigationType = NavigationType.BOTTOM_NAVIGATION
        }
        WindowWidthSizeClass.Medium -> {
            navigationType = NavigationType.NAVIGATION_RAIL
        }
        WindowWidthSizeClass.Expanded -> {
            navigationType = if (foldingDevicePosture is DevicePosture.BookPosture) {
                NavigationType.NAVIGATION_RAIL
            } else {
                NavigationType.PERMANENT_NAVIGATION_DRAWER
            }
        }
        else -> {
            navigationType = NavigationType.BOTTOM_NAVIGATION
        }
    }

    /**
     * Content inside Navigation Rail/Drawer can also be positioned at top, bottom or center for
     * ergonomics and reachability depending upon the height of the device.
     */
    val navigationContentPosition = when (windowSize.heightSizeClass) {
        WindowHeightSizeClass.Compact -> {
            NavigationContentPosition.TOP
        }
        WindowHeightSizeClass.Medium,
        WindowHeightSizeClass.Expanded -> {
            NavigationContentPosition.CENTER
        }
        else -> {
            NavigationContentPosition.TOP
        }
    }

    ReplyNavigationWrapper(
        navigationType = navigationType,
        navigationContentPosition = navigationContentPosition,
        windowSize = windowSize,
        viewModel = viewModel,
        profileViewModel = profileViewModel,
        graphViewModel = endlessGraphViewModel,
        onlineGraphViewModel = onlineGraphViewModel,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReplyNavigationWrapper(
    navigationType: NavigationType,
    navigationContentPosition: NavigationContentPosition,
    windowSize: WindowSizeClass,
    viewModel: QuizzifyHomeViewModel,
    profileViewModel: ProfileViewModel,
    graphViewModel: EndlessGraphViewModel,
    onlineGraphViewModel: OnlineGraphViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        NavigationActions(navController)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination =
        navBackStackEntry?.destination?.route ?: Route.COMPETITIVE

    if (navigationType == NavigationType.PERMANENT_NAVIGATION_DRAWER) {
        PermanentNavigationDrawer(drawerContent = {
            PermanentNavigationDrawerContent(
                selectedDestination = selectedDestination,
                navigationContentPosition = navigationContentPosition,
                navigateToTopLevelDestination = navigationActions::navigateTo,
            )
        }) {
            ReplyAppContent(
                navigationType = navigationType,
                navigationContentPosition = navigationContentPosition,
                navController = navController,
                selectedDestination = selectedDestination,
                navigateToTopLevelDestination = navigationActions::navigateTo,
                windowSize = windowSize,
                viewModel = viewModel,
                profileViewModel = profileViewModel,
                graphViewModel = graphViewModel,
                onlineGraphViewModel = onlineGraphViewModel,
            )
        }
    } else {
        ModalNavigationDrawer(
            scrimColor = Color.Transparent,
            drawerContent = {   //TODO:Remove if we want to remove the panel when sliding to right
                /*
                ModalNavigationDrawerContent(
                    selectedDestination = selectedDestination,
                    navigationContentPosition = navigationContentPosition,
                    navigateToTopLevelDestination = navigationActions::navigateTo,
                    onDrawerClicked = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
                */
            },
            drawerState = drawerState
        ) {
            ReplyAppContent(
                navigationType = navigationType,
                navigationContentPosition = navigationContentPosition,
                navController = navController,
                selectedDestination = selectedDestination,
                navigateToTopLevelDestination = navigationActions::navigateTo,
                windowSize = windowSize,
                viewModel = viewModel,
                profileViewModel = profileViewModel,
                graphViewModel = graphViewModel,
                onlineGraphViewModel = onlineGraphViewModel,
            ) {
                scope.launch {
                    drawerState.open()
                }
            }
        }
    }
}

@Composable
fun ReplyAppContent(
    modifier: Modifier = Modifier,
    navigationType: NavigationType,
    navigationContentPosition: NavigationContentPosition,
    navController: NavHostController,
    selectedDestination: String,
    windowSize: WindowSizeClass,
    viewModel: QuizzifyHomeViewModel,
    profileViewModel: ProfileViewModel,
    graphViewModel: EndlessGraphViewModel,
    onlineGraphViewModel: OnlineGraphViewModel,
    navigateToTopLevelDestination: (ReplyTopLevelDestination) -> Unit,
    onDrawerClicked: () -> Unit = {},
) {
    var showProfile by remember { mutableStateOf(false) }

    Row(modifier = modifier.fillMaxSize()) {

        AnimatedVisibility(visible = navigationType == NavigationType.NAVIGATION_RAIL) {
            ReplyNavigationRail(
                selectedDestination = selectedDestination,
                navigationContentPosition = navigationContentPosition,
                navigateToTopLevelDestination = navigateToTopLevelDestination,
                onDrawerClicked = onDrawerClicked,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.inversePrimary,
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.inversePrimary
                            )
                        )
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Quizzify",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }

            ReplyNavHost(
                navController = navController,
                modifier = Modifier.weight(1f),
                windowSize = windowSize,
                showProfile = showProfile,
                viewModel = viewModel,
                profileViewModel = profileViewModel,
                graphViewModel = graphViewModel,
                onlineGraphViewModel = onlineGraphViewModel,
            )
            AnimatedVisibility(visible = navigationType == NavigationType.BOTTOM_NAVIGATION) {

                ReplyBottomNavigationBar(
                    selectedDestination = selectedDestination,
                    navigateToTopLevelDestination = navigateToTopLevelDestination
                )
            }
        }
    }
}

@Composable
private fun ReplyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    windowSize: WindowSizeClass,
    showProfile: Boolean,
    viewModel: QuizzifyHomeViewModel,
    profileViewModel: ProfileViewModel,
    graphViewModel: EndlessGraphViewModel,
    onlineGraphViewModel: OnlineGraphViewModel,
) {
    val context = LocalContext.current
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Route.COMPETITIVE,
    ) {
        composable(Route.TRAINING) {
            PageLayout(
                title = "Training",
                description = "Play with different categories to improve yourself!",
                content = {
                    TrainingPage(
                        windowSize = windowSize,
                        viewModel = viewModel
                    )
                },
                windowSize=windowSize,
            )
        }
        composable(Route.COMPETITIVE) {
            PageLayout(
                title = "Competitive",
                description = "Play in competitive mode and improve you high score to be the #1 worldwide and challenge other people!",
                outerContent = {
                    FloatingActionButtonBottomCenter(
                        onClick = { goToQuiz(context, viewModel.uiState.value.endless_quizType) },
                        textButton = "Play Endless",
                        viewModel = viewModel
                    )
                },
                content = {
                    CompetitiveGamePage(
                        graphViewModel,
                        onlineGraphViewModel,
                        windowSize = windowSize
                    )
                },
                needMoreSpace = true,
                windowSize = windowSize,
            )
        }
        composable(Route.PROFILE) {
            PageLayout(
                title = "Profile",
                description = "Your personal data from your Spotify account",
                content = {
                    CreateProfile(
                        windowSize = windowSize,
                        profile = profileViewModel
                    )
                },
                windowSize=windowSize
            )
        }
    }
}

