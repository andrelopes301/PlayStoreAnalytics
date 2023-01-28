package com.ipv.playstoreanalytics.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ipv.playstoreanalytics.MainActivity
import com.ipv.playstoreanalytics.R
import com.ipv.playstoreanalytics.databinding.FragmentLoginBinding
import com.ipv.playstoreanalytics.viewmodel.MainViewModel
import com.ipv.playstoreanalytics.viewmodel.ViewModelFactory
import javax.inject.Inject


class Login : Fragment() {

    private val activity get() = context as MainActivity
    private val component get() = activity.component
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private var isCreateUserMode = false

    @Inject
    lateinit var playStoreAppsFactory: ViewModelFactory<MainViewModel>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(activity,playStoreAppsFactory)[MainViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)


        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.supportActionBar?.title = getString(R.string.profile)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.buttonCreateLogin.setOnClickListener { onLoginButtonClicked() }
        binding.buttonExistingAccount.setOnClickListener { toggleCreateUserMode() }

        checkTextChanges()

        viewModel.isLoginButton.observe(viewLifecycleOwner){ isEnabled ->
            if (isEnabled){
                binding.buttonCreateLogin.isEnabled = true
                binding.buttonCreateLogin.isClickable = true
                binding.buttonCreateLogin.alpha = 1f
            }else {
                binding.buttonCreateLogin.isEnabled = false
                binding.buttonCreateLogin.isClickable = false
                binding.buttonCreateLogin.alpha = 0.75f
            }
        }

    }

    private fun checkTextChanges() {

        binding.inputEmail.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text has been changed.
                // You can check the new text value using s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is changed.

                if ((binding.inputEmail.text?.isNotEmpty() == true &&
                            binding.inputPassword.text?.isNotEmpty() == true))
                    viewModel.isLoginButton.postValue(true)
                else
                    viewModel.isLoginButton.postValue(false)
            }
        })

        binding.inputPassword.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text has been changed.
                // You can check the new text value using s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is changed.

                if ((binding.inputEmail.text?.isNotEmpty() == true &&
                            binding.inputPassword.text?.isNotEmpty() == true))
                    viewModel.isLoginButton.postValue(true)
                else
                    viewModel.isLoginButton.postValue(false)
            }
        })
    }


    private fun onLoginButtonClicked() {
        val username = binding.inputEmail.text.toString()
        val password = binding.inputPassword.text.toString()
        if (isCreateUserMode) {
            registerUser(username, password)
        } else {
            logIn(username, password)
        }
    }


    private fun registerUser(email: String, password: String) {
        // while this operation completes, disable the button to login or create a new account
        binding.buttonCreateLogin.isEnabled = false
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")

                    Toast.makeText(activity, "Authentication succeded!",
                        Toast.LENGTH_SHORT).show()

                    goToHomeFragment()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(activity, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
                binding.buttonCreateLogin.isEnabled = true
            }
    }

    private fun logIn(email: String, password: String) {
        // while this operation completes, disable the button to login or create a new account
        binding.buttonCreateLogin.isEnabled = false

        //val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
       //     Context.MODE_PRIVATE)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    Toast.makeText(activity, "Login succeded!",
                        Toast.LENGTH_SHORT).show()

                    goToHomeFragment()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(activity, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
                binding.buttonCreateLogin.isEnabled = true
            }


    }


    private fun toggleCreateUserMode() {
        this.isCreateUserMode = !isCreateUserMode
        if (isCreateUserMode) {
            binding.buttonCreateLogin.text = getString(R.string.create_account)
            binding.buttonExistingAccount.text = getString(R.string.already_have_account)
        } else {
            binding.buttonCreateLogin.text = getString(R.string.log_in)
            binding.buttonExistingAccount.text = getString(R.string.does_not_have_account)
        }
    }

    private fun goToHomeFragment(){
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.MenuHome;
    }
}