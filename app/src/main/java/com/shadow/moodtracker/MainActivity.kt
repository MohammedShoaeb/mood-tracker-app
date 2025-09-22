package com.shadow.moodtracker

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.shadow.moodtracker.ui.theme.MoodTrackerTheme
import com.shadow.moodtracker.viewmodel.AuthViewModel

//@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel: AuthViewModel by viewModels()
        authViewModel.checkAuthStatus()

        setContent {


            MoodTrackerTheme    {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding->

                    Navigation(authViewModel =authViewModel, modifier = Modifier.padding(innerPadding) )
                }
            }

        }
    }
}

