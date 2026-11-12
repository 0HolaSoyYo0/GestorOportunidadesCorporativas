package cl.duoc.gestoroportunidadescorporativas

import android.app.Application
import cl.duoc.gestoroportunidadescorporativas.datos.local.AppDatabase
import cl.duoc.gestoroportunidadescorporativas.datos.repository.OportunidadRepository

class GestionOportunidadesApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { OportunidadRepository(database.oportunidadDao()) }
}
