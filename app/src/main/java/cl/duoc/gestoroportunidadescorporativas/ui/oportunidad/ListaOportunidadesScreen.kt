package cl.duoc.gestoroportunidadescorporativas.ui.oportunidad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.gestoroportunidadescorporativas.datos.local.OportunidadEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaOportunidadesScreen(
    viewModel: OportunidadViewModel,
    onCrearNueva: () -> Unit,
    onEditar: (OportunidadEntity) -> Unit,
    onVolverAlMenu: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Oportunidades Registradas") },
                navigationIcon = {
                    IconButton(onClick = onVolverAlMenu) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.limpiarFormulario()
                    onCrearNueva()
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Oportunidad")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.oportunidades.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No hay oportunidades registradas.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Presiona el botón '+' para agregar una nueva oportunidad comercial.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.oportunidades, key = { it.id }) { item ->
                        OportunidadItemCard(
                            oportunidad = item,
                            onEditar = {
                                viewModel.cargarParaEditar(item)
                                onEditar(item)
                            },
                            onEliminar = {
                                viewModel.solicitarEliminacion(item)
                            }
                        )
                    }
                }
            }

            uiState.oportunidadAEliminar?.let { oportunidad ->
                AlertDialog(
                    onDismissRequest = { viewModel.cancelarEliminacion() },
                    title = { Text("Confirmar Eliminación") },
                    text = { Text("¿Deseas eliminar la oportunidad '${oportunidad.nombre}'?") },
                    confirmButton = {
                        TextButton(onClick = { viewModel.confirmarEliminacion() }) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.cancelarEliminacion() }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun OportunidadItemCard(
    oportunidad: OportunidadEntity,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = oportunidad.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                PrioridadBadge(prioridad = oportunidad.prioridad)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tipo: ${oportunidad.tipo}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Estado: ${oportunidad.estadoGestion}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun PrioridadBadge(prioridad: String) {
    val (bgColor, textColor) = when (prioridad.lowercase()) {
        "alta" -> Color(0xFFFFCDD2) to Color(0xFFB71C1C)
        "media" -> Color(0xFFFFE0B2) to Color(0xFFE65100)
        else -> Color(0xFFC8E6C9) to Color(0xFF1B5E20)
    }

    Box(
        modifier = Modifier
            .background(bgColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = prioridad.ifEmpty { "Normal" },
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}
