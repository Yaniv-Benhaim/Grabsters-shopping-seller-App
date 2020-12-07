package tech.gamedev.storemanager.ui.login

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.login_fragment.*
import org.angmarch.views.NiceSpinner
import org.angmarch.views.OnSpinnerItemSelectedListener
import tech.gamedev.storemanager.MainActivity
import tech.gamedev.storemanager.R
import tech.gamedev.storemanager.data.constants.Constants.AUTH_REQUEST_CODE
import tech.gamedev.storemanager.data.models.Store
import java.util.*
import kotlin.collections.ArrayList


class LoginFragment : Fragment(R.layout.login_fragment) {

    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: LoginViewModel
    private lateinit var user: FirebaseUser

    //User Details
    private var country: String = "test"
    private var city: String = "test"
    private var fullName: String = "test"
    private var phoneNumber: String = "65345634"
    private var storeName: String = "test store"
    private var address1: String = "wertwert"
    private var address2: String = "wertwe"
    private var category: String = "dsghsgsd"




    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        auth = Firebase.auth




        return super.onCreateView(inflater, container, savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chooseCountrySpinner.onSpinnerItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            OnSpinnerItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(requireContext(), "Please choose a country", Toast.LENGTH_SHORT).show()
                return
            }

            override fun onItemSelected(
                    parent: NiceSpinner?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
                country = parent!!.getItemAtPosition(position).toString()
            }
        }

        //SETUP SPINNER DATA (Countries/Cities,Categories)
        setupSpinnerData()

        //LOGIN SETUP********************************
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        btnGoogleSignIn.setOnClickListener {
            btnGoogleSignIn.elevation = 0f
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, AUTH_REQUEST_CODE)
        }

        tvBtnLoginWithDifferentAccount.setOnClickListener {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(requireActivity()) {
                        val signInIntent = mGoogleSignInClient.signInIntent
                        startActivityForResult(signInIntent, AUTH_REQUEST_CODE)
                    }
        }


        btnCreateStore.setOnClickListener {

            addNewUserToFirestore(user)

            /*if (areFieldsNotEmpty()){
                addNewUserToFirestore(user)
            }*/

        }

        // Initialize the SDK






    }

    private fun setupSpinnerData(){

        val locales: Array<String> = Locale.getISOCountries()
        val countries: ArrayList<String> = ArrayList()
        countries.add("**Select Country**")

        for (countryCode in locales) {
            val obj = Locale("", countryCode)
            countries.add(obj.displayCountry)
        }
        countries.sort()


        Log.d("before", "BEFORE SETTING LIST")
        chooseCountrySpinner.attachDataSource(countries)
        Log.d("after", "AFTER SETTING LIST")
    }

    private fun areFieldsNotEmpty(): Boolean{

        if (etFullName.text.isEmpty()){
            Toast.makeText(requireContext(), "Please fill in your full name", Toast.LENGTH_SHORT).show()
            return false
        }else{
            fullName = etFullName.text.toString()
        }

        if (etPhoneNumber.text.isEmpty()){
            Toast.makeText(requireContext(), "Please fill in your phone number", Toast.LENGTH_SHORT).show()
            return false
        }else{
            phoneNumber = etPhoneNumber.text.toString()
        }

        if (etStoreName.text.isEmpty()){
            Toast.makeText(requireContext(), "Please fill in your store name", Toast.LENGTH_SHORT).show()
            return false
        }else{
            storeName = etStoreName.text.toString()
        }



        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTH_REQUEST_CODE) {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("LOGIN", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("LOGIN", "Google sign in failed", e)
                // ...
            }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("LOGIN", "signInWithCredential:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("LOGIN", "signInWithCredential:failure", task.exception)
                        // ...
                        Snackbar.make(requireView(), "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                        updateUI(null)
                    }

                    // ...
                }
    }


    private fun updateUI(acct: FirebaseUser?) {
        if (acct != null) {

            //CHECK IF USER EXISTS AND IF NOT ADD TO USER DATABASE
            checkIfUserExists(acct)

        } else {
            Log.d("LOGIN", "NOT LOGGED IN YET")
        }

    }

    private fun addNewUserToFirestore(acct: FirebaseUser?){

        val db = FirebaseFirestore.getInstance()

        val seller = Store(
                acct?.displayName.toString(),
                "",
                "",
                "",
                acct?.email.toString(),
                fullName,
                phoneNumber,
                storeName,
                country,
                city,
                category,
                address1,
                address2,
                acct!!.uid
        )

        db.collection("sellers").document("${acct?.uid}")
                .set(seller)
                .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                findNavController().navigate(R.id.action_loginFragment_to_navigation_home)}
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e)
                    Toast.makeText(requireContext(), "Failed to create new store, please try again later!", Toast.LENGTH_SHORT).show()}


    }

    private fun checkIfUserExists(acct: FirebaseUser?){
        val db = FirebaseFirestore.getInstance()

        db.collection("sellers").document("${acct?.uid}").get()
                .addOnCompleteListener(OnCompleteListener {
                    if (it.result?.exists()!!) {
                        findNavController().navigate(R.id.action_loginFragment_to_navigation_home)
                    } else {
                        //SHOW CREATE STORE SCREEN
                        clFirstTimeUserScreen.visibility = View.VISIBLE
                        //SET USER ACCOUNT IN GLOBAL SCOPE FOR LATER ACCESS
                        user = acct!!
                        /*addNewUserToFirestore(acct)*/
                    }
                })



    }



}