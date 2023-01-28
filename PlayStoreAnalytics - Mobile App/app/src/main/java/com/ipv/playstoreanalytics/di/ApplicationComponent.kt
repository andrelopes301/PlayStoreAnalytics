package com.ipv.playstoreanalytics.di
import com.ipv.playstoreanalytics.PlayStoreAnalyticsApp
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
    ]
)
interface ApplicationComponent {

    fun activityComponent(): ActivityComponent
    fun inject(app: PlayStoreAnalyticsApp)
}