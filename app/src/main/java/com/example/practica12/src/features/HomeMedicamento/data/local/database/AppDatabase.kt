package com.example.practica12.src.features.HomeMedicamento.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.practica12.src.features.HomeMedicamento.data.local.dao.MedicamentoDao
import com.example.practica12.src.features.HomeMedicamento.data.local.entity.MedicamentoEntity

@Database(entities = [MedicamentoEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicamentoDao(): MedicamentoDao
}