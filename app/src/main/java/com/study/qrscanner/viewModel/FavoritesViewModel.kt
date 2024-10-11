package com.study.qrscanner.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.study.qrscanner.database.dao.QrHistoryDao
import com.study.qrscanner.model.QrDetails
import com.study.qrscanner.utils.AppEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val qrHistoryDao: QrHistoryDao) : ViewModel() {

    var _appEvent: MutableLiveData<AppEvent> = MutableLiveData()

    val appEvent: LiveData<AppEvent> get() = _appEvent

    fun getFavorites(): LiveData<List<QrDetails>> {
        return qrHistoryDao.getFavorites(true).asLiveData()
    }

}