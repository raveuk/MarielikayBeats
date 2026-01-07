package com.zimbabeats.ui.viewmodel.music

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zimbabeats.cloud.CloudPairingClient
import com.zimbabeats.cloud.RemoteConfigManager
import com.zimbabeats.core.domain.model.music.MusicPlaylist
import com.zimbabeats.core.domain.model.music.Track
import com.zimbabeats.core.domain.repository.MusicRepository
import com.zimbabeats.core.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class MusicLibraryTab {
    PLAYLISTS, FAVORITES, HISTORY
}

data class MusicLibraryUiState(
    val selectedTab: MusicLibraryTab = MusicLibraryTab.PLAYLISTS,
    val playlists: List<MusicPlaylist> = emptyList(),
    val favoriteTracks: List<Track> = emptyList(),
    val historyTracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val showCreatePlaylistDialog: Boolean = false,
    val error: String? = null
)

class MusicLibraryViewModel(
    private val musicRepository: MusicRepository,
    private val cloudPairingClient: CloudPairingClient
) : ViewModel() {

    companion object {
        private const val TAG = "MusicLibraryViewModel"
    }

    // Cloud-based content filter (Firebase) - for family-specific rules
    private val contentFilter get() = cloudPairingClient.contentFilter

    // Global content filter (RemoteConfig) - ALWAYS applies regardless of family linking
    private val remoteConfigManager = RemoteConfigManager()

    private val _uiState = MutableStateFlow(MusicLibraryUiState())
    val uiState: StateFlow<MusicLibraryUiState> = _uiState.asStateFlow()

    init {
        loadPlaylists()
        loadFavorites()
        loadHistory()
    }

    /**
     * Filter tracks using both Global blocks and Cloud Content Filter.
     * Global blocks ALWAYS apply regardless of family linking.
     */
    private fun filterTracks(tracks: List<Track>): List<Track> {
        return tracks.filter { track ->
            // ALWAYS check global blocks first (regardless of family linking)
            val textToCheck = "${track.title} ${track.artistName} ${track.albumName ?: ""}"
            val globalKeywordBlock = remoteConfigManager.isGloballyBlocked(textToCheck)
            if (globalKeywordBlock.isBlocked) {
                Log.d(TAG, "Track '${track.title}' blocked by global keyword filter")
                return@filter false
            }

            val globalArtistBlock = remoteConfigManager.isArtistGloballyBlocked(
                track.artistId ?: "",
                track.artistName
            )
            if (globalArtistBlock.isBlocked) {
                Log.d(TAG, "Track '${track.title}' by '${track.artistName}' blocked by global artist filter")
                return@filter false
            }

            // If linked to family, also apply family-specific rules
            val filter = contentFilter
            if (filter != null) {
                val blockResult = filter.shouldBlockMusicContent(
                    trackId = track.id,
                    title = track.title,
                    artistId = track.artistId ?: "",
                    artistName = track.artistName,
                    albumName = track.albumName,
                    genre = null,
                    durationSeconds = track.duration / 1000L,
                    isExplicit = track.isExplicit
                )
                if (blockResult.isBlocked) {
                    Log.d(TAG, "Track '${track.title}' blocked by family filter: ${blockResult.reason}")
                    return@filter false
                }
            }

            true // Not blocked
        }
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            musicRepository.getMusicPlaylists().collect { playlists ->
                _uiState.value = _uiState.value.copy(playlists = playlists)
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            musicRepository.getFavoriteTracks().collect { tracks ->
                val filteredTracks = filterTracks(tracks)
                Log.d(TAG, "Loaded ${tracks.size} favorites, ${filteredTracks.size} after filtering")
                _uiState.value = _uiState.value.copy(favoriteTracks = filteredTracks)
            }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            musicRepository.getListeningHistory(100).collect { tracks ->
                val filteredTracks = filterTracks(tracks)
                Log.d(TAG, "Loaded ${tracks.size} history tracks, ${filteredTracks.size} after filtering")
                _uiState.value = _uiState.value.copy(historyTracks = filteredTracks)
            }
        }
    }

    fun selectTab(tab: MusicLibraryTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun showCreatePlaylistDialog() {
        _uiState.value = _uiState.value.copy(showCreatePlaylistDialog = true)
    }

    fun hideCreatePlaylistDialog() {
        _uiState.value = _uiState.value.copy(showCreatePlaylistDialog = false)
    }

    fun createPlaylist(name: String, description: String? = null) {
        viewModelScope.launch {
            when (val result = musicRepository.createMusicPlaylist(name, description)) {
                is Resource.Success -> {
                    Log.d(TAG, "Created playlist: $name with id: ${result.data}")
                    _uiState.value = _uiState.value.copy(showCreatePlaylistDialog = false)
                }
                is Resource.Error -> {
                    Log.e(TAG, "Failed to create playlist: ${result.message}")
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                else -> {}
            }
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            when (val result = musicRepository.deleteMusicPlaylist(playlistId)) {
                is Resource.Success -> {
                    Log.d(TAG, "Deleted playlist: $playlistId")
                }
                is Resource.Error -> {
                    Log.e(TAG, "Failed to delete playlist: ${result.message}")
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                else -> {}
            }
        }
    }

    fun toggleFavorite(track: Track) {
        viewModelScope.launch {
            when (val result = musicRepository.toggleFavorite(track)) {
                is Resource.Success -> {
                    Log.d(TAG, "Toggled favorite for: ${track.id}, now: ${result.data}")
                }
                is Resource.Error -> {
                    Log.e(TAG, "Failed to toggle favorite: ${result.message}")
                }
                else -> {}
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            musicRepository.clearListeningHistory()
            Log.d(TAG, "Cleared listening history")
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
