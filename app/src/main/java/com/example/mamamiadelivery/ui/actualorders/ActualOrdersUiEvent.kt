package com.example.mamamiadelivery.ui.actualorders

import com.example.mamamiadelivery.util.LoadingState

sealed class ActualOrdersUiEvent {
    class DisplayLoadingState(val loadingState: LoadingState) : ActualOrdersUiEvent()
}