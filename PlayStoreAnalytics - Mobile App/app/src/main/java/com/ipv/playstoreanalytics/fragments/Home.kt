package com.ipv.playstoreanalytics.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ipv.playstoreanalytics.*
import com.ipv.playstoreanalytics.adapters.HomeAppsAdapter
import com.ipv.playstoreanalytics.databinding.FragmentHomeBinding
import com.ipv.playstoreanalytics.models.PlayStoreAppModel
import com.ipv.playstoreanalytics.models.StatisticsModel
import com.ipv.playstoreanalytics.viewmodel.MainViewModel
import com.ipv.playstoreanalytics.viewmodel.ViewModelFactory
import javax.inject.Inject


class Home : Fragment(),MenuProvider {

    private val activity get() = context as MainActivity
    private val component get() = activity.component
    private lateinit var binding: FragmentHomeBinding
    private val db = Firebase.firestore

    @Inject
    lateinit var playStoreAppsFactory: ViewModelFactory<MainViewModel>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(activity,playStoreAppsFactory)[MainViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)

        if (viewModel.randomAppsAdapter.value == null)
            getRandomApps()
        if (viewModel.topAppsAdapter.value == null)
            initTopApps()

        if (viewModel.randomStat.value == null)
            getRandomStatistic()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        if (viewModel.randomAppsAdapter.value != null){
            binding.progressBarRandomApps.visibility = View.GONE
            binding.randomAppsRV.visibility = View.VISIBLE
        }

        if (viewModel.topAppsAdapter.value != null){
            binding.progressBarTopApps.visibility = View.GONE
            binding.topAppsRV.visibility = View.VISIBLE
        }


        binding.topAppsRV.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        binding.topAppsRV.setHasFixedSize(true)
        binding.randomAppsRV.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        binding.randomAppsRV.setHasFixedSize(true)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireHost() as MenuHost
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        activity.supportActionBar?.title = getString(R.string.analytics)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        showBottomBar()


        viewModel.topAppsAdapter.observe(viewLifecycleOwner){ topAppsAdapter ->
            binding.topAppsRV.adapter = topAppsAdapter
        }
        viewModel.randomAppsAdapter.observe(viewLifecycleOwner){ randomAppsAdapter ->
            binding.randomAppsRV.adapter = randomAppsAdapter
        }

        viewModel.randomStat.observe(viewLifecycleOwner){ randomStat ->
            binding.statisticsTitle.text = randomStat.title
            Glide.with(this)
                .load(randomStat.imageURL)
                .into(binding.statisticsImage)

            binding.randomStatBtn.setOnClickListener {
                val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                (requireActivity() as MainActivity).statTabClicked = true
                bottomNavigationView.selectedItemId = R.id.MenuStatistics
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


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.home_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return true
    }

    private fun getRandomStatistic(){

        db
            .collection("statistics")
            .document(0.toString().padStart(3, '0'))
            .get()
            .addOnSuccessListener { document ->
                try {
                    if (document != null) {
                        document.toObject(StatisticsModel::class.java)?.let { app ->
                            viewModel.randomStat.postValue(app)
                            activity.randomTab = 0
                        }

                    }
                } catch (ex: Exception){
                    // Toast.makeText(activity, "Error reading document!", Toast.LENGTH_LONG).show()
                }
            }


/*        db
            .collection("statistics")
            .count()
            .get(AggregateSource.SERVER).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result

                    val min = 0
                    val max = (snapshot.count-1).toInt()
                    val random = java.util.Random()
                    // randomTab = random.nextInt(max - min + 1) + min
                    val randomTab = 2


                    //

                }

            }*/

    }

    private fun getRandomApps(){
        db
            .collection("applications")
            .count()
            .get(AggregateSource.SERVER).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result

                    val min = 0
                    val max = (snapshot.count-1).toInt()
                    val random = java.util.Random()
                    val generatedNumbers = mutableSetOf<Int>()
                    while (generatedNumbers.size < 5) {
                        val randomInt = random.nextInt(max - min + 1) + min
                        generatedNumbers.add(randomInt)
                    }
                    val randomApps: MutableList<PlayStoreAppModel> = ArrayList()
                    generatedNumbers.forEach {
                        db
                            .collection("applications")
                            .document(it.toString().padStart(5, '0'))
                            .get()
                            .addOnSuccessListener { document ->
                                try {
                                    if (document != null) {
                                        document.toObject(PlayStoreAppModel::class.java)?.let { app ->
                                            randomApps.add(app)
                                        }

                                        if (randomApps.count() == 5) {
                                            viewModel.randomAppsAdapter.postValue(HomeAppsAdapter(randomApps) { app ->
                                                val bundle = Bundle()
                                                bundle.putSerializable("playstoreapp", app)
                                                val playStoreAppItemFragment: Fragment =
                                                    PlayStoreAppItem(app.appName ?: "")
                                                playStoreAppItemFragment.arguments = bundle
                                                val fragmentManager =
                                                    activity.supportFragmentManager
                                                val fragmentTransaction =
                                                    fragmentManager.beginTransaction()
                                                fragmentTransaction
                                                    .setCustomAnimations(
                                                        R.anim.slide_in_right,
                                                        R.anim.slide_out_left,
                                                        R.anim.slide_in_left,
                                                        R.anim.slide_out_right
                                                    )
                                                    .replace(
                                                        R.id.frame_layout,
                                                        playStoreAppItemFragment,
                                                        "playstoreappitem"
                                                    )
                                                    .addToBackStack(null)
                                                    .commit()
                                            })
                                            binding.randomAppsRV.visibility = View.VISIBLE
                                            binding.progressBarRandomApps.visibility = View.GONE
                                        }
                                    }
                                } catch (ex: Exception){
                                    Toast.makeText(activity, "Error reading document!", Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                }
            }

    }

    private fun initTopApps(){
        db
            .collection("applications")
            .orderBy("Maximum Installs", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                try {
                    if (documents != null) {
                        val topApps: MutableList<PlayStoreAppModel> = ArrayList()
                        for (document in documents)
                            topApps.add(document.toObject(PlayStoreAppModel::class.java))

                        Handler(Looper.getMainLooper()).postDelayed({
                            viewModel.topAppsAdapter.postValue(HomeAppsAdapter(topApps) { app ->
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

                            })
                            binding.topAppsRV.visibility = View.VISIBLE
                            binding.progressBarTopApps.visibility = View.GONE
                        }, 150) //millis
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