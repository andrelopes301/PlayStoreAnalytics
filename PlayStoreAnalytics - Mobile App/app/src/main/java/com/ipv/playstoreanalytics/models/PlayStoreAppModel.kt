package com.ipv.playstoreanalytics.models

import com.google.firebase.firestore.PropertyName
import java.io.Serializable


data class PlayStoreAppModel(
    @get:PropertyName("App Name")
    @set:PropertyName("App Name")
    var appName: String? = null,
    @get:PropertyName("Ad Supported")
    @set:PropertyName("Ad Supported")
    var containsAdds: Boolean? = null,
    @get:PropertyName("Category")
    @set:PropertyName("Category")
    var category: String? = null,
    @get:PropertyName("Content Rating")
    @set:PropertyName("Content Rating")
    var contentRating: String? = null,
    @get:PropertyName("Developer Id")
    @set:PropertyName("Developer Id")
    var developerId: String? = null,
    @get:PropertyName("Editors Choice")
    @set:PropertyName("Editors Choice")
    var editorsChoice: Boolean? = null,
    @get:PropertyName("Installs")
    @set:PropertyName("Installs")
    var estimatedInstalls: String? = null,
    @get:PropertyName("Maximum Installs")
    @set:PropertyName("Maximum Installs")
    var exactInstalls: Double? = null,
    @get:PropertyName("Minimum Android")
    @set:PropertyName("Minimum Android")
    var minimumAndroid: String? = null,
    @get:PropertyName("Price")
    @set:PropertyName("Price")
    var price: Float? = null,
    @get:PropertyName("Rating")
    @set:PropertyName("Rating")
    var rating: Float? = null,
    @get:PropertyName("Rating Count")
    @set:PropertyName("Rating Count")
    var ratingCount: Int? = null,
    @get:PropertyName("Released")
    @set:PropertyName("Released")
    var releasedDate: String? = null,
    @get:PropertyName("Size")
    @set:PropertyName("Size")
    var size: String? = null,
    @get:PropertyName("imageURL")
    @set:PropertyName("imageURL")
    var imageUrl: String? = null,

) : Serializable










