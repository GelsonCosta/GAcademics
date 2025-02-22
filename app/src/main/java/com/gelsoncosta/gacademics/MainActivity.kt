package com.gelsoncosta.gacademics

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.gelsoncosta.gacademics.data.api.RetrofitInstance
import com.gelsoncosta.gacademics.data.api.TokenManager
import com.gelsoncosta.gacademics.data.local.GAcademicsDatabase
import com.gelsoncosta.gacademics.navigation.NavGraph
import com.gelsoncosta.gacademics.ui.viewmodel.UserViewModel
import com.gelsoncosta.gacademics.ui.viewmodel.MaterialViewModel
import com.gelsoncosta.gacademics.ui.viewmodel.CommentViewModel

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val tokenManager = TokenManager(sharedPreferences)
        RetrofitInstance.initialize(tokenManager)
        val database = GAcademicsDatabase.getInstance(applicationContext)
        val pdfMaterialDao = database.pdfMaterialDao()
        //val datastore = DataStoreHelper(applicationContext)

        val userViewModel = UserViewModel(RetrofitInstance.api, tokenManager)
        val materialViewModel = MaterialViewModel(RetrofitInstance.api, pdfMaterialDao)
        val commentViewModel = CommentViewModel(RetrofitInstance.api)

        setContent {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        userViewModel = userViewModel,
                        materialViewModel = materialViewModel,
                        commentViewModel = commentViewModel
                    )
                }

        }
    }
}
object SigleConstVariables {
    const val BASE_URL: String =  "http://192.168.98.206:3000"

    public val academicCategories = listOf(
        "Matemática",
        "Física",
        "Química",
        "Biologia",
        "Ciência da Computação",
        "Engenharia",
        "Literatura",
        "História",
        "Geografia",
        "Filosofia",
        "Psicologia",
        "Economia",
        "Negócios",
        "Direito",
        "Medicina",
        "Artes",
        "Línguas",
        "Ciências Sociais",
        "Educação",
        "Outros"
    )
}