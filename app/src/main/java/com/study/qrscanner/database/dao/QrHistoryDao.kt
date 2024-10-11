package com.study.qrscanner.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.study.qrscanner.model.QrDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface QrHistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addQrDetails(qrDetails: QrDetails)

    @Query("Select * from qr_history")
    fun getAllQrDetails(): Flow<List<QrDetails>>

    @Update
    fun updateQrDetails(qrDetails: QrDetails)

    @Query("Select * from qr_history where isFavorite = :isFavorite")
    fun getFavorites(isFavorite: Boolean): Flow<List<QrDetails>>

}