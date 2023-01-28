package com.ipv.playstoreanalytics.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ipv.playstoreanalytics.MainActivity
import com.ipv.playstoreanalytics.R
import com.ipv.playstoreanalytics.databinding.FragmentAboutBinding

class About : Fragment(), MenuProvider {

    private lateinit var binding: FragmentAboutBinding
    private val activity get() = context as MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireHost() as MenuHost
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        setupItemView()

    }

    private fun setupItemView(){
        val drawable = AppCompatResources.getDrawable(
            requireContext(),
            R.drawable.ic_arrow_back
        )
        activity.supportActionBar?.title = getString(R.string.about)
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
}