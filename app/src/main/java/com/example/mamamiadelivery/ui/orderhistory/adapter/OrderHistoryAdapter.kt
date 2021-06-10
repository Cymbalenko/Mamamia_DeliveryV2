package com.example.mamamiadelivery.ui.orderhistory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mamamiadelivery.R
import com.example.mamamiadelivery.model.db.entity.DeliveryOrders

class OrderHistoryAdapter () : ListAdapter<DeliveryOrders, OrderHistoryAdapter.OrdersViewHolder>(OrdersDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_orders_history_item, parent, false)
        return OrdersViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        getItem(position)?.let { order ->
            holder.bind(order)
        }
    }

    class OrdersViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(orders: DeliveryOrders) {
            val textViewAdress = itemView.findViewById<TextView>(R.id.text_view_adress_history)
            val textViewName = itemView.findViewById<TextView>(R.id.text_view_name_history)
            val textViewOrderId = itemView.findViewById<TextView>(R.id.text_view_orderdId_history)
            val textViewAPay = itemView.findViewById<TextView>(R.id.text_view_pay_history)
            if (orders.address!="")
                textViewAdress.text=orders.address;

            if (orders.name_!="")
                textViewName.text=orders.name_;
            if (orders.order_no!="")
                textViewOrderId.text=orders.order_no;
            if (orders.total_amount!="")
                textViewAPay.text=orders.total_amount;
           // buttonNoDel.setOnClickListener { setOnClickListener(orders) }

        }

    }

    class OrdersDiffUtil : DiffUtil.ItemCallback<DeliveryOrders>() {
        override fun areItemsTheSame(oldItem: DeliveryOrders, newItem: DeliveryOrders): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DeliveryOrders, newItem: DeliveryOrders): Boolean {
            return oldItem == newItem
        }
    }
}