package com.ipv.playstoreanalytics.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ipv.playstoreanalytics.MainActivity
import com.ipv.playstoreanalytics.R
import com.ipv.playstoreanalytics.adapters.PlayStoreAppsAdapter
import com.ipv.playstoreanalytics.databinding.FragmentProfileBinding
import com.ipv.playstoreanalytics.models.PlayStoreAppModel
import com.ipv.playstoreanalytics.viewmodel.MainViewModel
import com.ipv.playstoreanalytics.viewmodel.ViewModelFactory
import javax.inject.Inject


class Profile : Fragment() {

    private val activity get() = context as MainActivity
    private val component get() = activity.component
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.profileUsername.text = auth.currentUser?.email ?: getString(R.string.profile)

        binding.account.categoryText.text = getString(R.string.account)
        binding.account.cardImage.setImageResource(R.drawable.person)

        binding.favorites.categoryText.text = getString(R.string.favorites)
        binding.favorites.cardImage.setImageResource(R.drawable.favorite_border)

        binding.seenRecently.categoryText.text = getString(R.string.seen_recently)
        binding.seenRecently.cardImage.setImageResource(R.drawable.seen_recently)

        binding.about.categoryText.text = getString(R.string.about)
        binding.about.cardImage.setImageResource(R.drawable.about)

        binding.logout.categoryText.text = getString(R.string.log_out)
        binding.logout.cardImage.setImageResource(R.drawable.logout)



        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.supportActionBar?.title = getString(R.string.profile)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        showBottomBar()

        binding.favorites.listItem.setOnClickListener {
            initFavorites()
        }

        binding.seenRecently.listItem.setOnClickListener {
            initSeenRecently()
        }

        binding.logout.listItem.setOnClickListener {
            Firebase.auth.signOut()
            goToLoginFragment()
        }
    }



    private fun initFavorites(){
        db.collection("users").document(auth.currentUser?.email.toString())
            .collection("favorites")
            .get()
            .addOnSuccessListener { documents ->
                try {
                    if (documents != null) {
                        val favorites: MutableList<PlayStoreAppModel> = ArrayList()
                        for (document in documents)
                            favorites.add(document.toObject(PlayStoreAppModel::class.java))


                        if (favorites.isEmpty()){
                            Toast.makeText(activity, "There are no apps in your favorites", Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        }else{

                            val favoritesFragment: Fragment = Favorites()
                            val fragmentManager = activity.supportFragmentManager
                            val fragmentTransaction = fragmentManager.beginTransaction()
                            fragmentTransaction
                                .setCustomAnimations(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left,
                                    R.anim.slide_in_left,
                                    R.anim.slide_out_right
                                )
                                .replace(R.id.frame_layout, favoritesFragment,"favorites")
                                .addToBackStack(null)
                                .commit()
                        }
                        viewModel.favoritesAdapter.postValue(PlayStoreAppsAdapter(favorites){ app ->
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

    private fun initSeenRecently(){
        db.collection("users").document(auth.currentUser?.email.toString())
            .collection("seenRecently")
            .get()
            .addOnSuccessListener { documents ->
                try {
                    if (documents != null) {
                        val seenRecently: MutableList<PlayStoreAppModel> = ArrayList()
                        for (document in documents)
                            seenRecently.add(document.toObject(PlayStoreAppModel::class.java))

                        if (seenRecently.isEmpty()){
                            Toast.makeText(activity, "You have seen no applications yet!", Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        }else{

                            val  seenRecentlyFragment: Fragment = SeenRecently()
                            val fragmentManager = activity.supportFragmentManager
                            val fragmentTransaction = fragmentManager.beginTransaction()
                            fragmentTransaction
                                .setCustomAnimations(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left,
                                    R.anim.slide_in_left,
                                    R.anim.slide_out_right
                                )
                                .replace(R.id.frame_layout, seenRecentlyFragment,"seenRecently")
                                .addToBackStack(null)
                                .commit()
                        }
                        viewModel.seenRecentlyAdapter.postValue(PlayStoreAppsAdapter(seenRecently){ app ->
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


    private fun goToLoginFragment(){
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,Login(),"login")
        fragmentTransaction.commit()
    }
}