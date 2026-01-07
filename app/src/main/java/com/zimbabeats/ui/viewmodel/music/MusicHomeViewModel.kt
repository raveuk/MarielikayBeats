package com.zimbabeats.ui.viewmodel.music

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zimbabeats.cloud.CloudPairingClient
import com.zimbabeats.cloud.PairingStatus
import com.zimbabeats.cloud.RemoteConfigManager
import com.zimbabeats.core.domain.model.music.MusicBrowseItem
import com.zimbabeats.core.domain.model.music.MusicBrowseSection
import com.zimbabeats.core.domain.model.music.MusicSearchFilter
import com.zimbabeats.core.domain.model.music.MusicSearchResult
import com.zimbabeats.core.domain.model.music.Track
import com.zimbabeats.core.domain.repository.MusicRepository
import com.zimbabeats.core.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MusicHomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val sections: List<MusicBrowseSection> = emptyList(),
    val recentlyPlayed: List<Track> = emptyList(),
    val mostPlayed: List<Track> = emptyList(),
    val error: String? = null,
    val parentalControlEnabled: Boolean = false
)

class MusicHomeViewModel(
    private val musicRepository: MusicRepository,
    private val cloudPairingClient: CloudPairingClient
) : ViewModel() {

    // Cloud-based content filter (Firebase) - for family-specific rules
    private val contentFilter get() = cloudPairingClient.contentFilter

    // Global content filter (RemoteConfig) - ALWAYS applies regardless of family linking
    private val remoteConfigManager = RemoteConfigManager()

    companion object {
        private const val TAG = "MusicHomeViewModel"

        // Curated search queries for KIDS MODE (parental control ON)
        private val KIDS_CURATED_SECTIONS = listOf(
            "Kids Songs" to "kids songs nursery rhymes",
            "Disney Music" to "disney songs for kids",
            "Fun & Dance" to "kids dance songs",
            "Lullabies" to "lullaby songs for children",
            "Learning Songs" to "abc learning songs kids"
        )

        // Curated search queries for GENERAL MODE (parental control OFF)
        private val GENERAL_CURATED_SECTIONS = listOf(
            "Top Hits" to "top hits 2024",
            "Pop Music" to "pop music",
            "Chill Vibes" to "chill lofi music",
            "Hip Hop" to "hip hop rap",
            "Rock Classics" to "rock classics",
            "R&B Soul" to "r&b soul music"
        )
    }

    private val _uiState = MutableStateFlow(MusicHomeUiState())
    val uiState: StateFlow<MusicHomeUiState> = _uiState.asStateFlow()

    /**
     * Filter tracks using both Global blocks (RemoteConfig) and Cloud Content Filter (Firebase).
     * Global blocks ALWAYS apply regardless of family linking.
     * Family-specific blocks only apply when linked to a family.
     */
    private fun filterTracksWithBridge(tracks: List<Track>): List<Track> {
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

    /**
     * Filter music browse items using both Global blocks and Cloud Content Filter.
     * Global blocks ALWAYS apply regardless of family linking.
     */
    private fun filterBrowseItemsWithBridge(items: List<MusicBrowseItem>): List<MusicBrowseItem> {
        return items.filter { item ->
            // Extract item properties based on type
            val id: String
            val title: String
            val artistId: String
            val artistName: String
            val albumName: String
            val isExplicit: Boolean

            when (item) {
                is MusicBrowseItem.TrackItem -> {
                    id = item.track.id
                    title = item.track.title
                    artistId = item.track.artistId ?: ""
                    artistName = item.track.artistName
                    albumName = item.track.albumName ?: ""
                    isExplicit = item.track.isExplicit
                }
                is MusicBrowseItem.AlbumItem -> {
                    id = item.album.id
                    title = item.album.title
                    artistId = ""
                    artistName = item.album.artistName
                    albumName = item.album.title
                    isExplicit = false
                }
                is MusicBrowseItem.ArtistItem -> {
                    id = item.artist.id
                    title = item.artist.name
                    artistId = item.artist.id
                    artistName = item.artist.name
                    albumName = ""
                    isExplicit = false
                }
                is MusicBrowseItem.PlaylistItem -> {
                    id = item.playlist.id
                    title = item.playlist.title
                    artistId = ""
                    artistName = item.playlist.author ?: ""
                    albumName = ""
                    isExplicit = false
                }
            }

            // ALWAYS check global blocks first (regardless of family linking)
            val textToCheck = "$title $artistName $albumName"
            val globalKeywordBlock = remoteConfigManager.isGloballyBlocked(textToCheck)
            if (globalKeywordBlock.isBlocked) {
                Log.d(TAG, "Item '$title' blocked by global keyword filter")
                return@filter false
            }

            val globalArtistBlock = remoteConfigManager.isArtistGloballyBlocked(artistId, artistName)
            if (globalArtistBlock.isBlocked) {
                Log.d(TAG, "Item '$title' by '$artistName' blocked by global artist filter")
                return@filter false
            }

            // If linked to family, also apply family-specific rules
            val filter = contentFilter
            if (filter != null) {
                val blockResult = filter.shouldBlockMusicContent(
                    trackId = id,
                    title = title,
                    artistId = artistId,
                    artistName = artistName,
                    albumName = albumName,
                    genre = null,
                    durationSeconds = 0L,
                    isExplicit = isExplicit
                )
                if (blockResult.isBlocked) {
                    Log.d(TAG, "Item '$title' blocked by family filter: ${blockResult.reason}")
                    return@filter false
                }
            }

            true // Not blocked
        }
    }

    init {
        observeBridgeState()
        loadRecentlyPlayed()
        loadMostPlayed()
        startAutoRefresh()
    }

    /**
     * Auto-refresh content every 1 minute
     */
    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(60_000L) // 1 minute
                Log.d(TAG, "Auto-refreshing music content...")
                loadMusicHomeInternal()
            }
        }
    }

    /**
     * Observe cloud pairing state for changes from Firebase
     */
    private fun observeBridgeState() {
        viewModelScope.launch {
            cloudPairingClient.pairingStatus.collect { pairingStatus ->
                val isLinkedToFamily = pairingStatus is PairingStatus.Paired
                val isKidsMode = isLinkedToFamily // Kids mode ON when linked to family

                Log.d(TAG, "Cloud state received - linkedToFamily: $isLinkedToFamily")

                val previousEnabled = _uiState.value.parentalControlEnabled
                val settingsChanged = previousEnabled != isKidsMode

                _uiState.value = _uiState.value.copy(
                    parentalControlEnabled = isKidsMode
                )

                // Refresh content if parental control state changed
                if (settingsChanged) {
                    Log.d(TAG, "Parental settings CHANGED, refreshing content")
                    _uiState.value = _uiState.value.copy(
                        sections = emptyList(),
                        isLoading = true
                    )
                    loadMusicHome()
                } else if (_uiState.value.sections.isEmpty()) {
                    // Initial load
                    Log.d(TAG, "Initial load, loading music content...")
                    loadMusicHome()
                }
            }
        }
    }

    fun loadMusicHome() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = musicRepository.getMusicHome()) {
                is Resource.Success -> {
                    if (result.data.isNotEmpty()) {
                        Log.d(TAG, "Loaded ${result.data.size} sections from browse API")
                        _uiState.value = _uiState.value.copy(
                            sections = result.data,
                            isLoading = false
                        )
                    } else {
                        // Browse API returned empty - load fallback curated content
                        Log.d(TAG, "Browse API returned empty, loading curated content")
                        loadCuratedContent()
                    }
                }
                is Resource.Error -> {
                    Log.e(TAG, "Failed to load music home: ${result.message}, trying curated content")
                    // On error, also try curated content as fallback
                    loadCuratedContent()
                }
                else -> {}
            }
        }
    }

    private suspend fun loadCuratedContent() {
        val isKidsMode = _uiState.value.parentalControlEnabled
        val curatedSections = if (isKidsMode) KIDS_CURATED_SECTIONS else GENERAL_CURATED_SECTIONS
        Log.d(TAG, "Loading curated music sections (kids mode: $isKidsMode)...")

        val sections = curatedSections.mapNotNull { (title, query) ->
            try {
                val result = musicRepository.searchMusic(query, MusicSearchFilter.SONGS)
                if (result is Resource.Success && result.data.isNotEmpty()) {
                    val items = result.data.take(10).mapNotNull { searchResult ->
                        when (searchResult) {
                            is MusicSearchResult.TrackResult -> MusicBrowseItem.TrackItem(searchResult.track)
                            is MusicSearchResult.AlbumResult -> MusicBrowseItem.AlbumItem(searchResult.album)
                            is MusicSearchResult.ArtistResult -> MusicBrowseItem.ArtistItem(searchResult.artist)
                            is MusicSearchResult.PlaylistResult -> MusicBrowseItem.PlaylistItem(searchResult.playlist)
                        }
                    }

                    // Apply Bridge filtering
                    val filteredItems = filterBrowseItemsWithBridge(items)

                    if (filteredItems.isNotEmpty()) {
                        Log.d(TAG, "Loaded ${filteredItems.size} items for section: $title (${items.size - filteredItems.size} filtered)")
                        MusicBrowseSection(title = title, items = filteredItems)
                    } else null
                } else null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load curated section '$title': ${e.message}")
                null
            }
        }

        Log.d(TAG, "Loaded ${sections.size} curated sections")
        _uiState.value = _uiState.value.copy(
            sections = sections,
            isLoading = false,
            error = if (sections.isEmpty()) "No music content available" else null
        )
    }

    private fun loadRecentlyPlayed() {
        viewModelScope.launch {
            musicRepository.getRecentlyPlayed(10).collect { tracks ->
                val filteredTracks = filterTracksWithBridge(tracks)
                _uiState.value = _uiState.value.copy(recentlyPlayed = filteredTracks)
            }
        }
    }

    private fun loadMostPlayed() {
        viewModelScope.launch {
            musicRepository.getMostPlayed(10).collect { tracks ->
                val filteredTracks = filterTracksWithBridge(tracks)
                _uiState.value = _uiState.value.copy(mostPlayed = filteredTracks)
            }
        }
    }

    /**
     * Manual refresh - pull to refresh
     */
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            loadMusicHomeInternal()
            loadRecentlyPlayed()
            loadMostPlayed()
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }

    private suspend fun loadMusicHomeInternal() {
        when (val result = musicRepository.getMusicHome()) {
            is Resource.Success -> {
                if (result.data.isNotEmpty()) {
                    Log.d(TAG, "Loaded ${result.data.size} sections from browse API")
                    _uiState.value = _uiState.value.copy(
                        sections = result.data,
                        isLoading = false
                    )
                } else {
                    loadCuratedContent()
                }
            }
            is Resource.Error -> {
                Log.e(TAG, "Failed to load music home: ${result.message}, trying curated content")
                loadCuratedContent()
            }
            else -> {}
        }
    }
}
