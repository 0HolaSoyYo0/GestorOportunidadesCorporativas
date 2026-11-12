package cl.duoc.gestoroportunidadescorporativas.ui.oportunidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestoroportunidadescorporativas.datos.local.OportunidadEntity
import cl.duoc.gestoroportunidadescorporativas.datos.repository.OportunidadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OportunidadViewModel(
    private val repository: OportunidadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OportunidadUiState())
    val uiState: StateFlow<OportunidadUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.todasLasOportunidades.collect { lista ->
                _uiState.update { it.copy(oportunidades = lista) }
            }
        }
    }

    fun onNombreChange(valor: String) {
        _uiState.update { it.copy(nombre = valor, nombreError = null) }
    }

    fun onTipoChange(valor: String) {
        _uiState.update { it.copy(tipo = valor, tipoError = null) }
    }

    fun onDescripcionChange(valor: String) {
        _uiState.update { it.copy(descripcion = valor, descripcionError = null) }
    }

    fun onFuenteChange(valor: String) {
        _uiState.update { it.copy(fuenteInformacion = valor, fuenteError = null) }
    }

    fun onPrioridadChange(valor: String) {
        _uiState.update { it.copy(prioridad = valor, prioridadError = null) }
    }

    fun onPotencialChange(valor: String) {
        _uiState.update { it.copy(potencialComercial = valor, potencialError = null) }
    }

    fun onObservacionesChange(valor: String) {
        _uiState.update { it.copy(observaciones = valor) }
    }

    fun onUbicacionObtenida(lat: Double, lng: Double) {
        _uiState.update { it.copy(latitud = lat, longitud = lng) }
    }

    private fun validarFormulario(): Boolean {
        var esValido = true
        val state = _uiState.value

        var nomErr: String? = null
        var tipErr: String? = null
        var desErr: String? = null
        var fueErr: String? = null
        var priErr: String? = null
        var potErr: String? = null

        if (state.nombre.isBlank()) {
            nomErr = "El nombre de la oportunidad es obligatorio"
            esValido = false
        }
        if (state.tipo.isBlank()) {
            tipErr = "Seleccione un tipo de oportunidad"
            esValido = false
        }
        if (state.descripcion.isBlank()) {
            desErr = "La descripción es obligatoria"
            esValido = false
        }
        if (state.fuenteInformacion.isBlank()) {
            fueErr = "Ingrese la fuente de información"
            esValido = false
        }
        if (state.prioridad.isBlank()) {
            priErr = "Seleccione el nivel de prioridad"
            esValido = false
        }
        if (state.potencialComercial.isBlank()) {
            potErr = "Seleccione el potencial comercial"
            esValido = false
        }

        _uiState.update {
            it.copy(
                nombreError = nomErr,
                tipoError = tipErr,
                descripcionError = desErr,
                fuenteError = fueErr,
                prioridadError = priErr,
                potencialError = potErr
            )
        }

        return esValido
    }

    fun guardarOportunidad() {
        if (!validarFormulario()) return

        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true) }

            val entidad = OportunidadEntity(
                id = state.idEdicion ?: 0,
                nombre = state.nombre.trim(),
                tipo = state.tipo,
                descripcion = state.descripcion.trim(),
                fuenteInformacion = state.fuenteInformacion.trim(),
                prioridad = state.prioridad,
                potencialComercial = state.potencialComercial,
                estadoGestion = state.estadoGestion,
                observaciones = state.observaciones.trim(),
                latitud = state.latitud,
                longitud = state.longitud
            )

            if (state.modoEdicion && state.idEdicion != null) {
                repository.actualizar(entidad)
            } else {
                repository.insertar(entidad)
            }

            limpiarFormulario()
            _uiState.update { it.copy(guardando = false, guardadoExitosamente = true) }
        }
    }

    fun cargarParaEditar(oportunidad: OportunidadEntity) {
        _uiState.update {
            it.copy(
                nombre = oportunidad.nombre,
                tipo = oportunidad.tipo,
                descripcion = oportunidad.descripcion,
                fuenteInformacion = oportunidad.fuenteInformacion,
                prioridad = oportunidad.prioridad,
                potencialComercial = oportunidad.potencialComercial,
                estadoGestion = oportunidad.estadoGestion,
                observaciones = oportunidad.observaciones,
                latitud = oportunidad.latitud,
                longitud = oportunidad.longitud,
                modoEdicion = true,
                idEdicion = oportunidad.id,
                nombreError = null,
                tipoError = null,
                descripcionError = null,
                fuenteError = null,
                prioridadError = null,
                potencialError = null
            )
        }
    }

    fun solicitarEliminacion(oportunidad: OportunidadEntity) {
        _uiState.update { it.copy(oportunidadAEliminar = oportunidad) }
    }

    fun cancelarEliminacion() {
        _uiState.update { it.copy(oportunidadAEliminar = null) }
    }

    fun confirmarEliminacion() {
        val oportunidad = _uiState.value.oportunidadAEliminar ?: return
        viewModelScope.launch {
            repository.eliminar(oportunidad)
            _uiState.update { it.copy(oportunidadAEliminar = null) }
        }
    }

    fun limpiarFormulario() {
        _uiState.update {
            it.copy(
                nombre = "",
                tipo = "",
                descripcion = "",
                fuenteInformacion = "",
                prioridad = "",
                potencialComercial = "",
                estadoGestion = "Evento identificado",
                observaciones = "",
                latitud = null,
                longitud = null,
                modoEdicion = false,
                idEdicion = null,
                nombreError = null,
                tipoError = null,
                descripcionError = null,
                fuenteError = null,
                prioridadError = null,
                potencialError = null
            )
        }
    }

    fun resetGuardadoExitosamente() {
        _uiState.update { it.copy(guardadoExitosamente = false) }
    }

    class Factory(private val repository: OportunidadRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OportunidadViewModel::class.java)) {
                return OportunidadViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel Class Desconocida")
        }
    }
}
