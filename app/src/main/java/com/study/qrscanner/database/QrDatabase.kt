package com.study.qrscanner.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.study.qrscanner.database.dao.QrHistoryDao
import com.study.qrscanner.model.QrDetails
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(entities = [QrDetails::class], version = 1, exportSchema = false)
abstract class QrDatabase : RoomDatabase() {

    abstract fun getQrHistoryDao(): QrHistoryDao

}

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun getProductDao(quickBillDataBase: QrDatabase): QrHistoryDao {
        return quickBillDataBase.getQrHistoryDao()
    }

    @Provides
    @Singleton
    fun getQuickBillDatabase(@ApplicationContext appContext: Context): QrDatabase {
        return Room.databaseBuilder(
            appContext, QrDatabase::class.java, "QrDetails-database"
        ).build()
    }
}