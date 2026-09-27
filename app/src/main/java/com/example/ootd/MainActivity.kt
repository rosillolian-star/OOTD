package com.example.ootd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ootd.data.AppState
import com.example.ootd.navigation.OotdNavHost
import com.example.ootd.ui.theme.OOTDTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppState.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            OOTDTheme {
                OotdNavHost()
            }
        }
    }
}
