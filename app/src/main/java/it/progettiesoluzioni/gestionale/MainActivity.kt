package it.progettiesoluzioni.gestionale

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import it.progettiesoluzioni.gestionale.auth.AuthViewModel
import it.progettiesoluzioni.gestionale.auth.LoginScreen
import it.progettiesoluzioni.gestionale.navigation.AppNavigation
import it.progettiesoluzioni.gestionale.notifications.NotificationHelper
import it.progettiesoluzioni.gestionale.notifications.ScadenzaScheduler
import it.progettiesoluzioni.gestionale.ui.clienti.GestionaleViewModel
import it.progettiesoluzioni.gestionale.ui.splash.SplashScreen
import it.progettiesoluzioni.gestionale.ui.theme.PSGestionaleTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val externalRoute = mutableStateOf<String?>(null)
    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gestisciIntent(intent)
        NotificationHelper.creaCanale(this)
        ScadenzaScheduler.programma(this)
        ScadenzaScheduler.controllaOra(this)
        richiediPermessoNotifiche()

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
                    val authViewModel: AuthViewModel = viewModel()
                    val utente by authViewModel.utenteCorrente.collectAsState()

                    if (utente == null) {
                        LoginScreen(viewModel = authViewModel)
                    } else {
                        val gestionaleViewModel: GestionaleViewModel = viewModel()
                        val route by externalRoute
                        AppNavigation(
                            viewModel = gestionaleViewModel,
                            externalRoute = route,
                            onExternalRouteConsumed = { externalRoute.value = null },
                            onLogout = { authViewModel.disconnetti() }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        gestisciIntent(intent)
    }

    private fun gestisciIntent(intent: Intent?) {
        if (intent?.action == ACTION_OPEN_SCADENZE) externalRoute.value = "scadenze"
    }

    private fun richiediPermessoNotifiche() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    companion object {
        const val ACTION_OPEN_SCADENZE = "it.progettiesoluzioni.gestionale.OPEN_SCADENZE"
    }
}
