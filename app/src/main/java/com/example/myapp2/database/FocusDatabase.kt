package com.example.myapp2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Item::class], version = 1)
abstract class FocusDatabase :RoomDatabase(){
    abstract fun itemDao() :ItemDao
    companion object{
        @Volatile
        private var INSTANCE: FocusDatabase? = null

        fun getDataBase(context: Context): FocusDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FocusDatabase::class.java, "Focus_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}