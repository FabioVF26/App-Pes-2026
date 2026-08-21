package it.progettiesoluzioni.gestionale

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import it.progettiesoluzioni.gestionale.navigation.AppNavigation
import it.progettiesoluzioni.gestionale.ui.clienti.GestionaleViewModel
import it.progettiesoluzioni.gestionale.ui.splash.SplashScreen
import it.progettiesoluzioni.gestionale.ui.theme.PSGestionaleTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PSGestionaleTheme {
                var mostraSplash by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    delay(1_300)
                    mostraSplash = false
                }

                if (mostraSplash) {
                    SplashScreen()
                } else {
                    val gestionaleViewModel: GestionaleViewModel = viewModel()
                    AppNavigation(gestionaleViewModel)
                }
            }
        }
    }
}
