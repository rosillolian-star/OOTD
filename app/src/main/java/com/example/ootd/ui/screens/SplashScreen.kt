package com.example.ootd.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ootd.R
import com.example.ootd.ui.theme.OotdBlack
import com.example.ootd.ui.theme.OotdWhite
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0.85f) }
    val alpha = remember { Animatable(0f) }

    var loadingProgress by remember { mutableFloatStateOf(0.1f) }
    var statusText by remember { mutableStateOf("Initializing OOTD space...") }

    // Smooth progress bar animation
    val animatedProgress by animateFloatAsState(
        targetValue = loadingProgress,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "SmoothLoadingBar"
    )

    LaunchedEffect(Unit) {
        // Animate logo scale & alpha on app launch
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f)
        )
        alpha.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 500)
        )

        // Step 1: Database preparation
        delay(250)
        loadingProgress = 0.40f
        statusText = "Restoring user session..."

        // Step 2: Restoring persistent state
        delay(450)
        loadingProgress = 0.75f
        statusText = "Loading wardrobe & outfits..."

        // Step 3: Finalizing preparation
        delay(450)
        loadingProgress = 1.0f
        statusText = "Ready! Opening OOTD..."

        delay(350)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OotdBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // OOTD Custom Brand Logo with Soft Rounded Edges
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "OOTD Logo",
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .graphicsLayer(scaleX = scale.value, scaleY = scale.value)
                    .alpha(alpha.value)
            )

            Spacer(Modifier.height(36.dp))

            // Smooth Rounded Capsule Progress Bar
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(CircleShape)
                        .background(OotdWhite)
                )
            }

            Spacer(Modifier.height(18.dp))

            // Loading Status Label
            Text(
                text = statusText,
                color = Color(0xFFCCCCCC),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
