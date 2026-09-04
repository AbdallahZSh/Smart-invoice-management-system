package com.abdellahshabat.fatora.data.database.converter

import androidx.room.TypeConverter
import com.abdellahshabat.fatora.data.database.entity.TransactionType

class Converters {

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }
}