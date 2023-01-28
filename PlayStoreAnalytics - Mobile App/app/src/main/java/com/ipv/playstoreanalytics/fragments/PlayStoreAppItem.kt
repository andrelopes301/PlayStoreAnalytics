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
import com.ipv.playstoreanalytics.databinding.FragmentPlaystoreappitemBinding
import com.ipv.playstoreanalytics.models.PlayStoreAppModel
import com.ipv.playstoreanalytics.utils.getSerializableCompat
import com.ipv.playstoreanalytics.utils.isOnline
import com.ipv.playstoreanalytics.viewmodel.MainViewModel
import com.ipv.playstoreanalytics.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_playstoreappitem.*
import javax.inject.Inject


class PlayStoreAppItem(private var appName: String) : Fragment(), MenuProvider {

    @Inject
    lateinit var playStoreAppsFactory: ViewModelFactory<MainViewModel>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(activity,playStoreAppsFactory)[MainViewModel::class.java]
    }
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var playStoreApp: PlayStoreAppModel
    private val activity get() = context as MainActivity
    private val component get() = activity.component
    private lateinit var binding: FragmentPlaystoreappitemBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            playStoreApp = it.getSerializableCompat("playstoreapp", PlayStoreAppModel::class.java)
        }


        auth = Firebase.auth
        if (auth.currentUser != null) {
            checkIfFavorite()
            checkSeenRecently(playStoreApp)
        }
        component.inject(this)
    }

    private fun checkSeenRecently(app:PlayStoreAppModel){
        db.collection("users").document(getEmail())
            .collection("seenRecently")
            .whereEqualTo("App Name", app.appName)
            .limit(1)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    //Add to SeenRecently
                    db.collection("users").document(getEmail())
                        .collection("seenRecently")
                        .document(app.appName ?: "")
                        .set(app)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        activity.supportActionBar?.title = "App Details"

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaystoreappitemBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireHost() as MenuHost
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        setupItemView()


        Glide.with(this)
            .load(playStoreApp.imageUrl)
            .into(binding.image)

        binding.appName.text = playStoreApp.appName
        binding.category.text = playStoreApp.category
        binding.price.text = playStoreApp.price.toString()
        if(playStoreApp.containsAdds == true)
            binding.containsAds.text = "True"
        else
            binding.containsAds.text = "False"

        binding.developerId.text = playStoreApp.developerId
        if(playStoreApp.editorsChoice == true)
            binding.editorChoice.text = "True"
        else
            binding.editorChoice.text = "False"
        if(playStoreApp.price?.toInt() == 0)
            binding.price.text = "Free"
        else
            binding.price.text = " ${playStoreApp.price} + €"
        binding.installs.text = playStoreApp.estimatedInstalls
        binding.minimumAndroid.text = playStoreApp.minimumAndroid
        binding.rating.text = playStoreApp.rating.toString()
        binding.size.text = playStoreApp.size
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
        menuInflater.inflate(R.menu.item_menu, menu)
    }


    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

        when(menuItem.itemId){
            R.id.favorite_off -> setFavoriteOnOff()
            R.id.favorite_on -> setFavoriteOnOff()
            R.id.action_compare -> addToCompareList()
        }

        return true
    }

    override fun onPrepareMenu(menu: Menu) {

        viewModel.isFavorite.observe(viewLifecycleOwner){ isEnabled ->
            if (isEnabled != null) {
                menu.findItem(R.id.favorite_on).isVisible = isEnabled
                menu.findItem(R.id.favorite_off).isVisible = !isEnabled
            }
        }
    }

    private fun addToCompareList(){
        val isInCompareList = viewModel.checkIfInCompareList(playStoreApp)
        if (!isInCompareList){
            viewModel.addToCompareList(playStoreApp)
            Toast.makeText(activity, "App added to Compare!", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(activity, "App already in Compare!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setFavoriteOnOff(){

        db.collection("users").document(getEmail())
            .collection("favorites")
            .whereEqualTo("App Name", appName)
            .limit(1)
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    //Add to Favorites
                    db.collection("users").document(getEmail())
                        .collection("favorites")
                        .document(appName)
                        .set(playStoreApp)
                        .addOnSuccessListener {
                            viewModel.isFavorite.postValue(true)
                            //Toast.makeText(activity, "Application added to Favorites!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            viewModel.isFavorite.postValue(true)
                            Log.w("FIREBASE", "Error adding document", e)
                            Toast.makeText(requireActivity(), "Error updating document!", Toast.LENGTH_SHORT).show()
                        }
                    val online = context?.let { context -> isOnline(context)}
                    if (online == false){
                        viewModel.isFavorite.postValue(false)
                        Toast.makeText(requireActivity(), "No connection to Internet. App will be added later!", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    //Remove from Favorites
                    db.collection("users").document(getEmail())
                        .collection("favorites")
                        .document(appName)
                        .delete()
                        .addOnSuccessListener {
                            viewModel.isFavorite.postValue(false)
                          //  Toast.makeText(requireActivity(), "Application removed from Favorites!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error deleting document", e)
                            viewModel.isFavorite.postValue(false)
                            Toast.makeText(requireActivity(), "Error updating document!", Toast.LENGTH_SHORT).show()
                        }

                    val online = context?.let { context -> isOnline(context)}
                    if (online == false){
                        viewModel.isFavorite.postValue(false)
                        Toast.makeText(requireActivity(), "No connection to Internet. App will be removed later!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun checkIfFavorite(){

        db.collection("users").document(getEmail()).collection("favorites")
            .whereEqualTo("App Name", appName)
            .limit(1).get()
            .addOnSuccessListener {
                if(!it.isEmpty)
                    viewModel.isFavorite.postValue(true)
                else
                    viewModel.isFavorite.postValue(false)
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error reading document", e) }
    }

    override fun onDestroy() {
        viewModel.isFavorite.postValue(null)
        super.onDestroy()
    }

    private fun getEmail(): String {
        var email = ""
        if(auth.currentUser != null){
            email = auth.currentUser!!.email.toString()
        }
        return email
    }


}