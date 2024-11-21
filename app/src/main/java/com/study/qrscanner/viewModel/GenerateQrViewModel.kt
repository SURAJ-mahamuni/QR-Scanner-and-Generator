package com.study.qrscanner.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.study.qrscanner.utils.AppEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GenerateQrViewModel @Inject constructor() : ViewModel() {

    var _appEvent: MutableLiveData<AppEvent> = MutableLiveData()

    val appEvent: LiveData<AppEvent> get() = _appEvent

    fun generateQrFromText() {

    }

    fun generateQrFromUrl() {

    }

    fun generateQrFromContact() {

    }

    fun generateQrFromEmail() {

    }

    fun generateQrFromSms() {

    }

    fun generateQrFromPhone() {

    }
}