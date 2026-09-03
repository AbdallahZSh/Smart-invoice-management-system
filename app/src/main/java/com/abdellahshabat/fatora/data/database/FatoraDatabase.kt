package com.abdellahshabat.fatora.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.abdellahshabat.fatora.data.database.dao.CustomerDao
import com.abdellahshabat.fatora.data.database.dao.TransactionDao
import com.abdellahshabat.fatora.data.database.entity.Customer
import com.abdellahshabat.fatora.data.database.entity.Transaction

@Database(
    entities = [
        Customer::class,
        Transaction::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FatoraDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao

    abstract fun transactionDao(): TransactionDao

    companion object {

        @Volatile
        private var INSTANCE: FatoraDatabase? = null

        fun getInstance(context: Context): FatoraDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FatoraDatabase::class.java,
                    "shopbook_database"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}