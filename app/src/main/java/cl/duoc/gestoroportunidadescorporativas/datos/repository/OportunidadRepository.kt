package cl.duoc.gestoroportunidadescorporativas.datos.repository

import cl.duoc.gestoroportunidadescorporativas.datos.local.OportunidadDao
import cl.duoc.gestoroportunidadescorporativas.datos.local.OportunidadEntity
import kotlinx.coroutines.flow.Flow

class OportunidadRepository(private val dao: OportunidadDao) {

    val todasLasOportunidades: Flow<List<OportunidadEntity>> = dao.getAllOportunidades()

    suspend fun obtenerPorId(id: Int): OportunidadEntity? = dao.getOportunidadById(id)

    suspend fun insertar(oportunidad: OportunidadEntity): Long = dao.insert(oportunidad)

    suspend fun actualizar(oportunidad: OportunidadEntity) = dao.update(oportunidad)

    suspend fun eliminar(oportunidad: OportunidadEntity) = dao.delete(oportunidad)
}
