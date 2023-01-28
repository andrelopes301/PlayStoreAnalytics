package com.ipv.playstoreanalytics.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ipv.playstoreanalytics.MainActivity
import com.ipv.playstoreanalytics.R
import com.ipv.playstoreanalytics.databinding.FragmentStatisticsBinding
import com.ipv.playstoreanalytics.databinding.FragmentStatisticsSubViewBinding
import com.ipv.playstoreanalytics.models.StatisticsModel
import com.ipv.playstoreanalytics.utils.getColorCompat
import com.ipv.playstoreanalytics.viewmodel.MainViewModel
import com.ipv.playstoreanalytics.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import kotlin.math.max
import kotlin.math.min
import javax.inject.Inject


class Statistics : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding
    private val activity get() = context as MainActivity
    private val component get() = activity.component
    private val db = Firebase.firestore
    private var viewPagerTabSelected: Int = 0

    @Inject
    lateinit var playStoreAppsFactory: ViewModelFactory<MainViewModel>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(activity,playStoreAppsFactory)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        arguments?.let {
            viewPagerTabSelected = it.getInt("tabSelected",0)
            (requireActivity() as MainActivity).statTabClicked = false
        }

        if (viewModel.statisticsList.value == null)
            initStatistics()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        if (viewModel.statisticsList.value != null){
            binding.progressBar.visibility = View.GONE
            binding.htabAppbar.visibility = View.VISIBLE
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.supportActionBar?.title = getString(R.string.statistics)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)



        binding.htabTabs.setTabTextColors(
            activity.getColorCompat(R.color.colorWhite),
            activity.getColorCompat(R.color.secondaryColor)
        )

        binding.htabTabs.setSelectedTabIndicatorColor(
            activity.getColorCompat(R.color.secondaryColor))



        //observe
        viewModel.statisticsList.observe(viewLifecycleOwner){ statistics ->
            setupViewPager(statistics)
            setupTabLayout(statistics)

        }
    }



    private fun setupTabLayout(statistics: List<StatisticsModel>) {
        if (statistics.count() > 4)
            binding.htabTabs.tabMode = TabLayout.MODE_SCROLLABLE
        else
            binding.htabTabs.tabMode = TabLayout.MODE_FIXED
        TabLayoutMediator(
            binding.htabTabs, binding.htabViewpager
        ) { tab, position -> tab.text = statistics[position].statTitle }.attach()
    }
    private fun setupViewPager(statistics: List<StatisticsModel>) {
        val adapter = ViewPagerAdapter(activity, statistics.size)
        binding.htabViewpager.adapter = adapter

        binding.htabViewpager.post {
            binding.htabViewpager.setCurrentItem(viewPagerTabSelected, false)
        }
    }





    inner class ViewPagerAdapter(fragmentActivity: FragmentActivity, private var totalCount: Int) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return totalCount
        }

        override fun createFragment(position: Int): Fragment {
            return StatisticsSubViewFragment(viewModel.statisticsList.value?.get(position))
        }


    }



    class StatisticsSubViewFragment(private val statisticsModel: StatisticsModel?) : Fragment() {

        private lateinit var binding: FragmentStatisticsSubViewBinding


        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            binding = FragmentStatisticsSubViewBinding.inflate(layoutInflater)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            setupStatisticsView()
        }

        private fun setupStatisticsView() {

            binding.statstitle.text = statisticsModel?.title
            Glide.with(this)
                .load(statisticsModel?.imageURL)
                .into(binding.statsImage)
            val text = statisticsModel?.description?.replace("\\n", "\n")
            binding.statsDescription.text = text

        }

    }


    private fun initStatistics(){

        db
            .collection("statistics")
            .get()
            .addOnSuccessListener { documents ->
                try {
                    if (documents != null) {
                        val statistics: MutableList<StatisticsModel> = ArrayList()
                        for (document in documents)
                            statistics.add(document.toObject(StatisticsModel::class.java))

                        Handler(Looper.getMainLooper()).postDelayed({
                            viewModel.statisticsList.postValue(statistics)
                            binding.progressBar.visibility = View.GONE
                            binding.htabAppbar.visibility = View.VISIBLE
                            listenForStatisticListChanges()
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

    private fun listenForStatisticListChanges(){

        CoroutineScope(Dispatchers.IO).launch {
            val docRef = db.collection("statistics")
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    e.message?.let { Log.e( "Listen failed.", it) }
                    return@addSnapshotListener
                }

                val statistics: MutableList<StatisticsModel> = ArrayList()
                for (document in snapshot?.documents!!)
                    document.toObject(StatisticsModel::class.java)?.let { statistics.add(it) }

                viewModel.statisticsList.postValue(statistics)
            }
        }
    }



}