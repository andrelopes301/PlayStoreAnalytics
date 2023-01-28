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
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ipv.playstoreanalytics.MainActivity
import com.ipv.playstoreanalytics.PlayStoreAnalyticsApp.Companion.CATEGORY
import com.ipv.playstoreanalytics.R
import com.ipv.playstoreanalytics.adapters.CategoriesAdapter
import com.ipv.playstoreanalytics.databinding.FragmentCategoriesBinding
import com.ipv.playstoreanalytics.models.CategoryModel
import com.ipv.playstoreanalytics.viewmodel.MainViewModel
import com.ipv.playstoreanalytics.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class Categories : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding
    private val activity get() = context as MainActivity
    private val component get() = activity.component
    private val db = Firebase.firestore

    @Inject
    lateinit var playStoreAppsFactory: ViewModelFactory<MainViewModel>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(activity,playStoreAppsFactory)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.categoriesRecyclerView.setHasFixedSize(true)

        if (viewModel.categoriesAdapter.value == null)
            initCategories()
        else{
            binding.progressBar.visibility = View.GONE
            binding.categoriesRecyclerView.visibility = View.VISIBLE
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.supportActionBar?.title = getString(R.string.categories)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        showBottomBar()


        //observe
        viewModel.categoriesAdapter.observe(viewLifecycleOwner){ categoriesAdapter ->
            binding.categoriesRecyclerView.adapter = categoriesAdapter
        }
    }

    private fun initCategories(){
        db
            .collection("categories")
            .get()
            .addOnSuccessListener { documents ->
                try {
                    if (documents != null) {
                        val categories: MutableList<CategoryModel> = ArrayList()
                        for (document in documents)
                            categories.add(document.toObject(CategoryModel::class.java))

                        categories.sortBy { it.name }

                        Handler(Looper.getMainLooper()).postDelayed({
                            viewModel.categoriesAdapter.postValue(CategoriesAdapter(categories){ category ->

                                val categoriesListAppsFragment: Fragment = CategoriesListApps(category.name ?: "")

                                val fragmentManager = activity.supportFragmentManager
                                val fragmentTransaction = fragmentManager.beginTransaction()
                                    .setCustomAnimations(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left,
                                        R.anim.slide_in_left,
                                        R.anim.slide_out_right
                                    )
                                fragmentTransaction.replace(R.id.frame_layout, categoriesListAppsFragment,"categogorieslistapps")
                                fragmentTransaction.addToBackStack(null)
                                fragmentTransaction.commit()


                            })
                            binding.progressBar.visibility = View.GONE
                            binding.categoriesRecyclerView.visibility = View.VISIBLE
                            listenForCategoriesListChanges()
                        }, 500) //millis

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

    private fun listenForCategoriesListChanges(){

        CoroutineScope(Dispatchers.IO).launch {
            val docRef = db.collection("categories")
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    e.message?.let { Log.e( "Listen failed.", it) }
                    return@addSnapshotListener
                }

                val categories: MutableList<CategoryModel> = ArrayList()
                for (document in snapshot?.documents!!) {
                    document.toObject(CategoryModel::class.java)?.let { categories.add(it) }
                }
                categories.sortBy { it.name }

                viewModel.categoriesAdapter.postValue(CategoriesAdapter(categories){ category ->
                    val categoriesListAppsFragment: Fragment = CategoriesListApps(category.name ?: "")
                    val fragmentManager = activity.supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction
                        .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                        )
                        .replace(R.id.frame_layout, categoriesListAppsFragment,"categogorieslistapps")
                        .addToBackStack(null)
                        .commit()

                })
            }
        }
    }




    private fun showBottomBar(){
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val bottomAppBar = requireActivity().findViewById<BottomAppBar>(R.id.bottomAppBar)
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.compareAppsFAB)

        if(bottomNavigationView.visibility == View.GONE){
            bottomNavigationView.animate()
                .translationY(0f)
                .alpha(1.0f)
                .duration = 300
            bottomNavigationView.visibility = View.VISIBLE
        }
        if(bottomAppBar.visibility == View.GONE){
            bottomAppBar.animate()
                .translationY(0f)
                .alpha(1.0f)
                .duration = 300
            bottomAppBar.visibility = View.VISIBLE
        }
        if(fab.visibility == View.GONE){
            fab.animate()
                .translationY(0f)
                .alpha(1.0f)
                .duration = 300
            fab.visibility = View.VISIBLE
        }
    }


}