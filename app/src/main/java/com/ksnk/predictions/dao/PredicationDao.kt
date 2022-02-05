package com.ksnk.predictions.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

import com.ksnk.predictions.entity.Predication
@Dao
interface PredicationDao {

    @Query("SELECT * FROM predication")
    fun getAll(): List<Predication>

    @Insert
    fun insertAll(vararg predication: Predication)

    @Delete
    fun delete(predication: Predication)

    @Query("DELETE FROM predication WHERE uid = :id")
    fun deleteById(id: Int)
}