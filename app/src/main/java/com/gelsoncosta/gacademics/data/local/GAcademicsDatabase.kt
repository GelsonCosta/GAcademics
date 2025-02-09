package com.gelsoncosta.gacademics.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gelsoncosta.gacademics.data.local.dao.PdfMaterialDao
import com.gelsoncosta.gacademics.data.models.PdfMaterial


@Database(entities = [PdfMaterial::class], version = 1)
abstract class GAcademicsDatabase : RoomDatabase() {
    abstract fun pdfMaterialDao(): PdfMaterialDao


    companion object {
        @Volatile
        private var INSTANCE: GAcademicsDatabase? = null

        fun getInstance(context: Context): GAcademicsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GAcademicsDatabase::class.java,
                    "academics_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
