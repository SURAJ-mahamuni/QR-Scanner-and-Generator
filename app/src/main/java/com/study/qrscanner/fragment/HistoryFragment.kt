package com.study.qrscanner.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.phntech.phnlabtrack.fragment.BindingFragment
import com.study.qrscanner.adapter.GenericAdapter
import com.study.qrscanner.R
import com.study.qrscanner.databinding.FragmentHistoryBinding
import com.study.qrscanner.databinding.HistoryItemBinding
import com.study.qrscanner.model.QR_FORMATE
import com.study.qrscanner.model.QrDetails
import com.study.qrscanner.utils.Convertor
import com.study.qrscanner.viewModel.HistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class HistoryFragment : BindingFragment<FragmentHistoryBinding>() {

    private val viewModel by viewModels<HistoryViewModel>()

    override val backPressedHandler: () -> Unit
        get() = {}
    override val onCreate: () -> Unit
        get() = {}
    override val onDestroyViewHandler: () -> Unit
        get() = {}
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentHistoryBinding::inflate

    private var _historyAdapter: GenericAdapter<QrDetails, HistoryItemBinding>? = null

    private val historyAdapter get() = _historyAdapter!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeAdapter()

        observables()

    }

    private fun initializeAdapter() {
        _historyAdapter = GenericAdapter(bindingInflater = HistoryItemBinding::inflate,
            onBind = { itemData, itemBinding, position, listSize ->
                itemBinding.apply {
                    when (itemData.format) {
                        QR_FORMATE.SMS -> {
                            formatIcon.setImageResource(R.drawable.sms_format_ic)
                        }

                        QR_FORMATE.URL -> {
                            formatIcon.setImageResource(R.drawable.url_format_ic)
                        }

                        QR_FORMATE.CONTACT -> {
                            formatIcon.setImageResource(R.drawable.contact_format_ic)
                        }

                        QR_FORMATE.MAIL -> {
                            formatIcon.setImageResource(R.drawable.email_format_ic)
                        }

                        QR_FORMATE.PHONE -> {
                            formatIcon.setImageResource(R.drawable.contact_format_ic)
                        }

                        QR_FORMATE.TEXT -> {
                            formatIcon.setImageResource(R.drawable.text_format_ic)
                        }
                    }
                    format.text = itemData.format.name
                    data.text = itemData.qrData
                    date.text = itemData.date.replace("\n", " ")
                    if (itemData.isFavorite) {
                        addFavorite.setImageResource(R.drawable.star_fill_ic)
                    } else {
                        addFavorite.setImageResource(R.drawable.star_unfill_ic)
                    }
                    addFavorite.setOnClickListener {
                        itemData.isFavorite = !itemData.isFavorite
                        viewModel.updateQrDetails(itemData)
                        historyAdapter.notifyItemChanged(position)
                    }
                    container.setOnClickListener {
                        findNavController().navigate(
                            HistoryFragmentDirections.actionGlobalQrDetailsFragment2(
                                qrSavedData = Convertor.toJason(itemData)
                            )
                        )
                    }
                }
            })
        binding.historyRv.adapter = historyAdapter
    }

    private fun observables() {
        viewModel.getAllQrDetails().observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                historyAdapter.setData(ArrayList(it.reversed()))
            }
        }
    }

}