package com.ipv.playstoreanalytics.di

import com.ipv.playstoreanalytics.MainActivity
import com.ipv.playstoreanalytics.fragments.*
import dagger.Subcomponent


@Subcomponent
interface ActivityComponent {

    // Activities
    fun inject(activity: MainActivity)

    // Fragments
    fun inject(fragment: Home)
    fun inject(fragment: Categories)
    fun inject(fragment: Statistics)
    fun inject(fragment: PlayStoreAppItem)
    fun inject(fragment: CategoriesListApps)
    fun inject(fragment: CompareBottomSheet)
    fun inject(fragment: SearchAppsDialog)
    fun inject(fragment: Login)
    fun inject(fragment: Profile)
    fun inject(fragment: Favorites)
    fun inject(fragment: SeenRecently)
    fun inject(fragment: PlayStoreAppCompareItems)
}

