package com.ksnk.predictions.base

import androidx.room.Room

import android.app.Application


class App : Application() {
    private var database: AppDataBase? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(this, AppDataBase::class.java, "database")
            .allowMainThreadQueries().build()
    }

    fun getDatabase(): AppDataBase? {
        return database
    }

    companion object {
        var instance: App? = null
    }
}