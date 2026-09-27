package com.example.ootd.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ootd.data.AppState
import com.example.ootd.ui.components.NavTab
import com.example.ootd.ui.components.OotdBottomBar
import com.example.ootd.ui.screens.*

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val AUTH = "auth?mode={mode}"
    const val HOME = "home"
    const val WARDROBE = "wardrobe"
    const val PLANNER = "planner"
    const val ADD_ITEM = "add_item?itemId={itemId}"
    const val ITEM_DETAIL = "item_detail/{itemId}"
    const val OUTFIT_DETAIL = "outfit_detail/{outfitId}"
    const val CREATE_OUTFIT = "create_outfit?outfitId={outfitId}"
    const val OUTFIT_HISTORY = "outfit_history"
    const val FAVORITES = "favorites"
    const val DISPOSAL = "disposal"
    const val PROFILE = "profile"
    const val PLACEHOLDER = "placeholder/{title}"
    const val WARDROBE_PICKER = "wardrobe_picker"

    fun auth(mode: AuthMode = AuthMode.LOGIN) = "auth?mode=${mode.name}"
    fun outfitDetail(id: String) = "outfit_detail/$id"
    fun itemDetail(id: String) = "item_detail/$id"
    fun placeholder(title: String) = "placeholder/$title"
    fun addItem(editId: String? = null) = if (editId == null) "add_item" else "add_item?itemId=$editId"
    fun createOutfit(editId: String? = null) = if (editId == null) "create_outfit" else "create_outfit?outfitId=$editId"
}

// Routes that should show the bottom navigation bar
private val bottomBarRoutes = setOf(Routes.HOME, Routes.WARDROBE, Routes.PLANNER, Routes.OUTFIT_HISTORY, Routes.PROFILE)

// Names of profile menu rows that already map to a real tab/screen.
private val profileMenuRouteMap = mapOf(
    "My Wardrobe" to Routes.WARDROBE,
    "Outfit History" to Routes.OUTFIT_HISTORY,
    "Favorites" to Routes.FAVORITES,
    "Disposal Suggestions" to Routes.DISPOSAL
)

@Composable
fun OotdNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val initialStartDestination = remember {
        if (AppState.currentUser.value != null) Routes.HOME else Routes.ONBOARDING
    }

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                val currentTab = when (currentRoute) {
                    Routes.HOME -> NavTab.HOME
                    Routes.WARDROBE -> NavTab.WARDROBE
                    Routes.PLANNER -> NavTab.PLANNER
                    Routes.OUTFIT_HISTORY -> NavTab.OUTFITS
                    Routes.PROFILE -> NavTab.PROFILE
                    else -> NavTab.HOME
                }
                OotdBottomBar(
                    current = currentTab,
                    onTabSelected = { tab ->
                        val route = when (tab) {
                            NavTab.HOME -> Routes.HOME
                            NavTab.WARDROBE -> Routes.WARDROBE
                            NavTab.PLANNER -> Routes.PLANNER
                            NavTab.OUTFITS -> Routes.OUTFIT_HISTORY
                            NavTab.PROFILE -> Routes.PROFILE
                        }
                        navController.navigate(route) {
                            popUpTo(Routes.HOME)
                            launchSingleTop = true
                        }
                    },
                    onAddClick = {
                        // Contextual "+": adding a wardrobe piece on the Wardrobe tab,
                        // otherwise building a new outfit.
                        if (currentRoute == Routes.WARDROBE) {
                            navController.navigate(Routes.addItem())
                        } else {
                            navController.navigate(Routes.createOutfit())
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            enterTransition = {
                fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)) +
                scaleIn(initialScale = 0.96f, animationSpec = tween(280, easing = FastOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(220, easing = FastOutSlowInEasing)) +
                scaleOut(targetScale = 0.98f, animationSpec = tween(220, easing = FastOutSlowInEasing))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)) +
                scaleIn(initialScale = 0.98f, animationSpec = tween(280, easing = FastOutSlowInEasing))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(220, easing = FastOutSlowInEasing)) +
                scaleOut(targetScale = 0.96f, animationSpec = tween(220, easing = FastOutSlowInEasing))
            },
            modifier = Modifier.padding(bottom = if (currentRoute in bottomBarRoutes) padding.calculateBottomPadding() else 0.dp)
        ) {
            composable(
                Routes.SPLASH,
                exitTransition = { fadeOut(animationSpec = tween(450)) }
            ) {
                SplashScreen(
                    onSplashFinished = {
                        val targetRoute = if (AppState.currentUser.value != null) Routes.HOME else Routes.ONBOARDING
                        navController.navigate(targetRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onGetStarted = {
                        navController.navigate(Routes.auth(AuthMode.SIGN_UP))
                    },
                    onLogin = {
                        navController.navigate(Routes.auth(AuthMode.LOGIN))
                    }
                )
            }

            composable(
                Routes.AUTH,
                arguments = listOf(navArgument("mode") {
                    type = NavType.StringType
                    defaultValue = AuthMode.LOGIN.name
                })
            ) { entry ->
                val modeString = entry.arguments?.getString("mode") ?: AuthMode.LOGIN.name
                val initialMode = try {
                    AuthMode.valueOf(modeString)
                } catch (e: Exception) {
                    AuthMode.LOGIN
                }

                AuthScreen(
                    initialMode = initialMode,
                    onBack = { navController.popBackStack() },
                    onAuthSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.HOME) {
                HomeScreen(
                    onOutfitClick = { outfit -> navController.navigate(Routes.outfitDetail(outfit.id)) },
                    onSeeAllOutfits = { navController.navigate(Routes.OUTFIT_HISTORY) },
                    onWardrobeItemClick = { item -> navController.navigate(Routes.itemDetail(item.id)) }
                )
            }

            composable(Routes.WARDROBE) {
                WardrobeScreen(
                    onItemClick = { item -> navController.navigate(Routes.itemDetail(item.id)) }
                )
            }

            composable(Routes.PLANNER) {
                PlannerScreen(
                    onOutfitClick = { outfit -> navController.navigate(Routes.outfitDetail(outfit.id)) }
                )
            }

            composable(
                Routes.ADD_ITEM,
                arguments = listOf(navArgument("itemId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { entry ->
                val itemId = entry.arguments?.getString("itemId")
                val existingItem = AppState.findItem(itemId)
                AddItemScreen(
                    existingItem = existingItem,
                    onBack = { navController.popBackStack() },
                    onSave = { item ->
                        if (existingItem != null) AppState.updateItem(item) else AppState.addItem(item)
                        navController.popBackStack()
                    }
                )
            }

            composable(
                Routes.ITEM_DETAIL,
                arguments = listOf(navArgument("itemId") { type = NavType.StringType })
            ) { entry ->
                val itemId = entry.arguments?.getString("itemId")
                val item = AppState.findItem(itemId)
                if (item != null) {
                    ItemDetailScreen(
                        item = item,
                        onBack = { navController.popBackStack() },
                        onEdit = { navController.navigate(Routes.addItem(item.id)) },
                        onDelete = {
                            navController.popBackStack()
                            AppState.wardrobe.remove(item)
                        }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }

            composable(
                Routes.OUTFIT_DETAIL,
                arguments = listOf(navArgument("outfitId") { type = NavType.StringType })
            ) { entry ->
                val outfitId = entry.arguments?.getString("outfitId")
                val outfit = AppState.findOutfit(outfitId)
                if (outfit != null) {
                    OutfitDetailScreen(
                        outfit = outfit,
                        onBack = { navController.popBackStack() },
                        onEdit = { navController.navigate(Routes.createOutfit(outfit.id)) },
                        onDelete = {
                            navController.popBackStack()
                            AppState.deleteOutfit(outfit.id)
                        }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }

            composable(
                Routes.CREATE_OUTFIT,
                arguments = listOf(navArgument("outfitId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { entry ->
                val outfitId = entry.arguments?.getString("outfitId")
                val existingOutfit = AppState.findOutfit(outfitId)
                CreateOutfitScreen(
                    existingOutfit = existingOutfit,
                    onBack = { navController.popBackStack() },
                    onSave = { outfit ->
                        if (existingOutfit != null) AppState.updateOutfit(outfit) else AppState.addOutfit(outfit)
                        navController.popBackStack()
                    },
                    onAddFromWardrobe = { navController.navigate(Routes.WARDROBE_PICKER) }
                )
            }

            composable(Routes.WARDROBE_PICKER) {
                WardrobePickerScreen(onDone = { navController.popBackStack() })
            }

            composable(Routes.OUTFIT_HISTORY) {
                OutfitHistoryScreen(
                    onOutfitClick = { outfit -> navController.navigate(Routes.outfitDetail(outfit.id)) }
                )
            }

            composable(Routes.FAVORITES) {
                FavoritesScreen(
                    onBack = { navController.popBackStack() },
                    onItemClick = { item -> navController.navigate(Routes.itemDetail(item.id)) },
                    onOutfitClick = { outfit -> navController.navigate(Routes.outfitDetail(outfit.id)) }
                )
            }

            composable(Routes.DISPOSAL) {
                DisposalScreen(
                    onBack = { navController.popBackStack() },
                    onItemClick = { item -> navController.navigate(Routes.itemDetail(item.id)) }
                )
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    onMenuItemClick = { label ->
                        if (label == "Log Out") {
                            AppState.logout()
                            navController.navigate(Routes.ONBOARDING) {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            val mapped = profileMenuRouteMap[label]
                            if (mapped != null) {
                                navController.navigate(mapped)
                            } else {
                                navController.navigate(Routes.placeholder(label))
                            }
                        }
                    }
                )
            }

            composable(
                Routes.PLACEHOLDER,
                arguments = listOf(navArgument("title") { type = NavType.StringType })
            ) { entry ->
                val title = entry.arguments?.getString("title") ?: ""
                PlaceholderScreen(title = title, onBack = { navController.popBackStack() })
            }
        }
    }
}
