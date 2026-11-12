package cl.duoc.gestoroportunidadescorporativas.datos.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OportunidadDao {

    @Query("SELECT * FROM oportunidades ORDER BY id DESC")
    fun getAllOportunidades(): Flow<List<OportunidadEntity>>

    @Query("SELECT * FROM oportunidades WHERE id = :id")
    suspend fun getOportunidadById(id: Int): OportunidadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(oportunidad: OportunidadEntity): Long

    @Update
    suspend fun update(oportunidad: OportunidadEntity)

    @Delete
    suspend fun delete(oportunidad: OportunidadEntity)
}
