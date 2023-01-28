package com.ipv.playstoreanalytics

import android.app.Application
import android.content.res.Resources
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.ipv.playstoreanalytics.di.ApplicationComponent
import com.ipv.playstoreanalytics.di.ApplicationModule
import com.ipv.playstoreanalytics.di.DaggerApplicationComponent

inline fun <reified T> T.TAG(): String = T::class.java.simpleName


class PlayStoreAnalyticsApp : Application() {

    companion object {
        lateinit var instance: Application
        lateinit var resourses: Resources
        var CATEGORY = ""
        var APPNAME = ""
    }

     val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        resourses = resources
        FirebaseApp.initializeApp(this);
        val db = Firebase.firestore

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings


    }
}
