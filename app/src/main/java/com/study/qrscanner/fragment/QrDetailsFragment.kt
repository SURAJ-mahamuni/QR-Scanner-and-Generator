package com.study.qrscanner.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.google.zxing.Result
import com.phntech.phnlabtrack.fragment.BindingFragment
import com.study.qrscanner.R
import com.study.qrscanner.databinding.FragmentQrDetailsBinding
import com.study.qrscanner.model.QR_FORMATE
import com.study.qrscanner.model.QrDetails
import com.study.qrscanner.utils.AppEvent
import com.study.qrscanner.utils.Convertor
import com.study.qrscanner.utils.copyToClipboard
import com.study.qrscanner.utils.showView
import com.study.qrscanner.utils.toastMsg
import com.study.qrscanner.viewModel.QrDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QrDetailsFragment : BindingFragment<FragmentQrDetailsBinding>() {

    private val args by navArgs<QrDetailsFragmentArgs>()

    private val viewModel by viewModels<QrDetailsViewModel>()

    private var data: Result? = null

    private var savedData: QrDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = Convertor.jsonToObject(args.qrData ?: "")
        data?.let {
            viewModel.setDetails(result = it)
            viewModel.isFavorite = false
        }
        savedData = Convertor.jsonToObject(args.qrSavedData ?: "")
        savedData?.let {
            viewModel.qrDetails.postValue(it)
            viewModel.isFavorite = it.isFavorite
        }

    }

    override val backPressedHandler: () -> Unit
        get() = {
            findNavController().navigateUp()
        }
    override val onCreate: () -> Unit
        get() = {
            binding.viewModel = viewModel
            binding.lifecycleOwner = this
        }
    override val onDestroyViewHandler: () -> Unit
        get() = {}
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentQrDetailsBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observables()

    }

    private fun observables() {
        viewModel.qrDetails.observe(viewLifecycleOwner) { data ->
            data?.let {
                when (it.format) {
                    QR_FORMATE.PHONE -> {
                        try {
                            binding.codeDataValue.text = it.qrData.split(":").get(1)
                            binding.call.showView()
                            binding.addContact.showView()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        binding.formatIcon.setImageResource(R.drawable.contact_format_ic)
                    }

                    QR_FORMATE.MAIL -> {
                        try {
                            val mail = it.qrData.split("?")[0]
                            val subject = it.qrData.split("?")[1].split("&")[0]
                            val body = it.qrData.split("&")[1]
                            val formatValue = "${mail}\n${subject}\n${body}"
                            binding.codeDataValue.text = formatValue
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        binding.openMail.showView()
                        binding.formatIcon.setImageResource(R.drawable.email_format_ic)

                    }

                    QR_FORMATE.SMS -> {
                        try {
                            it.qrData.split(":").let {
                                val formatValue = "phone number : ${it[1]}\nmessage : ${it[2]}"
                                binding.codeDataValue.text = formatValue
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        binding.formatIcon.setImageResource(R.drawable.sms_format_ic)
                    }

                    QR_FORMATE.CONTACT -> {
                        binding.formatIcon.setImageResource(R.drawable.contact_format_ic)
                    }

                    QR_FORMATE.URL -> {
                        binding.openUrl.showView()
                    }

                    else -> {
                        binding.formatIcon.setImageResource(R.drawable.text_format_ic)
                    }
                }
                if (it.isFavorite) {
                    binding.favorite.setImageResource(R.drawable.star_fill_ic)
                } else {
                    binding.favorite.setImageResource(R.drawable.star_unfill_ic)
                }
            }
        }
        viewModel.appEvent.observe(viewLifecycleOwner) {
            when (it) {
                is AppEvent.Other -> {
                    when (it.message) {
                        "Favorite" -> {
                            if (viewModel.isFavorite) {
                                viewModel.isFavorite = false
                                binding.favorite.setImageResource(R.drawable.star_unfill_ic)
                            } else {
                                viewModel.isFavorite = true
                                binding.favorite.setImageResource(R.drawable.star_fill_ic)
                            }
                            viewModel.updateQrDetails()
                        }

                        "Share" -> {
                            shareData()
                        }

                        "Copy" -> {
                            requireContext().copyToClipboard(
                                viewModel.qrDetails.value?.format?.name ?: "",
                                viewModel.qrDetails.value?.qrData ?: ""
                            )
                            toastMsg("Copy")
                        }

                        "SaveContact" -> {
                            importContact(viewModel.qrDetails.value?.qrData ?: "")
                        }

                        "MakeCall" -> {
                            makeCall((viewModel.qrDetails.value?.qrData ?: ""))
                        }

                        "OpenUrl" -> {
                            openUrl()
                        }

                        "OpenMail" -> {
                            try {
                                val email =
                                    viewModel.qrDetails.value?.qrData?.split("?")?.get(0)
                                        ?.split(":")
                                        ?.get(1)
                                val subject =
                                    viewModel.qrDetails.value?.qrData?.split("?")?.get(1)
                                        ?.split("&")
                                        ?.get(0)?.split("=")?.get(1)
                                val body =
                                    viewModel.qrDetails.value?.qrData?.split("&")?.get(0)
                                        ?.split("=")
                                        ?.get(1)
                                openMail(
                                    email = email ?: "", subject = subject ?: "", body = body ?: ""
                                )
                            } catch (e: Exception) {
                                toastMsg("Can't open!")
                            }

                        }
                    }
                }

                else -> {}
            }
        }
    }

    private fun openMail(email: String, subject: String, body: String) {
        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        val urlString =
            "mailto:" + Uri.encode(email) + "?subject=" + Uri.encode(subject) + "&body=" + Uri.encode(
                body
            )
        selectorIntent.data = Uri.parse(urlString)

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, body)
        emailIntent.selector = selectorIntent

        startActivity(Intent.createChooser(emailIntent, "Send email"))
    }

    private fun openUrl() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.qrDetails.value?.qrData))
        startActivity(browserIntent)
    }

    private fun importContact(number: String) {
        val intent = Intent(ContactsContract.Intents.Insert.ACTION)
        intent.type = ContactsContract.RawContacts.CONTENT_TYPE
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, number.replace("tel:", ""))
        startActivity(intent)
    }

    private fun makeCall(traPhone: String) {
        if (traPhone != "") {
            val callIntent = Intent(Intent.ACTION_CALL, Uri.parse(traPhone))
            startActivity(callIntent)
        } else {
            toastMsg("Contact number not found!")
        }
    }

    private fun shareData() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, viewModel.qrDetails.value?.qrData);
        startActivity(
            Intent.createChooser(
                shareIntent, viewModel.qrDetails.value?.format?.name ?: ""
            )
        )
    }

}