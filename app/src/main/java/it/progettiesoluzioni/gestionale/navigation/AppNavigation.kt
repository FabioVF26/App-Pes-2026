package it.progettiesoluzioni.gestionale.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.progettiesoluzioni.gestionale.ui.clienti.ClienteDettaglioScreen
import it.progettiesoluzioni.gestionale.ui.clienti.ClientiScreen
import it.progettiesoluzioni.gestionale.ui.clienti.GestionaleViewModel
import it.progettiesoluzioni.gestionale.ui.clienti.NuovoClienteScreen
import it.progettiesoluzioni.gestionale.ui.common.PlaceholderScreen
import it.progettiesoluzioni.gestionale.ui.dashboard.DashboardScreen

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
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
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
                            label = { Text(item.label) }
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
            composable("nuovoCliente") {
                NuovoClienteScreen(viewModel) { navController.popBackStack() }
            }
            composable("cliente/{clienteId}") { entry ->
                val id = entry.arguments?.getString("clienteId")?.toLongOrNull() ?: return@composable
                ClienteDettaglioScreen(id, viewModel)
            }
            composable("scadenze") { PlaceholderScreen("Scadenze", "Modulo predisposto per la prossima fase.") }
            composable("sopralluoghi") { PlaceholderScreen("Sopralluoghi", "Le checklist specifiche saranno definite per HACCP, Sicurezza e GDPR.") }
            composable("documenti") { PlaceholderScreen("Documenti", "Archivio documentale previsto nelle prossime versioni.") }
        }
    }
}
