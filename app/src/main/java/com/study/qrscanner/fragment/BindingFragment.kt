package com.phntech.phnlabtrack.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.study.qrscanner.utils.backPressedHandle

abstract class BindingFragment<out T : ViewBinding> : Fragment() {
    private var _binding: ViewBinding? = null

    @Suppress("UNCHECKED_CAST")
    val binding get() = _binding as T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater(inflater)
        backPressedHandle {
            backPressedHandler()
        }
        onCreate()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        onDestroyViewHandler()
    }

    protected abstract val backPressedHandler: () -> Unit
    protected abstract val onCreate: () -> Unit
    protected abstract val onDestroyViewHandler: () -> Unit

    protected abstract val bindingInflater: (LayoutInflater) -> ViewBinding
}