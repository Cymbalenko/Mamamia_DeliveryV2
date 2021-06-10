package com.example.mamamiadelivery.ui.orderhistory

import androidx.lifecycle.LiveData
import com.example.mamamiadelivery.model.db.entity.DeliveryOrders
import com.example.mamamiadelivery.model.repository.RoomRepository
import com.example.mamamiadelivery.ui.base.BaseViewModel
import com.example.mamamiadelivery.util.EventLiveData
import com.example.mamamiadelivery.util.EventMutableLiveData
import com.example.mamamiadelivery.util.LoadingState
import com.example.mamamiadelivery.util.call

class OrderHistoryViewModel : BaseViewModel() {
    private val _deliveryOrders = RoomRepository.getAllDeliveryOrders()
    private val _showEvent = EventMutableLiveData<OrdersHistoryUiEvent>()

    val deliveryOrders: LiveData<List<DeliveryOrders>> = _deliveryOrders
    val showEvent: EventLiveData<OrdersHistoryUiEvent> = _showEvent

}