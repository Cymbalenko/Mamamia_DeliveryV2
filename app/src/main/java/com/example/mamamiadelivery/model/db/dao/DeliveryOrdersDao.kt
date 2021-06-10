package com.example.mamamiadelivery.model.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mamamiadelivery.model.db.entity.DeliveryOrders
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface DeliveryOrdersDao {

    @Insert
    fun insert(deliveryOrders: DeliveryOrders): Single<Long>

    @Update
    fun update(deliveryOrders: DeliveryOrders): Completable

    @Query("DELETE FROM deliveryorders WHERE id = :id")
    fun deleteById(id: Int): Completable

    @Query("DELETE FROM deliveryorders WHERE order_no = :no")
    fun deleteByOrderId(no: String): Completable

    @Query("SELECT * FROM deliveryorders")
    fun getAllDeliveryOrders(): LiveData<List<DeliveryOrders>>

    @Query("SELECT * FROM deliveryorders where posting_date >= CURRENT_DATE and delivered=0")
    fun getDeliveryOrdersToday(): LiveData<List<DeliveryOrders>>

    @Query("SELECT * FROM deliveryorders where posting_date < CURRENT_DATE and delivered=1")
    fun getDeliveryOrdersHistory(): LiveData<List<DeliveryOrders>>

    @Query("SELECT * FROM deliveryorders WHERE id = :id")
    fun getDeliveryOrderById(id: Int): Single<List<DeliveryOrders>>

    @Query("SELECT * FROM deliveryorders WHERE order_no = :no")
    fun getDeliveryOrderByOrderId(no: String): Single<List<DeliveryOrders>>

}