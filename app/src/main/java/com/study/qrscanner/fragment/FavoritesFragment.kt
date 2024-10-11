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
import com.study.qrscanner.databinding.FragmentFavoritesBinding
import com.study.qrscanner.databinding.HistoryItemBinding
import com.study.qrscanner.model.QR_FORMATE
import com.study.qrscanner.model.QrDetails
import com.study.qrscanner.utils.Convertor
import com.study.qrscanner.utils.hideView
import com.study.qrscanner.viewModel.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : BindingFragment<FragmentFavoritesBinding>() {

    private val viewModel by viewModels<FavoritesViewModel>()

    override val backPressedHandler: () -> Unit
        get() = {
            findNavController().navigateUp()
        }
    override val onCreate: () -> Unit
        get() = {}
    override val onDestroyViewHandler: () -> Unit
        get() = {}
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentFavoritesBinding::inflate

    private var _favoriteAdapter: GenericAdapter<QrDetails, HistoryItemBinding>? = null

    private val favoriteAdapter get() = _favoriteAdapter!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeAdapter()

        observables()

    }

    private fun initializeAdapter() {
        _favoriteAdapter = GenericAdapter(bindingInflater = HistoryItemBinding::inflate,
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
                    addFavorite.hideView()
                    container.setOnClickListener {
                        findNavController().navigate(
                            FavoritesFragmentDirections.actionMenuFavoritesToQrDetailsFragment(
                                qrSavedData = Convertor.toJason(itemData)
                            )
                        )
                    }
                }
            })
        binding.historyRv.adapter = favoriteAdapter
    }

    private fun observables() {
        viewModel.getFavorites().observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                favoriteAdapter.setData(ArrayList(it.reversed()))
            }
        }
    }

}