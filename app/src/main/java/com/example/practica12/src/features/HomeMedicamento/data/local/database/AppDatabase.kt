package com.example.practica12.src.features.HomeMedicamento.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.practica12.src.features.HomeMedicamento.data.Converters
import com.example.practica12.src.features.HomeMedicamento.data.local.dao.MedicamentoDao
import com.example.practica12.src.features.HomeMedicamento.data.local.entity.MedicamentoEntity

@Database(entities = [MedicamentoEntity::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicamentoDao(): MedicamentoDao
}