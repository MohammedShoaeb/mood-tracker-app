package com.shadow.moodtracker.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shadow.moodtracker.data.repository.Episode
import com.shadow.moodtracker.data.repository.Series
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
class SeriesTrackerViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _seriesList = MutableStateFlow<List<Pair<String, Series>>>(emptyList())
    val seriesList: StateFlow<List<Pair<String, Series>>> = _seriesList.asStateFlow()

    init {
        fetchSeries()
    }

    fun fetchSeries() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("seriesTracker")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val result = snapshot.documents.mapNotNull {
                        val series = it.toObject(Series::class.java)
                        if (series != null) it.id to series else null
                    }
                    _seriesList.value = result
                }

            }
    }



    fun addSeries(series: Series,onResult: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("seriesTracker")
            .add(series)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun updateEpisode(seriesId: String, seasonIndex: Int, episodeIndex: Int, newEpisode: Episode,
                      onResult: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val seriesRef = db.collection("users").document(userId).collection("seriesTracker").document(seriesId)

        seriesRef.get().addOnSuccessListener {
            val current = it.toObject(Series::class.java) ?: return@addOnSuccessListener
            val updatedSeasons = current.seasons.toMutableList()
            val updatedEpisodes = updatedSeasons[seasonIndex].episodes.toMutableList()
            updatedEpisodes[episodeIndex] = newEpisode
            updatedSeasons[seasonIndex] = updatedSeasons[seasonIndex].copy(episodes = updatedEpisodes)
            seriesRef.set(current.copy(seasons = updatedSeasons))
            onResult(true)
        }
            .addOnFailureListener {
                onResult(false)
            }
    }
    fun updateSeries(seriesId: String, updatedSeries: Series,onResult: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("seriesTracker")
            .document(seriesId)
            .set(updatedSeries)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun deleteSeries(seriesId: String,onResult: (Boolean)->Unit) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("seriesTracker")
            .document(seriesId)
            .delete()
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }
}