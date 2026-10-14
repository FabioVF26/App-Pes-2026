package it.progettiesoluzioni.gestionale

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import it.progettiesoluzioni.gestionale.util.CrashLogger
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val externalRoute = mutableStateOf<String?>(null)
    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CrashLogger.installa(this)
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

                // Se l'app si e' chiusa in modo imprevisto, mostra l'errore una sola volta al riavvio.
                var erroreAvvioPrecedente by rememberSaveable {
                    mutableStateOf<String?>(CrashLogger.leggiEPulisci(this@MainActivity))
                }
                erroreAvvioPrecedente?.let { testo ->
                    AlertDialog(
                        onDismissRequest = { erroreAvvioPrecedente = null },
                        title = { Text("L'app si era chiusa in modo imprevisto") },
                        text = {
                            Column {
                                Text(
                                    "Premi Copia e incolla il testo nella chat: serve a capire e correggere il problema.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    testo.take(3000),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    modifier = Modifier.heightIn(max = 280.dp).verticalScroll(rememberScrollState())
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { copiaNegliAppunti(testo) }) { Text("Copia") }
                        },
                        dismissButton = {
                            TextButton(onClick = { erroreAvvioPrecedente = null }) { Text("Chiudi") }
                        }
                    )
                }
            }
        }
    }

    private fun copiaNegliAppunti(testo: String) {
        val appunti = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        appunti.setPrimaryClip(ClipData.newPlainText("Errore P&S Gestionale", testo))
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
