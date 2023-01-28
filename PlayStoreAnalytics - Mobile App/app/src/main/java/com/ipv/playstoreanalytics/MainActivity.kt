package com.ipv.playstoreanalytics

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ipv.playstoreanalytics.databinding.ActivityMainBinding
import com.ipv.playstoreanalytics.fragments.*
import com.ipv.playstoreanalytics.viewmodel.MainViewModel
import com.ipv.playstoreanalytics.viewmodel.ViewModelFactory
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val app get() = applicationContext as PlayStoreAnalyticsApp
    val component by lazy { app.component.activityComponent() }
    private val home = Home()
    private val categories = Categories()
    private val profile = Profile()
    private val login = Login()
    private val statistics = Statistics()
    var randomTab = 0
    var statTabClicked = false


    @Inject
    lateinit var playStoreAppsFactory: ViewModelFactory<MainViewModel>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this,playStoreAppsFactory)[MainViewModel::class.java]
    }

    private  lateinit var searchDialog : SearchAppsDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInitialView()
        binding.compareAppsFAB.setOnClickListener {
            if(binding.progressBar.visibility == View.GONE){
                val compareBottomSheetDialogFragment = CompareBottomSheet()
                compareBottomSheetDialogFragment.show(supportFragmentManager, compareBottomSheetDialogFragment.tag)
            }
        }
        binding.bottomNavigationView
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.MenuHome -> replaceFragment(home,"home")
                R.id.MenuCategories -> replaceFragment(categories,"categories")
                R.id.MenuStatistics ->{
                    val bundle = Bundle()
                    if (statTabClicked){
                        bundle.putInt("tabSelected", randomTab)
                    }else{
                        bundle.putInt("tabSelected", 0)
                    }
                        statistics.arguments = bundle
                        replaceFragment(statistics,"statistics")
                }
                R.id.MenuProfile -> {
                    if(Firebase.auth.currentUser != null)
                        replaceFragment(profile,"profile")
                    else
                        replaceFragment(login,"login")
                }
                else ->{
                }
            }
            true
        }

        if (binding.bottomNavigationView.selectedItemId == R.id.MenuHome){
            replaceFragment(home,"home")
        }
    }



    private fun setupInitialView(){
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.primaryColor)))
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false
    }

    private fun replaceFragment(fragment: Fragment, tag: String?){
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_layout, fragment,tag)
            fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> supportFragmentManager.popBackStack()
            R.id.action_search -> showSearchAppsDialog()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        binding.progressBar.visibility = View.GONE
    }




    private fun showSearchAppsDialog() {
        searchDialog = SearchAppsDialog()
        searchDialog.show(supportFragmentManager)
    }

}

