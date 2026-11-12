package cl.duoc.gestoroportunidadescorporativas.ui.oportunidad

import cl.duoc.gestoroportunidadescorporativas.datos.local.OportunidadEntity

data class OportunidadUiState(
    val oportunidades: List<OportunidadEntity> = emptyList(),

    val nombre: String = "",
    val tipo: String = "",
    val descripcion: String = "",
    val fuenteInformacion: String = "",
    val prioridad: String = "",
    val potencialComercial: String = "",
    val estadoGestion: String = "Evento identificado",
    val observaciones: String = "",
    val latitud: Double? = null,
    val longitud: Double? = null,

    val nombreError: String? = null,
    val tipoError: String? = null,
    val descripcionError: String? = null,
    val fuenteError: String? = null,
    val prioridadError: String? = null,
    val potencialError: String? = null,

    val modoEdicion: Boolean = false,
    val idEdicion: Int? = null,

    val guardando: Boolean = false,
    val guardadoExitosamente: Boolean = false,

    val oportunidadAEliminar: OportunidadEntity? = null
)
