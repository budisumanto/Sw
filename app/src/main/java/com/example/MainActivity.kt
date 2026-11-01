package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.screens.StatusHDOptimizerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.OptimizerViewModel

class MainActivity : ComponentActivity() {

  private val optimizerViewModel: OptimizerViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme(darkTheme = true) {
        StatusHDOptimizerScreen(viewModel = optimizerViewModel)
      }
    }
  }
}
