package it.progettiesoluzioni.gestionale.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy

private enum class ModalitaLogin { ACCEDI, RESET_PASSWORD }

@Composable
fun LoginScreen(viewModel: AuthViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    var modalita by rememberSaveable { mutableStateOf(ModalitaLogin.ACCEDI) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisibile by remember { mutableStateOf(false) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = BrandNavy,
                modifier = Modifier
                    .padding(top = 48.dp, bottom = 16.dp)
                    .height(56.dp)
            )

            Text(
                text = "Progetti e Soluzioni",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )
            Text(
                text = if (modalita == ModalitaLogin.ACCEDI) "Accedi al gestionale" else "Recupera la password",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            if (modalita == ModalitaLogin.ACCEDI) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisibile) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisibile = !passwordVisibile }) {
                            Icon(
                                imageVector = if (passwordVisibile) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisibile) "Nascondi password" else "Mostra password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            uiState.errore?.let { errore ->
                Text(
                    text = errore,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            if (uiState.emailResetInviata) {
                Text(
                    text = "Email di recupero inviata. Controlla la posta e segui il link ricevuto.",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (modalita == ModalitaLogin.ACCEDI) viewModel.accedi(email, password)
                    else viewModel.inviaResetPassword(email)
                },
                enabled = !uiState.caricamento,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (uiState.caricamento) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (modalita == ModalitaLogin.ACCEDI) "Accedi" else "Invia email di recupero")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (modalita == ModalitaLogin.ACCEDI) {
                TextButton(onClick = {
                    viewModel.consumaErrore()
                    viewModel.consumaMessaggioReset()
                    modalita = ModalitaLogin.RESET_PASSWORD
                }) {
                    Text("Password dimenticata?")
                }
                Text(
                    text = "Gli account sono abilitati dall'amministratore.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else {
                TextButton(onClick = {
                    viewModel.consumaErrore()
                    modalita = ModalitaLogin.ACCEDI
                }) {
                    Text("Torna al login")
                }
            }
        }
    }
}
