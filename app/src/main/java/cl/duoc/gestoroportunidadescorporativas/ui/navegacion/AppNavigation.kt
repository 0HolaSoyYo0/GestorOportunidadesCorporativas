package cl.duoc.gestoroportunidadescorporativas.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.gestoroportunidadescorporativas.GestionOportunidadesApp
import cl.duoc.gestoroportunidadescorporativas.ui.login.LoginScreen
import cl.duoc.gestoroportunidadescorporativas.ui.menu.MenuScreen
import cl.duoc.gestoroportunidadescorporativas.ui.oportunidad.ListaOportunidadesScreen
import cl.duoc.gestoroportunidadescorporativas.ui.oportunidad.OportunidadFormScreen
import cl.duoc.gestoroportunidadescorporativas.ui.oportunidad.OportunidadViewModel

object Rutas {
    const val LOGIN = "login"
    const val MENU = "menu"
    const val LISTA = "lista"
    const val FORMULARIO = "formulario"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current.applicationContext as GestionOportunidadesApp
    val repository = context.repository
    val viewModel: OportunidadViewModel = viewModel(
        factory = OportunidadViewModel.Factory(repository)
    )

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Rutas.MENU) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.MENU) {
            MenuScreen(
                onNavegarAOportunidades = {
                    navController.navigate(Rutas.LISTA)
                },
                onCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.MENU) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.LISTA) {
            ListaOportunidadesScreen(
                viewModel = viewModel,
                onCrearNueva = {
                    navController.navigate(Rutas.FORMULARIO)
                },
                onEditar = {
                    navController.navigate(Rutas.FORMULARIO)
                },
                onVolverAlMenu = {
                    navController.navigate(Rutas.MENU)
                }
            )
        }

        composable(Rutas.FORMULARIO) {
            OportunidadFormScreen(
                viewModel = viewModel,
                onVolverALista = {
                    navController.popBackStack()
                }
            )
        }
    }
}
