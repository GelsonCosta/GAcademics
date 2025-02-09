package com.gelsoncosta.gacademics.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gelsoncosta.gacademics.R
import com.gelsoncosta.gacademics.navigation.AppNavigator
import com.gelsoncosta.gacademics.ui.viewmodel.UserViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    userViewModel: UserViewModel,
    isLoggedIn: Boolean,
    isOffiline: Boolean?
) {
    val scale = remember { Animatable(0.5f) }
    val opacity = remember { Animatable(0f) }
    val isOfflineState = userViewModel.isOffiline.collectAsState()

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                delayMillis = 200
            )
        )
        opacity.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                delayMillis = 200
            )
        )
        delay(2500)

        if (isLoggedIn) {
            if (isOfflineState.value == true) {
                AppNavigator.navigateToDownloads()
            } else {
                onNavigateToHome()
            }
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer(
                        scaleX = scale.value,
                        scaleY = scale.value
                    )
            )
            Text(
                text = "G Academics",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .graphicsLayer(alpha = opacity.value)
            )
        }
    }
}