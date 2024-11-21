package com.study.qrscanner.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.phntech.phnlabtrack.fragment.BindingFragment
import com.study.qrscanner.R
import com.study.qrscanner.adapter.GenericAdapter
import com.study.qrscanner.databinding.FragmentGenerateQRBinding
import com.study.qrscanner.databinding.GenerateTextToQrBottomSheetBinding
import com.study.qrscanner.databinding.QrCategoryItemBinding
import com.study.qrscanner.model.QrCategoryData
import com.study.qrscanner.model.getQrCategoryDataList
import com.study.qrscanner.utils.setBottomSheet
import com.study.qrscanner.utils.showView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenerateQRFragment : BindingFragment<FragmentGenerateQRBinding>() {
    override val backPressedHandler: () -> Unit
        get() = {}
    override val onCreate: () -> Unit
        get() = {}
    override val onDestroyViewHandler: () -> Unit
        get() = {}
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentGenerateQRBinding::inflate

    private var _categoryAdapter: GenericAdapter<QrCategoryData, QrCategoryItemBinding>? = null

    private val categoryAdapter get() = _categoryAdapter!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeAdapter()

    }

    private fun initializeAdapter() {
        _categoryAdapter = GenericAdapter(bindingInflater = QrCategoryItemBinding::inflate,
            onBind = { itemData, itemBinding, position, listSize ->
                itemBinding.apply {
                    categoryIcon.setImageResource(itemData.icon)
                    categoryText.text = getString(itemData.text)
                    categoryContainer.setOnClickListener {
                        openSheet(itemData)
                    }
                }
            })
        binding.qrCategoryRv.adapter = categoryAdapter
        categoryAdapter.setData(getQrCategoryDataList())
    }

    private fun openSheet(categoryData: QrCategoryData) {
        setBottomSheet(
            GenerateTextToQrBottomSheetBinding::inflate,
            onBind = { sheetBinding, sheetDialog ->
                sheetBinding.apply {
                    when (categoryData.text) {
                        R.string.text -> {
                            textToQrContainer.showView()
                        }

                        R.string.url -> {
                            urlToQrContainer.showView()
                        }

                        R.string.contact -> {
                            contactToQrContainer.showView()
                        }

                        R.string.email -> {
                            emailToQrContainer.showView()
                        }

                        R.string.sms -> {
                            smsToQrContainer.showView()
                        }

                        R.string.phone -> {
                            phoneToQrContainer.showView()
                        }
                    }
                }
            })
    }

}