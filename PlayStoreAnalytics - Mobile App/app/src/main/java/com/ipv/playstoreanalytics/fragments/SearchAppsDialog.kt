package com.ipv.playstoreanalytics.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.shape.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ipv.playstoreanalytics.R
import com.ipv.playstoreanalytics.adapters.SearchListAppsAdapter
import com.ipv.playstoreanalytics.databinding.FragmentSearchAppsDialogBinding
import com.ipv.playstoreanalytics.models.PlayStoreAppModel
import com.ipv.playstoreanalytics.utils.Popover
import com.ipv.playstoreanalytics.utils.closeKeyboardForce
import com.ipv.playstoreanalytics.viewmodel.MainViewModel
import com.ipv.playstoreanalytics.viewmodel.ViewModelFactory
import java.util.*
import javax.inject.Inject

class SearchAppsDialog: Popover() {

    @Inject
    lateinit var playStoreAppsFactory: ViewModelFactory<MainViewModel>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(activity,playStoreAppsFactory)[MainViewModel::class.java]
    }
    private val db = Firebase.firestore

    private lateinit var binding: FragmentSearchAppsDialogBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        if ( viewModel.searchListAppsAdapter.value == null) {
            initSeenRecently()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchAppsDialogBinding.inflate(inflater, container, false)
        binding.rvApps.layoutManager = LinearLayoutManager(activity)
        binding.rvApps.setHasFixedSize(true)

        if ( viewModel.searchListAppsAdapter.value != null) {
            binding.progressBar.visibility = View.GONE
            binding.rvApps.visibility = View.VISIBLE
        }

        //create a new ShapeAppearanceModel
        val shapeAppearanceModel = ShapeAppearanceModel()
            .toBuilder()
            .setTopLeftCorner(CornerFamily.ROUNDED, 0f)
            .setTopRightCorner(CornerFamily.ROUNDED, 8f)
            .setBottomLeftCorner(CornerFamily.ROUNDED, 0f)
            .setBottomRightCorner(CornerFamily.ROUNDED, 8f)
            .build()

        val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
        binding.searchButton.setBackgroundDrawable(shapeDrawable)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //observe
        viewModel.searchListAppsAdapter.observe(viewLifecycleOwner){ searchListAppsAdapter ->
            binding.rvApps.adapter = searchListAppsAdapter
        }

        binding.searchButton.setOnClickListener {
            searchApps()
        }

        binding.closeDialog.setOnClickListener{
            view.closeKeyboardForce()
            dismiss()
        }
    }

    private fun searchApps(){
        db
            .collection("applications")
            .orderBy("App Name", Query.Direction.ASCENDING)
            .orderBy("Maximum Installs", Query.Direction.DESCENDING)
            .whereGreaterThanOrEqualTo("App Name",
                binding.searchInputText.text.toString()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() })
            .whereLessThanOrEqualTo("App Name",
                binding.searchInputText.text.toString()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } + "\\uf7ff")
            .limit(50)
            .get()
            .addOnSuccessListener { documents ->
                try {
                    if (documents != null) {
                        val apps: MutableList<PlayStoreAppModel> = ArrayList()
                        for (document in documents)
                            apps.add(document.toObject(PlayStoreAppModel::class.java))

                        Handler(Looper.getMainLooper()).postDelayed({
                            viewModel.searchListAppsAdapter.postValue(
                                SearchListAppsAdapter(apps){ app ->
                                    onAppClicked(app)
                                }
                            )
                            binding.progressBar.visibility = View.GONE
                            binding.rvApps.visibility = View.VISIBLE
                        }, 500) //millis

                    }
                }catch (ex: Exception){

                    ex.message?.let { Log.e("Error", it) }
                }
            }.addOnFailureListener {
                    e ->
                Log.e("Error", "Error writing document", e)
            }
    }

    private fun initSeenRecently(){
        val auth = Firebase.auth
        if(auth.currentUser != null){
            db.collection("users")
                .document(auth.currentUser?.email.toString())
                .collection("seenRecently")
                .get()
                .addOnSuccessListener { documents ->
                    try {
                        if (documents != null) {
                            val seenRecently: MutableList<PlayStoreAppModel> = ArrayList()
                            for (document in documents)
                                seenRecently.add(document.toObject(PlayStoreAppModel::class.java))

                            if (seenRecently.isEmpty()){
                                searchApps()
                                return@addOnSuccessListener
                            }else{
                                Handler(Looper.getMainLooper()).postDelayed({
                                    viewModel.searchListAppsAdapter.postValue(
                                        SearchListAppsAdapter(seenRecently){ app ->
                                            onAppClicked(app)
                                        }
                                    )
                                    binding.progressBar.visibility = View.GONE
                                    binding.rvApps.visibility = View.VISIBLE
                                }, 500) //millis
                            }


                        } else {
                            Toast.makeText(activity, "Error reading document!", Toast.LENGTH_LONG).show()
                        }
                    }catch (ex: Exception){
                        Toast.makeText(activity, "Error reading document!", Toast.LENGTH_LONG).show()
                        ex.message?.let { Log.e("Error", it) }
                    }
                }.addOnFailureListener {
                        e ->
                    Toast.makeText(activity, "Error reading document! :" + e.message, Toast.LENGTH_LONG).show()
                    e.message?.let { Log.e("Error", it) }
                }
        }
    }


    private fun onAppClicked(app: PlayStoreAppModel){
        view?.closeKeyboardForce()
        dismiss()

        val bundle = Bundle()
        bundle.putSerializable("playstoreapp", app)
        val playStoreAppItemFragment: Fragment = PlayStoreAppItem(app.appName ?: "")
        playStoreAppItemFragment.arguments = bundle
        val fragmentManager = activity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
            .replace(R.id.frame_layout, playStoreAppItemFragment,"playstoreappitem")
            .addToBackStack(null)
            .commit()
    }


}