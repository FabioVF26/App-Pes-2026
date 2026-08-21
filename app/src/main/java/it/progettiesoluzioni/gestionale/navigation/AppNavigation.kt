package it.progettiesoluzioni.gestionale.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.progettiesoluzioni.gestionale.ui.clienti.ClienteDettaglioScreen
import it.progettiesoluzioni.gestionale.ui.clienti.ClientiScreen
import it.progettiesoluzioni.gestionale.ui.clienti.GestionaleViewModel
import it.progettiesoluzioni.gestionale.ui.clienti.ModificaClienteScreen
import it.progettiesoluzioni.gestionale.ui.clienti.NuovaSedeScreen
import it.progettiesoluzioni.gestionale.ui.clienti.NuovoClienteScreen
import it.progettiesoluzioni.gestionale.ui.dashboard.DashboardScreen
import it.progettiesoluzioni.gestionale.ui.scadenze.ScadenzeScreen
import it.progettiesoluzioni.gestionale.ui.scadenze.NuovaAttivitaScreen
import it.progettiesoluzioni.gestionale.ui.documenti.DocumentiScreen
import it.progettiesoluzioni.gestionale.ui.documenti.NuovoDocumentoScreen
import it.progettiesoluzioni.gestionale.ui.servizi.ServizioClienteScreen
import it.progettiesoluzioni.gestionale.ui.sopralluoghi.NuovoSopralluogoHaccpScreen
import it.progettiesoluzioni.gestionale.ui.sopralluoghi.NuovoSopralluogoSicurezzaScreen
import it.progettiesoluzioni.gestionale.ui.sopralluoghi.SopralluoghiScreen
import it.progettiesoluzioni.gestionale.ui.sopralluoghi.SopralluogoHaccpScreen
import it.progettiesoluzioni.gestionale.ui.sopralluoghi.SopralluogoSicurezzaScreen
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy

private data class NavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun AppNavigation(viewModel: GestionaleViewModel) {
    val navController = rememberNavController()
    val bottomItems = listOf(
        NavItem("dashboard", "Home", Icons.Default.Home),
        NavItem("clienti", "Clienti", Icons.Default.People),
        NavItem("scadenze", "Scadenze", Icons.Default.Event),
        NavItem("sopralluoghi", "Sopralluoghi", Icons.Default.Assignment),
        NavItem("documenti", "Documenti", Icons.Default.Description)
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = currentDestination?.route in bottomItems.map { it.route }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Color.White) {
                    bottomItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BrandNavy,
                                selectedTextColor = BrandNavy,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") { DashboardScreen(viewModel) }
            composable("clienti") {
                ClientiScreen(
                    viewModel = viewModel,
                    onNuovoCliente = { navController.navigate("nuovoCliente") },
                    onClienteClick = { navController.navigate("cliente/$it") }
                )
            }
            composable("nuovoCliente") { NuovoClienteScreen(viewModel) { navController.popBackStack() } }
            composable("cliente/{clienteId}") { entry ->
                val id = entry.arguments?.getString("clienteId")?.toLongOrNull() ?: return@composable
                ClienteDettaglioScreen(
                    clienteId = id,
                    viewModel = viewModel,
                    onModificaCliente = { navController.navigate("modificaCliente/$id") },
                    onAggiungiSede = { navController.navigate("nuovaSede/$id") },
                    onNuovoSopralluogoHaccp = { navController.navigate("nuovoSopralluogoHaccp/$id") },
                    onNuovoSopralluogoSicurezza = { navController.navigate("nuovoSopralluogoSicurezza/$id") },
                    onApriSopralluogo = { sopralluogoId, tipo ->
                        if (tipo == "SICUREZZA") navController.navigate("sopralluogoSicurezza/$sopralluogoId")
                        else navController.navigate("sopralluogoHaccp/$sopralluogoId")
                    },
                    onApriServizio = { tipo -> navController.navigate("servizio/$id/$tipo") }
                )
            }
            composable("modificaCliente/{clienteId}") { entry ->
                val id = entry.arguments?.getString("clienteId")?.toLongOrNull() ?: return@composable
                ModificaClienteScreen(
                    clienteId = id,
                    viewModel = viewModel,
                    onSaved = { navController.popBackStack() },
                    onArchived = {
                        navController.navigate("clienti") { popUpTo("clienti") { inclusive = true } }
                    }
                )
            }
            composable("nuovaSede/{clienteId}") { entry ->
                val id = entry.arguments?.getString("clienteId")?.toLongOrNull() ?: return@composable
                NuovaSedeScreen(id, viewModel) { navController.popBackStack() }
            }
            composable("scadenze") { ScadenzeScreen(viewModel, onNuova = { navController.navigate("nuovaAttivita") }) }
            composable("nuovaAttivita") { NuovaAttivitaScreen(viewModel) { navController.popBackStack() } }
            composable("sopralluoghi") {
                SopralluoghiScreen(
                    viewModel = viewModel,
                    onNuovoHaccp = { navController.navigate("nuovoSopralluogoHaccp/0") },
                    onNuovoSicurezza = { navController.navigate("nuovoSopralluogoSicurezza/0") },
                    onApri = { idSopralluogo, tipo ->
                        if (tipo == "SICUREZZA") navController.navigate("sopralluogoSicurezza/$idSopralluogo")
                        else navController.navigate("sopralluogoHaccp/$idSopralluogo")
                    }
                )
            }
            composable("nuovoSopralluogoHaccp/{clienteId}") { entry ->
                val id = entry.arguments?.getString("clienteId")?.toLongOrNull()?.takeIf { it > 0 }
                NuovoSopralluogoHaccpScreen(
                    viewModel = viewModel,
                    clientePreselezionato = id,
                    onCreated = { sopralluogoId ->
                        navController.navigate("sopralluogoHaccp/$sopralluogoId") {
                            popUpTo("sopralluoghi")
                        }
                    }
                )
            }
            composable("sopralluogoHaccp/{sopralluogoId}") { entry ->
                val id = entry.arguments?.getString("sopralluogoId")?.toLongOrNull() ?: return@composable
                SopralluogoHaccpScreen(id, viewModel) { navController.popBackStack() }
            }

            composable("nuovoSopralluogoSicurezza/{clienteId}") { entry ->
                val id = entry.arguments?.getString("clienteId")?.toLongOrNull()?.takeIf { it > 0 }
                NuovoSopralluogoSicurezzaScreen(
                    viewModel = viewModel,
                    clientePreselezionato = id,
                    onCreated = { sopralluogoId ->
                        navController.navigate("sopralluogoSicurezza/$sopralluogoId") {
                            popUpTo("sopralluoghi")
                        }
                    }
                )
            }
            composable("sopralluogoSicurezza/{sopralluogoId}") { entry ->
                val id = entry.arguments?.getString("sopralluogoId")?.toLongOrNull() ?: return@composable
                SopralluogoSicurezzaScreen(id, viewModel) { navController.popBackStack() }
            }

            composable("documenti") { DocumentiScreen(viewModel, onNuovo = { navController.navigate("nuovoDocumento") }) }
            composable("nuovoDocumento") { NuovoDocumentoScreen(viewModel) { navController.popBackStack() } }
            composable("servizio/{clienteId}/{tipo}") { entry ->
                val clienteId = entry.arguments?.getString("clienteId")?.toLongOrNull() ?: return@composable
                val tipo = entry.arguments?.getString("tipo") ?: return@composable
                ServizioClienteScreen(clienteId, tipo, viewModel)
            }
        }
    }
}
