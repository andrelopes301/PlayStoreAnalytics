package com.ipv.playstoreanalytics.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ipv.playstoreanalytics.MainActivity
import com.ipv.playstoreanalytics.R
import com.ipv.playstoreanalytics.databinding.FragmentPlaystoreappCompareItemsBinding
import com.ipv.playstoreanalytics.databinding.FragmentPlaystoreappitemBinding
import com.ipv.playstoreanalytics.models.PlayStoreAppModel
import com.ipv.playstoreanalytics.utils.getSerializableCompat
import com.ipv.playstoreanalytics.utils.isOnline
import com.ipv.playstoreanalytics.viewmodel.MainViewModel
import com.ipv.playstoreanalytics.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_playstoreappitem.*
import javax.inject.Inject


class PlayStoreAppCompareItems : Fragment(), MenuProvider {

    @Inject
    lateinit var playStoreAppsFactory: ViewModelFactory<MainViewModel>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(activity,playStoreAppsFactory)[MainViewModel::class.java]
    }
    private val activity get() = context as MainActivity
    private val component get() = activity.component
    private lateinit var binding: FragmentPlaystoreappCompareItemsBinding
    private lateinit var playStoreApp1: PlayStoreAppModel
    private lateinit var playStoreApp2: PlayStoreAppModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            playStoreApp1 = it.getSerializableCompat("playstoreapp1", PlayStoreAppModel::class.java)
            playStoreApp2 = it.getSerializableCompat("playstoreapp2", PlayStoreAppModel::class.java)
        }
        component.inject(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaystoreappCompareItemsBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireHost() as MenuHost
        activity.supportActionBar?.title = getString(R.string.compare)
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        setupItemView()

        //App #1
        Glide.with(this)
            .load(playStoreApp1.imageUrl)
            .into(binding.image1)

        binding.appName1.text = playStoreApp1.appName
        binding.category1.text = playStoreApp1.category
        binding.price1.text = playStoreApp1.price.toString()
        if(playStoreApp1.containsAdds == true)
            binding.containsAds1.text = "True"
        else
            binding.containsAds1.text = "False"

        binding.developerId1.text = playStoreApp1.developerId
        if(playStoreApp1.editorsChoice == true)
            binding.editorChoice1.text = "True"
        else
            binding.editorChoice1.text = "False"
        if(playStoreApp1.price?.toInt() == 0)
            binding.price1.text = "Free"
        else
            binding.price1.text = " ${playStoreApp1.price} + €"

        if(playStoreApp1.estimatedInstalls?.isEmpty() == true)
            binding.installs1.text = "N/A"
        else
            binding.installs1.text = playStoreApp1.estimatedInstalls

        if(playStoreApp1.minimumAndroid?.isEmpty() == true)
            binding.minimumAndroid1.text = "N/A"
        else
            binding.minimumAndroid1.text = playStoreApp1.minimumAndroid


        binding.rating1.text = playStoreApp1.rating.toString()

        if(playStoreApp1.size?.isEmpty() == true)
            binding.size1.text = "N/A"
        else
            binding.size1.text = playStoreApp1.size

        //App #2
        Glide.with(this)
            .load(playStoreApp2.imageUrl)
            .into(binding.image2)

        binding.appName2.text = playStoreApp2.appName
        binding.category2.text = playStoreApp2.category
        binding.price2.text = playStoreApp2.price.toString()
        if(playStoreApp2.containsAdds == true)
            binding.containsAds2.text = "True"
        else
            binding.containsAds2.text = "False"

        binding.developerId2.text = playStoreApp2.developerId
        if(playStoreApp2.editorsChoice == true)
            binding.editorChoice2.text = "True"
        else
            binding.editorChoice2.text = "False"
        if(playStoreApp2.price?.toInt() == 0)
            binding.price2.text = "Free"
        else
            binding.price2.text = " ${playStoreApp2.price} + €"

        if(playStoreApp2.estimatedInstalls?.isEmpty() == true)
            binding.installs2.text = "N/A"
        else
            binding.installs2.text = playStoreApp2.estimatedInstalls


        if(playStoreApp2.minimumAndroid?.isEmpty() == true)
            binding.minimumAndroid2.text = "N/A"
        else
            binding.minimumAndroid2.text = playStoreApp2.minimumAndroid

        binding.rating2.text = playStoreApp2.rating.toString()

        if(playStoreApp2.size?.isEmpty() == true)
            binding.size2.text = "N/A"
        else
            binding.size2.text = playStoreApp2.size
    }


    private fun setupItemView(){
        val drawable = AppCompatResources.getDrawable(
            requireContext(),
            R.drawable.ic_arrow_back
        )

        activity.supportActionBar?.setHomeAsUpIndicator(drawable)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val bottomAppBar = requireActivity().findViewById<BottomAppBar>(R.id.bottomAppBar)
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.compareAppsFAB)

        if(bottomNavigationView.visibility == View.VISIBLE){
            bottomNavigationView.animate()
                .translationY(bottomAppBar.height.toFloat())
                .alpha(0.0f)
                .duration = 300
            bottomNavigationView.visibility = View.GONE
        }
        if(bottomAppBar.visibility == View.VISIBLE){
            bottomAppBar.animate()
                .translationY(bottomAppBar.height.toFloat())
                .alpha(0.0f)
                .duration = 300
            bottomAppBar.visibility = View.GONE
        }
        if(fab.visibility == View.VISIBLE){
            fab.animate()
                .translationY(bottomAppBar.height.toFloat())
                .alpha(0.0f)
                .duration = 300
            fab.visibility = View.GONE
        }

    }



    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
    }


    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

        return true
    }


}