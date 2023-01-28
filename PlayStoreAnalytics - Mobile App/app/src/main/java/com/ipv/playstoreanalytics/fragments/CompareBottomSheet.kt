package com.ipv.playstoreanalytics.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ipv.playstoreanalytics.MainActivity
import com.ipv.playstoreanalytics.R
import com.ipv.playstoreanalytics.databinding.FragmentCompareBottomSheetBinding
import com.ipv.playstoreanalytics.models.PlayStoreAppModel
import com.ipv.playstoreanalytics.viewmodel.MainViewModel
import com.ipv.playstoreanalytics.viewmodel.ViewModelFactory
import javax.inject.Inject


class CompareBottomSheet : BottomSheetDialogFragment() {


    private lateinit var binding: FragmentCompareBottomSheetBinding
    private val activity get() = context as MainActivity
    private val component get() = activity.component

    @Inject
    lateinit var playStoreAppsFactory: ViewModelFactory<MainViewModel>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(activity,playStoreAppsFactory)[MainViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompareBottomSheetBinding.inflate(inflater, container, false)

        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.compareAppsButton.setOnClickListener {
            compareApps()
        }
        binding.removeButton1.setOnClickListener {
            viewModel.removeFromCompareListIndex(0)
        }
        binding.removeButton2.setOnClickListener {
            viewModel.removeFromCompareListIndex(1)
        }

        //observe
        viewModel.compareAppsList.observe(viewLifecycleOwner){ appsList ->
            setCompareListView(appsList)
        }
    }

    private fun setCompareListView(appsList: MutableList<PlayStoreAppModel>) {
        if(appsList.count() == 1){
            binding.removeButton1.isEnabled = true
            binding.removeButton1.isClickable = true
            binding.removeButton1.alpha = 1f
            binding.app1.setText(appsList[0].appName)
            binding.removeButton2.isEnabled = false
            binding.removeButton2.isClickable = false
            binding.removeButton2.alpha = 0.5f
            binding.app2.setText("App #2")
            binding.compareAppsButton.isEnabled = false
            binding.compareAppsButton.isClickable = false
            binding.compareAppsButton.alpha = 0.5f

        }else if(appsList.count() == 2){
            binding.removeButton1.isEnabled = true
            binding.removeButton1.isClickable = true
            binding.removeButton1.alpha = 1f
            binding.app1.setText(appsList[0].appName)
            binding.removeButton2.isEnabled = true
            binding.removeButton2.isClickable = true
            binding.removeButton2.alpha = 1f
            binding.app2.setText(appsList[1].appName)
            binding.compareAppsButton.isEnabled = true
            binding.compareAppsButton.isClickable = true
            binding.compareAppsButton.alpha = 1f
        }else{
            binding.removeButton1.isEnabled = false
            binding.removeButton1.isClickable = false
            binding.removeButton1.alpha = 0.5f
            binding.removeButton2.isEnabled = false
            binding.removeButton2.isClickable = false
            binding.removeButton2.alpha = 0.5f
            binding.app1.setText("App #1")
            binding.app2.setText("App #2")
            binding.compareAppsButton.isEnabled = false
            binding.compareAppsButton.isClickable = false
            binding.compareAppsButton.alpha = 0.5f
        }
    }

    private fun compareApps(){


        dialog?.hide()

        val bundle = Bundle()
        bundle.putSerializable("playstoreapp1", viewModel.compareAppsList.value?.get(0))
        bundle.putSerializable("playstoreapp2", viewModel.compareAppsList.value?.get(1))
        val playStoreAppCompareItemsFragment: Fragment = PlayStoreAppCompareItems()

        playStoreAppCompareItemsFragment.arguments = bundle
        val fragmentManager = activity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
            .replace(R.id.frame_layout, playStoreAppCompareItemsFragment,"playstorecompareappitems")
            .addToBackStack(null)
            .commit()



    }
}