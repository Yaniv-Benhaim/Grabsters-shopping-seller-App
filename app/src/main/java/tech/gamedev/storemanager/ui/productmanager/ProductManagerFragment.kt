package tech.gamedev.storemanager.ui.productmanager

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_product_manager.*
import org.angmarch.views.NiceSpinner
import org.angmarch.views.OnSpinnerItemSelectedListener
import tech.gamedev.storemanager.R
import tech.gamedev.storemanager.data.constants.Constants.IMAGE_REQUEST_CODE
import tech.gamedev.storemanager.data.models.Product
import tech.gamedev.storemanager.data.repositories.FirebaseRepo
import tech.gamedev.storemanager.ui.adapters.MyProductsAdapter


class ProductManagerFragment : Fragment(R.layout.fragment_product_manager) {

    private lateinit var dashboardViewModel: ProductManagerViewModel
    private lateinit var myProductsAdapter: MyProductsAdapter
    private var addNewProductVisible = false
    private var firebaseRepo: FirebaseRepo = FirebaseRepo()
    private var productList: ArrayList<Product> = ArrayList()
    private var mStorageRef: StorageReference? = null
    private var productImage = ""
    private var typeOfProduct: Int = 1
    private var spinnerType: Int = 1
    private lateinit var serviceList: ArrayList<String>




    //ON VIEW CREATED
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        checkCurrentTypeOfProduct(typeOfProduct)

        mStorageRef = FirebaseStorage.getInstance().reference.child("product_images");

        //ADD NEW PRODUCT FAB VISIBILITY SWITCH
        fabAddProduct.setOnClickListener {
            if(!addNewProductVisible){
                addNewProductVisible = true
                svAddProductContainer.visibility = View.VISIBLE
                llAddNewProductContainer.visibility = View.VISIBLE
            }else{
                addNewProductVisible = false
                svAddProductContainer.visibility = View.GONE
                llAddNewProductContainer.visibility = View.GONE
            }
        }
        //PRODUCT TAB BACK BTN
        ivBtnBack.setOnClickListener {
            addNewProductVisible = false
            svAddProductContainer.visibility = View.GONE
            llAddNewProductContainer.visibility = View.GONE

        }
        //SAVE NEW PRODUCT
        btnSaveNewProduct.setOnClickListener {

            checkUserAndSaveNewProduct(typeOfProduct)
        }
        //SETUP PRODUCT RV
        loadProducts(1)


        //CHOOSE AND UPLOAD IMAGE
        ivChooseImage1.setOnClickListener {
            uploadImage(it)
        }

        //SET PRODUCT TYPE
        tvIsForSale.setOnClickListener {

            typeOfProduct = 1

            it.setBackgroundResource(R.drawable.product_check_bg_selected)
            tvIsForRent.setBackgroundResource(R.drawable.product_check_bg_not_selected)
            tvIsService.setBackgroundResource(R.drawable.product_check_bg_not_selected)
        }

        tvIsForRent.setOnClickListener {

            typeOfProduct = 2
            it.setBackgroundResource(R.drawable.product_check_bg_selected)
            tvIsForSale.setBackgroundResource(R.drawable.product_check_bg_not_selected)
            tvIsService.setBackgroundResource(R.drawable.product_check_bg_not_selected)

        }

        tvIsService.setOnClickListener {

            typeOfProduct = 3

            it.setBackgroundResource(R.drawable.product_check_bg_selected)
            tvIsForRent.setBackgroundResource(R.drawable.product_check_bg_not_selected)
            tvIsForSale.setBackgroundResource(R.drawable.product_check_bg_not_selected)
        }


        //SETUP SPINNER
        setupServiceArray()
        productTypeSpinner.onSpinnerItemSelectedListener = object : AdapterView.OnItemSelectedListener,
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
                when(serviceType){
                    "Products for sale" -> loadProducts(1)
                    "Products for rent" -> loadProducts(2)
                    "Services" -> loadProducts(3)
                }
            }
        }


    }

    private fun setupServiceArray(){
        //SET STORE CATEGORIES SPINNER

        serviceList = ArrayList<String>()
        serviceList.add("Products for sale")
        serviceList.add("Products for rent")
        serviceList.add("Services")

        val dataSet: List<String> = serviceList
        productTypeSpinner.attachDataSource(dataSet)

    }

    private fun checkCurrentTypeOfProduct(type: Int){

        when(type){
            1 -> {
                tvIsForSale.setBackgroundResource(R.drawable.product_check_bg_selected)
                tvIsForRent.setBackgroundResource(R.drawable.product_check_bg_not_selected)
                tvIsService.setBackgroundResource(R.drawable.product_check_bg_not_selected)
            }
            2 -> {
                tvIsForSale.setBackgroundResource(R.drawable.product_check_bg_not_selected)
                tvIsForRent.setBackgroundResource(R.drawable.product_check_bg_selected)
                tvIsService.setBackgroundResource(R.drawable.product_check_bg_not_selected)
            }
            3 -> {
                tvIsForSale.setBackgroundResource(R.drawable.product_check_bg_not_selected)
                tvIsForRent.setBackgroundResource(R.drawable.product_check_bg_not_selected)
                tvIsService.setBackgroundResource(R.drawable.product_check_bg_selected)
            }
        }
    }

    private fun uploadImage(view: View){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    private fun loadProducts(type: Int){

        if (productList.isNotEmpty()){
            productList.clear()
        }

        when(type){
            1 ->{
                firebaseRepo.getMyProductsList().addOnCompleteListener {
                    if(it.isSuccessful){
                        for (document in it.result!!){
                            val product = document.toObject(Product::class.java)
                            productList.add(product)
                        }
                        //SETUP PRODUCT RECYCLERVIEW
                        setupProductRV()
                    }else{
                        Log.d("Error", "Error: ${it.exception!!.message}")
                    }
                }

            }
            2 ->{

                firebaseRepo.getMyRentedProductsList().addOnCompleteListener {
                    if(it.isSuccessful){
                        for (document in it.result!!){
                            val product = document.toObject(Product::class.java)
                            productList.add(product)
                        }
                        //SETUP PRODUCT RECYCLERVIEW
                        setupProductRV()
                    }else{
                        Log.d("Error", "Error: ${it.exception!!.message}")
                    }
                }

            }
            3 ->{

                firebaseRepo.getMyServicesList().addOnCompleteListener {
                    if(it.isSuccessful){
                        for (document in it.result!!){
                            val product = document.toObject(Product::class.java)
                            productList.add(product)
                        }
                        //SETUP PRODUCT RECYCLERVIEW
                        setupProductRV()
                    }else{
                        Log.d("Error", "Error: ${it.exception!!.message}")
                    }
                }

            }

        }


    }

    private fun setupProductRV() = rvProductManagerList.apply {

        myProductsAdapter = MyProductsAdapter(productList, requireContext())
        adapter = myProductsAdapter

        val mLayoutManager = LinearLayoutManager(requireContext())
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager = mLayoutManager
    }



    private fun checkUserAndSaveNewProduct(type: Int){


        if (firebaseRepo.getUser() == null){
            firebaseRepo.createUser().addOnCompleteListener { it ->
                if(it.isSuccessful){
                    saveProductToDatabase(type)
                }else{
                    Log.d("Error", "Error: ${it.exception!!.message}")
                }
            }
        }else{
            //User logged
            saveProductToDatabase(type)
        }

    }

    private fun saveProductToDatabase(type: Int){




        //GETTING VALUES FROM EDIT TEXTS
        val name = etNewProductName.text.toString()
        val description = etNewProductDescription.text.toString().replace("\\n", "<br />")
        val price = etNewProductPrice.text.toString()
        val stock = etNewProductStock.text.toString()
            productImage
        val productId = etNewProductItemNumber.text.toString()
        val model = etNewProductModel.text.toString()
        val uid = firebaseRepo.getUser()?.uid


        //CREATING AN INSTANCE OF THE DATABASE
        val db = FirebaseFirestore.getInstance()
        //CREATING A NEW PRODUCT OBJECT
        val product = Product(
            name,
            description,
            price.toDouble(),
            stock.toInt(),
            productImage,
            productId,
            model,
            "",
            uid!!,
            "Gadgets"
        )

        val user = firebaseRepo.getUser()


        when(typeOfProduct){

            1 -> {
                //TELLING THE DATABASE WHERE TO UPLOAD THE PRODUCT
                db.collection("sellers")
                        .document(user!!.uid)
                        .collection("products")
                        .document(product.getProductId())
                        .set(product)

                        .addOnSuccessListener { Log.d("DATABASE", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w("DATABASE", "Error writing document", e) }

                db.collection("all_products").document(productId).set(product)

                        .addOnSuccessListener { Log.d("DATABASE", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w("DATABASE", "Error writing document", e) }

                //MAKE ADD PRODUCT VIEW INVISIBLE AND REFRESH THE ARRAY
                llAddNewProductContainer.visibility = View.GONE
                svAddProductContainer.visibility = View.GONE
                loadProducts(1)
                myProductsAdapter.dataChanged(productList)
            }

            2 -> {
                //TELLING THE DATABASE WHERE TO UPLOAD THE PRODUCT
                db.collection("renters")
                        .document(user!!.uid)
                        .collection("products")
                        .document(product.getProductId())
                        .set(product)

                        .addOnSuccessListener { Log.d("DATABASE", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w("DATABASE", "Error writing document", e) }

                db.collection("all_products_for_rent").document(productId).set(product)

                        .addOnSuccessListener { Log.d("DATABASE", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w("DATABASE", "Error writing document", e) }

                //MAKE ADD PRODUCT VIEW INVISIBLE AND REFRESH THE ARRAY
                llAddNewProductContainer.visibility = View.INVISIBLE
                loadProducts(1)
                myProductsAdapter.dataChanged(productList)

            }

            3 -> {
                //TELLING THE DATABASE WHERE TO UPLOAD THE PRODUCT
                db.collection("service_providers")
                        .document(user!!.uid)
                        .collection("services")
                        .document(product.getProductId())
                        .set(product)

                        .addOnSuccessListener { Log.d("DATABASE", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w("DATABASE", "Error writing document", e) }

                db.collection("all_services").document(productId).set(product)

                        .addOnSuccessListener { Log.d("DATABASE", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w("DATABASE", "Error writing document", e) }

                //MAKE ADD PRODUCT VIEW INVISIBLE AND REFRESH THE ARRAY
                llAddNewProductContainer.visibility = View.INVISIBLE
                loadProducts(1)
                myProductsAdapter.dataChanged(productList)

            }
        }






        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IMAGE_REQUEST_CODE){
          if(resultCode == RESULT_OK){

              val imageData: Uri? = data?.data

              val imageName: StorageReference = mStorageRef!!.child("product_img" + imageData!!.lastPathSegment)

              imageName.putFile(imageData).addOnSuccessListener {

                  imageName.downloadUrl.addOnSuccessListener {
                      productImage = it.toString()

                   Glide.with(requireContext())
                    .load(productImage)
                    .into(ivChooseImage1)
                  }
              }



          }
        }
    }
}