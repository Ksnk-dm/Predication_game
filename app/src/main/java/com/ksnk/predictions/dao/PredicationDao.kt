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


//    @Query("SELECT * FROM predication WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<Predication>

//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): Predication

    @Insert
    fun insertAll(vararg users: Predication)

    @Delete
    fun delete(user: Predication)

    @Query("DELETE FROM predication WHERE uid = :id")
    fun deleteById(id: Int)
}