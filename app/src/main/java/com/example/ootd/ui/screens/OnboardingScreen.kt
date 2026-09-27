package com.example.ootd.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ootd.R
import com.example.ootd.ui.theme.OotdBlack
import com.example.ootd.ui.theme.OotdWhite

@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit,
    onLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OotdBlack)
    ) {
        // Full screen background photo provided by user
        Image(
            painter = painterResource(id = R.drawable.onboarding_bg),
            contentDescription = "OOTD Fashion Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle dark gradient overlay for optimal text contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.15f),
                            Color.Black.copy(alpha = 0.45f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Foreground content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(28.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "OOTD",
                color = OotdWhite,
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Your Style. Your Story.",
                color = Color(0xFFE0E0E0),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Save your outfits, organize your wardrobe, and keep track of the stories behind what you wear.",
                color = Color(0xFFCCCCCC),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(20.dp))

            // Divider bar matching reference design
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(2.dp)
                    .background(Color.White.copy(alpha = 0.6f))
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = onGetStarted,
                colors = ButtonDefaults.buttonColors(containerColor = OotdWhite, contentColor = OotdBlack),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Get Started  →", fontSize = 16.sp)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "I already have an account",
                color = OotdWhite,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogin() }
            )

            Spacer(Modifier.height(12.dp))
        }
    }
}
