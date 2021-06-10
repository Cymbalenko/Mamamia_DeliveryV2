package com.example.mamamiadelivery.ui.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseViewBindingFragment<VB: ViewBinding> : Fragment() {
    private var _binding: VB? = null
    protected  val binding
        get() = _binding!!

    abstract fun initViewBinding(view: View):VB
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding=initViewBinding(view)
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}