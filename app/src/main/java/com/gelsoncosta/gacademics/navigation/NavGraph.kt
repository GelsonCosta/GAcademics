package com.gelsoncosta.gacademics.navigation


import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.asLiveData
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gelsoncosta.gacademics.ui.screens.*
import com.gelsoncosta.gacademics.ui.viewmodel.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Search:Screen("search")
    object MyMaterialList: Screen("mymaterials")
    object  Downloads: Screen("downloads")
    object UploadMaterialScreen: Screen("uploadMaterial")
    object UpdateMaterialScreen: Screen("updateMaterial/{materialId}"){
        fun createRoute(materialId: Int) = "updateMaterial/$materialId"
    }
    object MaterialDetail : Screen("materials/{materialId}") {
        fun createRoute(materialId: Int) = "materials/$materialId"
    }
    object pdfReader : Screen("pdfReader/{id}") {
        fun createRoute(id:Int) = "pdfReader/$id"
    }
    object OfflinepdfReader : Screen("offlinePdfReader/{id}") {
        fun createRoute(id:Int) = "offlinePdfReader/$id"
    }
    object FavoriteList : Screen("favorites")
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NavGraph(
    navController: NavHostController,
    userViewModel: UserViewModel,
    materialViewModel: MaterialViewModel,
    commentViewModel: CommentViewModel,
    startDestination: String = Screen.Splash.route
) {
    AppNavigator.setNavController(navController,userViewModel)
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                isLoggedIn = userViewModel.authToken.value != null,
                userViewModel = userViewModel,
                isOffiline = userViewModel.isOffiline.value
            )
        }
        composable(Screen.UploadMaterialScreen.route) {
            UploadMaterialScreen(
                viewModel = materialViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onUploadSuccess = {
                    navController.popBackStack()
                })
        }
        composable(
            route = Screen.UpdateMaterialScreen.route,
            arguments = listOf(
                navArgument("materialId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val materialId = backStackEntry.arguments?.getInt("materialId") ?: return@composable
            UpdateMaterialScreen(
                viewModel = materialViewModel,
                materialId = materialId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onUpdateSuccess = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.pdfReader.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            )
        ){
            backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            PdfReaderScreen(viewModel = materialViewModel, id = id,onNavigateBack = {
                navController.popBackStack()
            })
        }

        composable(
            route = Screen.OfflinepdfReader.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            )
        ){
                backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            LocalPdfReaderScreen(viewModel = materialViewModel, id = id,onNavigateBack = {
                navController.popBackStack()
            })
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = userViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = userViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }


        composable(Screen.Home.route) {
            MaterialListScreen(
                viewModel = materialViewModel,
                userViewModel = userViewModel,
                onNavigateToDetail = { materialId ->
                    navController.navigate(Screen.MaterialDetail.createRoute(materialId))
                }
            )
        }
        composable(Screen.Search.route) {
            SearchScreen (
                viewModel = materialViewModel,
                //userViewModel = userViewModel,
                onNavigateToDetail = { materialId ->
                    navController.navigate(Screen.MaterialDetail.createRoute(materialId))
                }
            )
        }
        composable(Screen.FavoriteList.route) {
            FavoritelListScreen (
                viewModel = materialViewModel,
                onNavigateToDetail = { materialId ->
                    navController.navigate(Screen.MaterialDetail.createRoute(materialId))
                }
            )
        }
        composable(Screen.MyMaterialList.route) {
            MyMaterialListScreen(
                viewModel = materialViewModel,
                onNavigateToDetail = { materialId ->
                   // navController.navigate(Screen.MaterialDetail.createRoute(materialId))
                }
            )
        }
        composable(Screen.Downloads.route) {
            OfflineMaterialListScreen(
                viewModel = materialViewModel,
                onNavigateToDetail = { materialId ->
                    // navController.navigate(Screen.MaterialDetail.createRoute(materialId))
                }
            )
        }

        composable(
            route = Screen.MaterialDetail.route,
            arguments = listOf(
                navArgument("materialId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val materialId = backStackEntry.arguments?.getInt("materialId") ?: return@composable
            MaterialDetailsScreen(
                viewModel = materialViewModel,
                userViewModel = userViewModel,
                commentViewModel = commentViewModel,
                materialId = materialId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

    }

}
object AppNavigator {
    private var navController: NavHostController? = null
    private var userViewModel: UserViewModel? = null

    fun setNavController(controller: NavHostController, userViewModelx: UserViewModel) {
        navController = controller
        userViewModel = userViewModelx
    }

    fun navigateToHome() {
        navController?.navigate(Screen.Home.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    fun navigateToMyMaterials(){
       navController?.navigate(Screen.MyMaterialList.route)
    }
    fun navigateToFavorites(){
        navController?.navigate(Screen.FavoriteList.route)
    }
    fun navigateToDownloads(){
        navController?.navigate(Screen.Downloads.route)
    }
    fun  navigateToAddMaterial(){
       navController?.navigate(Screen.UploadMaterialScreen.route)
    }
    fun navigateToUpdateMaterial (materialId: Int){
        navController?.navigate(Screen.UpdateMaterialScreen.createRoute(materialId))
    }
    fun navigateToPdfReader(id:Int){
        navController?.navigate(Screen.pdfReader.createRoute(id))
    }

    fun navigateToDetail(materialId: Int) {
        navController?.navigate(Screen.MaterialDetail.createRoute(materialId),{launchSingleTop = true})
    }
    fun navigateToOfflinePdfReader(id:Int){
        navController?.navigate(Screen.OfflinepdfReader.createRoute(id))
    }
    fun navigateToSearch(){
        navController?.navigate(Screen.Search.route)
    }

    fun navigatePopBack(){
        navController?.popBackStack()
    }


    fun onSignOut() {
        userViewModel?.logout()
        navController?.navigate(Screen.Login.route) {
            popUpTo(Screen.Home.route) { inclusive = true }
        }
    }


}

