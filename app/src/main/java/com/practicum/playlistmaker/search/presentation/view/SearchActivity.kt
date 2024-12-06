package com.practicum.playlistmaker.search.presentation.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.constants.AppConstants.TRACK_SHARE_KEY
import com.practicum.playlistmaker.common.constants.LogTags
import com.practicum.playlistmaker.common.constants.PrefsConstants
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.presentation.view.PlayerActivity
import com.practicum.playlistmaker.search.data.source.local.SearchHistory
import com.practicum.playlistmaker.search.data.model.Track
import com.practicum.playlistmaker.search.data.model.TrackResponse
import com.practicum.playlistmaker.search.data.source.remote.RetrofitClient
import com.practicum.playlistmaker.search.presentation.adapter.SearchHistoryAdapter
import com.practicum.playlistmaker.search.presentation.adapter.TrackAdapter
import com.practicum.playlistmaker.settings.presentation.view.SettingsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private val tracks: MutableList<Track> = mutableListOf()
    private var inputText: String = DEFAULT_INPUT_TEXT
    private var lastQuery: String = DEFAULT_INPUT_TEXT
    private val sharedPreferences by lazy {
        getSharedPreferences(PrefsConstants.PREFS_NAME, MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SearchHistory.init(sharedPreferences)

        setupUI()
        loadSearchHistory()
        observeSearchHistory()
    }

    private fun launchPlayer(track: Track) {
        val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
        intent.putExtra(TRACK_SHARE_KEY, track)
        startActivity(intent)
    }

    private fun observeSearchHistory() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                SearchHistory.historyFlow.collect { history ->
                    updateSearchHistoryAdapter(history)
                }
            }
        }
    }

    private fun setupUI() {
        setupSearchInput()
        setupButtons()
        with(binding) {
            searchRecycler.layoutManager = LinearLayoutManager(this@SearchActivity)
            searchRecycler.adapter = TrackAdapter(tracks) { track ->
                SearchHistory.addTrack(track)
                launchPlayer(track)
            }
        }
    }

    private fun setupButtons() {
        with(binding) {
            topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
            searchUpdateButton.setOnClickListener { searchForTracks(lastQuery) }
            clearInputButton.setOnClickListener { clearSearch() }
            clearHistoryButton.setOnClickListener { clearSearchHistory() }
        }
    }

    private fun setupSearchInput() {
        binding.inputSearch.apply {
            setText(inputText)

            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchForTracks(text.toString())
                    true
                } else false
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateUIForSearchInput(s)
                }

                override fun afterTextChanged(s: Editable?) {
                    inputText = s.toString()
                }
            })

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) updateSearchHistoryVisibility()
            }
        }
    }

    private fun clearSearch() {
        with(binding) {
            inputSearch.text.clear()
            searchPlaceholderViewGroup.visibility = View.GONE
            hideKeyboard()
            tracks.clear()
            searchRecycler.adapter?.notifyDataSetChanged()
        }
    }

    private fun clearSearchHistory() {
        SearchHistory.clearHistory()
    }

    private fun updateUIForSearchInput(s: CharSequence?) {
        with(binding) {
            if (s.isNullOrEmpty()) {
                clearInputButton.visibility = View.GONE
                searchRecycler.visibility = View.GONE
                searchPlaceholderViewGroup.visibility = View.GONE
                updateSearchHistoryVisibility()
            } else {
                clearInputButton.visibility = View.VISIBLE
                searchHistoryViewGroup.visibility = View.GONE
            }
        }
    }

    private fun loadSearchHistory() {
        val history = SearchHistory.getHistory()
        with(binding) {
            searchHistoryRecycler.layoutManager = LinearLayoutManager(this@SearchActivity)
            searchHistoryRecycler.adapter = SearchHistoryAdapter(history) { track ->
                launchPlayer(track)
            }
            searchHistoryViewGroup.visibility =
                if (inputSearch.text.isNullOrEmpty() && inputSearch.hasFocus() && history.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun updateSearchHistoryAdapter(history: List<Track>) {
        with(binding) {
            if (!::searchHistoryAdapter.isInitialized) {
                searchHistoryAdapter = SearchHistoryAdapter(history) { track ->
                    launchPlayer(track)
                }
                searchHistoryRecycler.layoutManager = LinearLayoutManager(this@SearchActivity)
                searchHistoryRecycler.adapter = searchHistoryAdapter
            } else {
                searchHistoryAdapter.updateTracks(history)
            }
            updateSearchHistoryVisibility(history)
        }

    }

    private fun updateSearchHistoryVisibility(history: List<Track> = SearchHistory.getHistory()) {
        with(binding) {
            searchHistoryViewGroup.visibility =
                if (inputSearch.text.isNullOrEmpty() && inputSearch.hasFocus() && history.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        view?.let {
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun searchForTracks(term: String) {
        lastQuery = term
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val trackResponse = RetrofitClient.iTunesApiService.searchTracks(term)
                withContext(Dispatchers.Main) {
                    handleTrackResponse(trackResponse)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    setNetworkErrorPlaceholder()
                    Log.e(LogTags.NETWORK_STATUS, "No network connection: ${e.message}")
                }
            }
        }
    }

    private fun handleTrackResponse(trackResponse: TrackResponse) {
        if (trackResponse.resultCount > ZERO_NUM) {
            showFoundTracks(trackResponse)
            Log.d(LogTags.API_RESPONSE, "Tracks found: ${trackResponse.resultCount}")
        } else {
            setNotFoundPlaceholder()
            Log.d(LogTags.API_RESPONSE, "No tracks found")
        }
    }

    private fun showFoundTracks(trackResponse: TrackResponse) {
        tracks.clear()
        tracks.addAll(trackResponse.results)

        with(binding) {
            searchPlaceholderViewGroup.visibility = View.GONE
            searchRecycler.visibility = View.VISIBLE
            searchRecycler.adapter?.notifyDataSetChanged()
        }
    }

    private fun setPlaceholder(
        placeholderImageResId: Int,
        placeholderText: String,
        isUpdateButtonVisible: Boolean
    ) {
        with(binding) {
            searchPlaceholderImage.setImageResource(placeholderImageResId)
            searchPlaceholderText.text = placeholderText
            searchRecycler.visibility = View.GONE
            searchPlaceholderViewGroup.visibility = View.VISIBLE
            searchUpdateButton.visibility = if (isUpdateButtonVisible) View.VISIBLE else View.GONE
        }
    }

    private fun setNotFoundPlaceholder() {
        setPlaceholder(
            placeholderImageResId = R.drawable.ic_not_found_placeholder,
            placeholderText = getString(R.string.search_placeholder_not_found),
            isUpdateButtonVisible = false
        )
    }

    private fun setNetworkErrorPlaceholder() {
        setPlaceholder(
            placeholderImageResId = R.drawable.ic_no_internet_placeholder,
            placeholderText = getString(R.string.search_placeholder_network_error),
            isUpdateButtonVisible = true
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_INPUT_TEXT, inputText)
        outState.putString(SAVED_LAST_QUERY, lastQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(SAVED_INPUT_TEXT, DEFAULT_INPUT_TEXT)
        lastQuery = savedInstanceState.getString(SAVED_LAST_QUERY, DEFAULT_INPUT_TEXT)
    }

    companion object {
        const val ZERO_NUM = 0
        const val DEFAULT_INPUT_TEXT = ""
        const val SAVED_INPUT_TEXT = "SAVED_INPUT_TEXT"
        const val SAVED_LAST_QUERY = "SAVED_LAST_QUERY"
    }
}