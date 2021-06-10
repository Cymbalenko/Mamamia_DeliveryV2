package com.example.mamamiadelivery.ui.orderhistory

import com.example.mamamiadelivery.util.LoadingState

sealed class OrdersHistoryUiEvent {
    class DisplayLoadingState(val loadingState: LoadingState) : OrdersHistoryUiEvent()
}