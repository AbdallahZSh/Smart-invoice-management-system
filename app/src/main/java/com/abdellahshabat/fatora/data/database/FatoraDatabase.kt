package com.abdellahshabat.fatora.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abdellahshabat.fatora.data.database.converter.Converters
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
@TypeConverters(Converters::class)

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
                    "fatora_database"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}