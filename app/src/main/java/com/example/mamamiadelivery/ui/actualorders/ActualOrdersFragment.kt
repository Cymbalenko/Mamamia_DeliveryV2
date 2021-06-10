package com.example.mamamiadelivery.ui.actualorders

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.mamamiadelivery.R
import com.example.mamamiadelivery.acra.MamamiaDeliveryApplication
import com.example.mamamiadelivery.databinding.FragmentActualOrdersListBinding
import com.example.mamamiadelivery.ui.actualorders.adapter.ActualOrdersAdapter
import com.example.mamamiadelivery.ui.base.BaseViewBindingFragment
import com.example.mamamiadelivery.util.LoadingState
import com.example.mamamiadelivery.util.observeEvent

/**
 * A fragment representing a list of Items.
 */
class ActualOrdersFragment : BaseViewBindingFragment<FragmentActualOrdersListBinding>() {

    companion object {
        fun newInstance() = ActualOrdersFragment()
    }

    private val viewModel: ActualOrdersViewModel by viewModels()
    private lateinit var progressBar: ProgressBar
    private val adapter = ActualOrdersAdapter { _ ->

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_actual_orders_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView(view)
        initViews()
        viewModel.deliveryOrders.observe(viewLifecycleOwner) { orders ->
            adapter.submitList(orders)
        }
        viewModel.showEvent.observeEvent(this) { actualOrdersUiEvent ->
            handleUiEvent(actualOrdersUiEvent)
        }
        binding.searchButton.setOnClickListener {
            viewModel.loadDeliveryList()
        }

    }

    private fun handleUiEvent(uiEvent: ActualOrdersUiEvent) {
        when (uiEvent) {
            is ActualOrdersUiEvent.DisplayLoadingState -> displayLoadingState(uiEvent.loadingState)
        }
    }
    private fun setUpRecyclerView(view: View) {
        val recyclerView = binding.recyclerViewActual
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
    override fun initViewBinding(view: View): FragmentActualOrdersListBinding {
        return FragmentActualOrdersListBinding.bind(view)
    }
    private fun initViews() {
        progressBar = binding.progress
    }
    private fun displayLoadingState(loadingState: LoadingState) {

        when (loadingState) {
            is LoadingState.Error -> {
                progressBar.isVisible = false
                Toast.makeText(MamamiaDeliveryApplication.instance, loadingState.throwable.message, Toast.LENGTH_SHORT).show()
            }
            LoadingState.Loading -> {
                progressBar.isVisible = true
                Toast.makeText(MamamiaDeliveryApplication.instance, "Loading", Toast.LENGTH_SHORT).show()
            }
            LoadingState.Success -> {
                progressBar.isVisible = false
            Toast.makeText(MamamiaDeliveryApplication.instance, "Success", Toast.LENGTH_SHORT).show()
        }
        }
    }

}