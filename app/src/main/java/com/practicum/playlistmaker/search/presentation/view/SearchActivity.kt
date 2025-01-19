package com.practicum.playlistmaker.search.presentation.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.constants.AppConstants.CLICK_DEBOUNCE_DELAY_MILLIS
import com.practicum.playlistmaker.common.constants.AppConstants.SEARCH_DEBOUNCE_DELAY_MILLIS
import com.practicum.playlistmaker.common.constants.AppConstants.TRACK_SHARE_KEY
import com.practicum.playlistmaker.common.constants.LogTags
import com.practicum.playlistmaker.common.constants.PrefsConstants
import com.practicum.playlistmaker.common.domain.mapper.impl.TrackMapperImpl
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.presentation.view.PlayerActivity
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.common.utils.NetworkUtils
import com.practicum.playlistmaker.search.di.SearchDependencyCreator
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.presentation.adapter.SearchHistoryAdapter
import com.practicum.playlistmaker.search.presentation.adapter.TrackAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class SearchActivity : AppCompatActivity() {

    private var _binding: ActivitySearchBinding? = null
    private val binding: ActivitySearchBinding
        get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }

    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private val tracks: MutableList<Track> = mutableListOf()
    private val handler = Handler(Looper.getMainLooper())
    private val isClickAllowed = AtomicBoolean(true)
    private var inputText: String = DEFAULT_INPUT_TEXT
    private var lastQuery: String = DEFAULT_INPUT_TEXT
    private val searchRunnable = Runnable { searchForTracks(inputText) }

    private val sharedPreferences by lazy {
        getSharedPreferences(PrefsConstants.PREFS_NAME, MODE_PRIVATE)
    }

    private val tracksInteractor: TracksInteractor by lazy {
        SearchDependencyCreator.provideTrackInteractor()
    }

    private val searchHistoryInteractor: SearchHistoryInteractor by lazy {
        SearchDependencyCreator.provideSearchHistoryInteractor(sharedPreferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeSearchHistory()
    }

    private fun showProgressBar() {
        with(binding) {
            pbSearchProgressBar.isVisible = true
            searchRecycler.isVisible = false
            searchPlaceholderViewGroup.isVisible = false
        }
    }

    private fun searchDebounce(term: String) {
        if (term.isNotBlank()) {
            handler.removeCallbacks(searchRunnable)
            inputText = term
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MILLIS)
        }
    }

    private fun clickDebounce(): Boolean {
        return if (isClickAllowed.get()) {
            isClickAllowed.set(false)
            handler.postDelayed({ isClickAllowed.set(true) }, CLICK_DEBOUNCE_DELAY_MILLIS)
            true
        } else false
    }

    private fun launchPlayer(track: Track) {
        if (clickDebounce()) {
            val trackParcel = TrackMapperImpl.toParcel(track)
            val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
            intent.putExtra(TRACK_SHARE_KEY, trackParcel)
            startActivity(intent)
        }
    }

    private fun observeSearchHistory() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchHistoryInteractor.getHistory().collect { history ->
                    updateSearchHistoryAdapter(history)
                }
            }
        }
    }

    private fun observeSearchHistoryVisibility() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchHistoryInteractor.getHistory().collect { history ->
                    updateSearchHistoryVisibility(history)
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
                searchHistoryInteractor.addTrack(track)
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
                    searchDebounce(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {
                    inputText = s.toString()
                }
            })

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) observeSearchHistoryVisibility()
            }
        }
    }

    private fun clearSearch() {
        with(binding) {
            inputSearch.text.clear()
            inputText = ""
            searchPlaceholderViewGroup.visibility = View.GONE
            hideKeyboard()
            tracks.clear()
            searchRecycler.adapter?.notifyDataSetChanged()
        }
    }

    private fun clearSearchHistory() {
        searchHistoryInteractor.clearHistory()
    }

    private fun updateUIForSearchInput(s: CharSequence?) {
        with(binding) {
            if (s.isNullOrEmpty()) {
                clearInputButton.visibility = View.GONE
                searchRecycler.visibility = View.GONE
                searchPlaceholderViewGroup.visibility = View.GONE
                observeSearchHistoryVisibility()
            } else {
                clearInputButton.visibility = View.VISIBLE
                searchHistoryViewGroup.visibility = View.GONE
            }
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

    private fun updateSearchHistoryVisibility(history: List<Track>) {
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
        if (term.isBlank()) return
        lastQuery = term
        showProgressBar()
        if (NetworkUtils.isNetworkAvailable(this)) {
            lifecycleScope.launch(Dispatchers.IO) {
                tracksInteractor.searchTracks(term) { foundTracks ->
                    lifecycleScope.launch(Dispatchers.Main) {
                        handleTrackResponse(foundTracks)
                    }
                }
            }

        } else {
            setNetworkErrorPlaceholder()
            Log.e(LogTags.NETWORK_STATUS, "No network connection")
        }
    }

    private fun handleTrackResponse(foundTracks: List<Track>) {
        if (foundTracks.isNotEmpty()) {
            showFoundTracks(foundTracks.toMutableList())
            Log.d(LogTags.API_RESPONSE, "Tracks found: ${foundTracks.size}")
        } else {
            setNotFoundPlaceholder()
            Log.d(LogTags.API_RESPONSE, "No tracks found")
        }
    }

    private fun showFoundTracks(foundTracks: MutableList<Track>) {
        tracks.clear()
        tracks.addAll(foundTracks)

        with(binding) {
            pbSearchProgressBar.isVisible = false
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
            pbSearchProgressBar.isVisible = false
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

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    companion object {
        const val DEFAULT_INPUT_TEXT = ""
        const val SAVED_INPUT_TEXT = "SAVED_INPUT_TEXT"
        const val SAVED_LAST_QUERY = "SAVED_LAST_QUERY"
    }
}