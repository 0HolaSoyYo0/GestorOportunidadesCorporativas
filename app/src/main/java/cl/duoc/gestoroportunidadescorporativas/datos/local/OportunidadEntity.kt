package cl.duoc.gestoroportunidadescorporativas.datos.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "oportunidades")
data class OportunidadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String = "",
    val tipo: String = "",
    val descripcion: String = "",
    val fuenteInformacion: String = "",
    val prioridad: String = "",
    val potencialComercial: String = "",
    val estadoGestion: String = "Evento identificado",
    val observaciones: String = "",
    val latitud: Double? = null,
    val longitud: Double? = null
)
