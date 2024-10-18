package com.study.qrscanner.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.phntech.phnlabtrack.fragment.BindingFragment
import com.study.qrscanner.utils.Convertor
import com.study.qrscanner.databinding.FragmentHomeBinding
import com.study.qrscanner.utils.hideView
import com.study.qrscanner.utils.showView
import com.study.qrscanner.utils.toastMsg
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BindingFragment<FragmentHomeBinding>() {

    var codeScanner: CodeScanner? = null

    override val backPressedHandler: () -> Unit
        get() = {}
    override val onCreate: () -> Unit
        get() = {}
    override val onDestroyViewHandler: () -> Unit
        get() = {}
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentHomeBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeScanner()

    }

    private fun initializeScanner() {
        val codeScanner = CodeScanner(requireActivity(), binding.scannerView)
        codeScanner.apply {
            // Parameters (default values)
            camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
            formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
            // ex. listOf(BarcodeFormat.QR_CODE)
            autoFocusMode = AutoFocusMode.CONTINUOUS // or CONTINUOUS
            scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
            isAutoFocusEnabled = true // Whether to enable auto focus or not
            isFlashEnabled = false // Whether to enable flash or not

            isTouchFocusEnabled = true

            // Callbacks
            decodeCallback = DecodeCallback {
                Log.e("result", it.toString())
                lifecycleScope.launch(Dispatchers.Main) {
                    if (it.text.isNullOrEmpty()) {
                        toastMsg("Code data is empty!")
                        binding.scanAgain.showView()
                    } else {
                        findNavController().navigate(
                            HomeFragmentDirections.actionGlobalQrDetailsFragment(
                                Convertor.toJason(it)
                            )
                        )
                    }
                }
            }
            errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
                it.printStackTrace()
                lifecycleScope.launch(Dispatchers.Main) {
                    toastMsg("Qr Code is not contain any data!")
                    binding.scanAgain.showView()
                }
            }
            codeScanner.startPreview()
        }
        binding.scanAgain.setOnClickListener {
            codeScanner.startPreview()
            it.hideView()
        }
    }

    override fun onStart() {
        super.onStart()
        codeScanner?.startPreview()
    }


    override fun onPause() {
        codeScanner?.releaseResources()
        super.onPause()
    }

}