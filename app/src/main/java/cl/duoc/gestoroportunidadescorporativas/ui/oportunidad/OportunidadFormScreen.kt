package cl.duoc.gestoroportunidadescorporativas.ui.oportunidad

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OportunidadFormScreen(
    viewModel: OportunidadViewModel,
    onVolverALista: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val tipos = listOf("Evento corporativo", "Activación de marca", "Feria o exposición", "Lanzamiento de producto", "Otro")
    val prioridades = listOf("Alta", "Media", "Baja")
    val potenciales = listOf("Alto", "Medio", "Bajo")

    var expandedTipo by remember { mutableStateOf(false) }

    @SuppressLint("MissingPermission")
    fun obtenerUbicacionDispositivo() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    viewModel.onUbicacionObtenida(loc.latitude, loc.longitude)
                } else {
                    viewModel.onUbicacionObtenida(-33.4489, -70.6693)
                }
            }.addOnFailureListener {
                viewModel.onUbicacionObtenida(-33.4489, -70.6693)
            }
        } else {
            viewModel.onUbicacionObtenida(-33.4489, -70.6693)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        obtenerUbicacionDispositivo()
    }

    LaunchedEffect(uiState.guardadoExitosamente) {
        if (uiState.guardadoExitosamente) {
            viewModel.resetGuardadoExitosamente()
            onVolverALista()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.modoEdicion) "Editar Oportunidad" else "Nueva Oportunidad") },
                navigationIcon = {
                    IconButton(onClick = onVolverALista) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre de la Oportunidad *") },
                isError = uiState.nombreError != null,
                supportingText = uiState.nombreError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = expandedTipo,
                onExpandedChange = { expandedTipo = !expandedTipo },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.tipo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de Oportunidad *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                    isError = uiState.tipoError != null,
                    supportingText = uiState.tipoError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedTipo,
                    onDismissRequest = { expandedTipo = false }
                ) {
                    tipos.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                viewModel.onTipoChange(item)
                                expandedTipo = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.descripcion,
                onValueChange = viewModel::onDescripcionChange,
                label = { Text("Descripción *") },
                isError = uiState.descripcionError != null,
                supportingText = uiState.descripcionError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = uiState.fuenteInformacion,
                onValueChange = viewModel::onFuenteChange,
                label = { Text("Fuente de Información *") },
                isError = uiState.fuenteError != null,
                supportingText = uiState.fuenteError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Column {
                Text(
                    text = "Prioridad *",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    prioridades.forEach { item ->
                        FilterChip(
                            selected = uiState.prioridad == item,
                            onClick = { viewModel.onPrioridadChange(item) },
                            label = { Text(item) }
                        )
                    }
                }
                uiState.prioridadError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            Column {
                Text(
                    text = "Potencial Comercial *",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    potenciales.forEach { item ->
                        FilterChip(
                            selected = uiState.potencialComercial == item,
                            onClick = { viewModel.onPotencialChange(item) },
                            label = { Text(item) }
                        )
                    }
                }
                uiState.potencialError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            OutlinedTextField(
                value = uiState.observaciones,
                onValueChange = viewModel::onObservacionesChange,
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Recurso Nativo: Ubicación del Dispositivo",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(
                    onClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Obtener Ubicación Actual")
                }
                uiState.latitud?.let { lat ->
                    uiState.longitud?.let { lng ->
                        Text(
                            text = "GPS: Lat $lat, Lng $lng",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.guardarOportunidad() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.guardando
            ) {
                Text(if (uiState.modoEdicion) "Actualizar Oportunidad" else "Guardar Oportunidad")
            }
        }
    }
}
