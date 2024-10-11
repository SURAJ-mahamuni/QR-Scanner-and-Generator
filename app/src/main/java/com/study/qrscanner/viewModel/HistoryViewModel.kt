package com.study.qrscanner.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.study.qrscanner.database.dao.QrHistoryDao
import com.study.qrscanner.model.QrDetails
import com.study.qrscanner.utils.AppEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val qrHistoryDao: QrHistoryDao) : ViewModel() {

    var _appEvent: MutableLiveData<AppEvent> = MutableLiveData()

    val appEvent: LiveData<AppEvent> get() = _appEvent

    fun getAllQrDetails(): LiveData<List<QrDetails>> {
        return qrHistoryDao.getAllQrDetails().asLiveData()
    }

    fun updateQrDetails(qrDetails: QrDetails) {
        viewModelScope.launch(Dispatchers.IO) {
            qrHistoryDao.updateQrDetails(qrDetails)
        }
    }

}