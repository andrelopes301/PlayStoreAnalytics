package com.ipv.playstoreanalytics.viewmodel

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.firestore.FirebaseFirestore
import com.ipv.playstoreanalytics.adapters.*
import com.ipv.playstoreanalytics.firebase.FirestorePagingSource
import com.ipv.playstoreanalytics.models.PlayStoreAppModel
import com.ipv.playstoreanalytics.models.StatisticsModel
import javax.inject.Inject


class MainViewModel @Inject  constructor(): ViewModel() {


    val searchListAppsAdapter = MutableLiveData<SearchListAppsAdapter>()
    val compareAppsList = MutableLiveData<MutableList<PlayStoreAppModel>>()
    val statisticsList = MutableLiveData<List<StatisticsModel>>()
    val categoriesListAppsAdapter = MutableLiveData<CategoriesListAppsAdapter>()

    val randomStat = MutableLiveData<StatisticsModel>()
    val topAppsAdapter = MutableLiveData<HomeAppsAdapter>()
    var categoriesAdapter = MutableLiveData<CategoriesAdapter>()
    val randomAppsAdapter = MutableLiveData<HomeAppsAdapter>()
    var favoritesAdapter = MutableLiveData<PlayStoreAppsAdapter>()
    var seenRecentlyAdapter = MutableLiveData<PlayStoreAppsAdapter>()
    var isFavorite = MutableLiveData<Boolean>()
    var isLoginButton = MutableLiveData<Boolean>()

    var flow = Pager(PagingConfig(20)) {
        FirestorePagingSource(FirebaseFirestore.getInstance())
    }.flow.cachedIn(viewModelScope)

    var flowSearch = Pager(PagingConfig(20)) {
        FirestorePagingSource(FirebaseFirestore.getInstance())
    }.flow.cachedIn(viewModelScope)


    fun addToCompareList(app: PlayStoreAppModel){
        var appsList = mutableListOf<PlayStoreAppModel>()

        if (compareAppsList.value != null){
            appsList = compareAppsList.value!!
            if (appsList.count() > 1) {
                appsList[1] = app
            }  else
                appsList.add(app)
        }else{
            appsList.add(app)
        }
        compareAppsList.postValue(appsList)
    }

    fun removeFromCompareListIndex(index: Int){
        if (compareAppsList.value != null){
            val appsList = compareAppsList.value!!
            appsList.removeAt(index)

            compareAppsList.postValue(appsList)
        }
    }


    fun checkIfInCompareList(app: PlayStoreAppModel): Boolean{
        if (compareAppsList.value != null){
            val appsList = compareAppsList.value!!

            return appsList.contains(app)
        }
        return false
    }

}
