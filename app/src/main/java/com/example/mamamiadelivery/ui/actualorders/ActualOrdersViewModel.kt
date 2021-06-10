package com.example.mamamiadelivery.ui.actualorders

import android.preference.PreferenceManager
import androidx.lifecycle.LiveData
import com.example.mamamiadelivery.acra.MamamiaDeliveryApplication
import com.example.mamamiadelivery.model.db.entity.DeliveryOrders
import com.example.mamamiadelivery.model.repository.RoomRepository
import com.example.mamamiadelivery.ui.ConnectToServer
import com.example.mamamiadelivery.ui.base.BaseViewModel
import com.example.mamamiadelivery.util.EventLiveData
import com.example.mamamiadelivery.util.EventMutableLiveData
import com.example.mamamiadelivery.util.LoadingState
import com.example.mamamiadelivery.util.call
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class ActualOrdersViewModel : BaseViewModel() {

    private val _deliveryOrders = RoomRepository.getAllDeliveryOrders()
    private val _showEvent = EventMutableLiveData<ActualOrdersUiEvent>()

    val deliveryOrders: LiveData<List<DeliveryOrders>> = _deliveryOrders
    val showEvent: EventLiveData<ActualOrdersUiEvent> = _showEvent
    fun deliveredOrder(order: DeliveryOrders) {
        /*_showEvent.call(ActualOrdersUiEvent.DisplayLoadingState(LoadingState.Loading))
        disposeOnCleared(
                RoomRepository.update(order),
                {
                    _showEvent.call(ActualOrdersUiEvent.DisplayLoadingState(LoadingState.Success))
                }) {
            _showEvent.call(ActualOrdersUiEvent.DisplayLoadingState(LoadingState.Error(it)))
        }
*/
    }
     fun   loadDeliveryList() {
        val connectToServer = ConnectToServer()
        connectToServer.deliveryOrderList()
    }

}