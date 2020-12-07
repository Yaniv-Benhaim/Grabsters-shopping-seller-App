package tech.gamedev.storemanager.data.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import tech.gamedev.storemanager.data.models.Product

class FirebaseRepo {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getUser(): FirebaseUser?{
        return firebaseAuth.currentUser
    }

    fun getUserUid(): String? {
        return firebaseAuth.currentUser?.uid
    }

    fun createUser(): Task<AuthResult> {
        return firebaseAuth.signInAnonymously()
    }


    fun getStoresList(): Task<QuerySnapshot>{
        return firebaseFireStore.collection("sellers")
            .orderBy("name", Query.Direction.DESCENDING)
            .get()
    }

    fun getAllProductsList(): Task<QuerySnapshot>{
        return firebaseFireStore.collection("most_bought")
            .get()
    }

    fun getMyProductsList(): Task<QuerySnapshot>{
        return firebaseFireStore
                .collection("sellers")
                .document(getUserUid().toString())
                .collection("products")
                .get()
    }

    fun getMyRentedProductsList(): Task<QuerySnapshot>{
        return firebaseFireStore
            .collection("renters")
            .document(getUserUid().toString())
            .collection("products")
            .get()
    }

    fun getMyServicesList(): Task<QuerySnapshot>{
        return firebaseFireStore
            .collection("service_providers")
            .document(getUserUid().toString())
            .collection("services")
            .get()
    }

    fun getProductList(): MutableLiveData<ArrayList<Product>>{


        val productList = ArrayList<Product>()

        getAllProductsList().addOnCompleteListener{
            if(it.isSuccessful){

                for (document in it.result!!){

                    val product = document.toObject(Product::class.java)
                    productList.add(product)
                }

            }else{
                Log.d("Error", "Error: ${it.exception!!.message}")
            }
        }
        return MutableLiveData(productList)
    }
}