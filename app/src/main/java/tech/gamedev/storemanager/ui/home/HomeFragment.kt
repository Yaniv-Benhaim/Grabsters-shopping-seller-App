package tech.gamedev.storemanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_home.*
import org.angmarch.views.NiceSpinner
import org.angmarch.views.OnSpinnerItemSelectedListener
import tech.gamedev.storemanager.R
import tech.gamedev.storemanager.data.repositories.FirebaseRepo

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var firebaseRepo = FirebaseRepo()
    private lateinit var serviceList: ArrayList<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabEnterAdminScreen.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_adminFragment)
        }

        //SETUP SERVICE ARRAY
        setupServiceArray()

        nice_spinner.onSpinnerItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            OnSpinnerItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val category = parent?.getItemAtPosition(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }

            override fun onItemSelected(
                parent: NiceSpinner?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var serviceType: String = parent!!.getItemAtPosition(position).toString()
                Toast.makeText(requireContext(), serviceType, Toast.LENGTH_SHORT).show()
            }
        }




    }




    private fun setupServiceArray(){
        //SET STORE CATEGORIES SPINNER

        serviceList = ArrayList<String>()
        serviceList.add("Incoming Orders")
        serviceList.add("Outgoing Orders")
        serviceList.add("Finished Orders")

        val dataSet: List<String> = serviceList
        nice_spinner.attachDataSource(dataSet)

    }


}