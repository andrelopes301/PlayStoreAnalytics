package com.ipv.playstoreanalytics.firebase

import com.ipv.playstoreanalytics.models.PlayStoreAppModel

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.ipv.playstoreanalytics.PlayStoreAnalyticsApp.Companion.CATEGORY
import kotlinx.coroutines.tasks.await

class FirestorePagingSource(
    private val db: FirebaseFirestore,
) : PagingSource<QuerySnapshot, PlayStoreAppModel>() {


    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, PlayStoreAppModel> {
        return try {
            val currentPage = params.key ?:
            db.collection("applications")
                .whereEqualTo("Category", CATEGORY)
                .orderBy("Maximum Installs", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()

            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            val nextPage = db.collection("applications")
                .whereEqualTo("Category", CATEGORY)
                .orderBy("Maximum Installs", Query.Direction.DESCENDING)
                .limit(10)
                .startAfter(lastDocumentSnapshot)
                .get()
                .await()

            LoadResult.Page(
                data = currentPage.toObjects(PlayStoreAppModel::class.java),
                prevKey = null,
                nextKey = nextPage
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }


    override fun getRefreshKey(state: PagingState<QuerySnapshot, PlayStoreAppModel>): QuerySnapshot? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPageIndex = state.pages.indexOf(state.closestPageToPosition(anchorPosition))
            state.pages.getOrNull(anchorPageIndex + 1)?.prevKey ?: state.pages.getOrNull(anchorPageIndex - 1)?.nextKey
        }
    }
}
