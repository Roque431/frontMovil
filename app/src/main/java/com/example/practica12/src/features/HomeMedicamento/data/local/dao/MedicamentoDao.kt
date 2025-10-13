package com.example.practica12.src.features.HomeMedicamento.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.practica12.src.features.HomeMedicamento.data.local.entity.MedicamentoEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface MedicamentoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medicamento: MedicamentoEntity)

    @Query("SELECT * FROM medicamentos WHERE isSynced = 0")
    suspend fun getNoSincronizados(): List<MedicamentoEntity>

    @Update
    suspend fun actualizar(medicamento: MedicamentoEntity)

    @Query("SELECT * FROM medicamentos")
    fun getAll(): Flow<List<MedicamentoEntity>>

    @Query("DELETE FROM medicamentos WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM medicamentos WHERE id = :id")
    suspend fun getMedicamentById(id: Int): MedicamentoEntity?
}

