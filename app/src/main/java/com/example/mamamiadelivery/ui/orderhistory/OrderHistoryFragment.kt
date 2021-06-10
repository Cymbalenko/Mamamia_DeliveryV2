package com.example.mamamiadelivery.ui.orderhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.example.mamamiadelivery.databinding.FragmentOrdersHistoryListBinding
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mamamiadelivery.R
import com.example.mamamiadelivery.acra.MamamiaDeliveryApplication
import com.example.mamamiadelivery.ui.base.BaseViewBindingFragment
import com.example.mamamiadelivery.ui.orderhistory.adapter.OrderHistoryAdapter
import com.example.mamamiadelivery.util.LoadingState
import com.example.mamamiadelivery.util.observeEvent

class OrderHistoryFragment : BaseViewBindingFragment<FragmentOrdersHistoryListBinding>() {

    companion object {
        fun newInstance() = OrderHistoryFragment ()
    }

    private val viewModel: OrderHistoryViewModel by viewModels()
    private val adapter = OrderHistoryAdapter ()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_orders_history_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView(view)
        viewModel.deliveryOrders.observe(viewLifecycleOwner) { orders ->
            adapter.submitList(orders)
        }
        viewModel.showEvent.observeEvent(this) { order ->
            handleUiEvent(order)
        }


    }

    private fun handleUiEvent(uiEvent: OrdersHistoryUiEvent) {
        when (uiEvent) {
            is OrdersHistoryUiEvent.DisplayLoadingState -> displayLoadingState(uiEvent.loadingState)
        }
    }
    private fun setUpRecyclerView(view: View) {
        val recyclerView = binding.recyclerViewHistory
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
    override fun initViewBinding(view: View): FragmentOrdersHistoryListBinding {
        return FragmentOrdersHistoryListBinding.bind(view)
    }
    private fun displayLoadingState(loadingState: LoadingState) {

        when (loadingState) {
            is LoadingState.Error -> {
                Toast.makeText(MamamiaDeliveryApplication.instance, loadingState.throwable.message, Toast.LENGTH_SHORT).show()
            }
            LoadingState.Loading -> {
                Toast.makeText(MamamiaDeliveryApplication.instance, "Loading", Toast.LENGTH_SHORT).show()
            }
            LoadingState.Success -> {
                Toast.makeText(MamamiaDeliveryApplication.instance, "Success", Toast.LENGTH_SHORT).show()
            }
        }
    }
}