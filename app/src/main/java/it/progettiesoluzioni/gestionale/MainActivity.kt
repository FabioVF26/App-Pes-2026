package it.progettiesoluzioni.gestionale

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import it.progettiesoluzioni.gestionale.navigation.AppNavigation
import it.progettiesoluzioni.gestionale.ui.clienti.GestionaleViewModel
import it.progettiesoluzioni.gestionale.ui.theme.PSGestionaleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PSGestionaleTheme {
                val gestionaleViewModel: GestionaleViewModel = viewModel()
                AppNavigation(gestionaleViewModel)
            }
        }
    }
}
