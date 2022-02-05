package com.ksnk.predictions.base

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ksnk.predictions.dao.PredicationDao
import com.ksnk.predictions.entity.Predication

@Database(entities = [Predication::class], version = 1)
abstract class AppDataBase: RoomDatabase() {
    abstract fun predicationDao(): PredicationDao
}