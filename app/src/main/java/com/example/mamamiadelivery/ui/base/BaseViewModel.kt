package com.example.mamamiadelivery.ui.base

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


abstract class BaseViewModel : ViewModel() {

    private val disposables = CompositeDisposable()

    protected fun disposeOnCleared(
            single: Completable,
            onSuccess: () -> Unit,
            onError: (Throwable) -> Unit
    ) {
        disposables.add(
            single
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onSuccess()
                }, {
                    onError(it)
                })
        )
    }
    override fun onCleared() {
        super.onCleared()
        if (!disposables.isDisposed)
            disposables.dispose()
    }

}