package com.ipv.playstoreanalytics.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ipv.playstoreanalytics.MainActivity
import com.ipv.playstoreanalytics.PlayStoreAnalyticsApp.Companion.CATEGORY
import com.ipv.playstoreanalytics.R
import com.ipv.playstoreanalytics.databinding.FragmentCategoriesListAppsBinding
import com.ipv.playstoreanalytics.models.PlayStoreAppModel
import com.ipv.playstoreanalytics.viewmodel.MainViewModel
import com.ipv.playstoreanalytics.viewmodel.ViewModelFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject


class CategoriesListApps(private var categoryName: String) : Fragment(), MenuProvider {

    @Inject
    lateinit var playStoreAppsFactory: ViewModelFactory<MainViewModel>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(activity,playStoreAppsFactory)[MainViewModel::class.java]
    }

    private lateinit var auth: FirebaseAuth
    private val activity get() = context as MainActivity
    private val component get() = activity.component
    private lateinit var binding: FragmentCategoriesListAppsBinding
    private var adapter = AppsAdapter{}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        auth = Firebase.auth
        adapter = AppsAdapter{ app ->
            onAppClicked(app)
        }

        CATEGORY = categoryName
        lifecycleScope.launch {
            viewModel.flow.collect {
                adapter.submitData(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesListAppsBinding.inflate(inflater, container, false)
        activity.supportActionBar?.title = categoryName
        binding.appsRecyclerView.adapter = adapter

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding.progressBar.isVisible = loadStates.refresh is LoadState.Loading
                binding.progressBarLoadMore.isVisible = loadStates.append is LoadState.Loading
            }
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireHost() as MenuHost
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        setupItemView()

    }

    private fun onAppClicked(app: PlayStoreAppModel){
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
        menuInflater.inflate(R.menu.home_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId){
            R.id.action_search -> {}
        }
        return true
    }

    override fun onDestroy() {
        adapter.refresh()
        super.onDestroy()
    }
}