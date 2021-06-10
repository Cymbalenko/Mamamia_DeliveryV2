package com.example.mamamiadelivery.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mamamiadelivery.Connections.HttpCheckAndDownloadUpdate
import com.example.mamamiadelivery.R
import com.example.mamamiadelivery.properties.AppProperties
import com.example.mamamiadelivery.ui.ConnectToServer
import com.example.mamamiadelivery.ui.settings.Setting
import java.io.File

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            Setting.btnUpdate = view?.findViewById(R.id.btn_refr) as Button
            Setting.btnUpdate?.setOnClickListener { v -> getDeliveryList(v) }
        })
        return root
    }
    fun getDeliveryList(view: View?) {

        val connectToServer = ConnectToServer()
        connectToServer.deliveryOrderList()
    }
}