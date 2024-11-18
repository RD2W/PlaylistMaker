package com.practicum.playlistmaker.search.presentation.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.constants.LogTags
import com.practicum.playlistmaker.common.constants.PrefsConstants
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.data.model.SearchHistory
import com.practicum.playlistmaker.search.data.model.Track
import com.practicum.playlistmaker.search.data.model.TrackResponse
import com.practicum.playlistmaker.search.data.source.remote.RetrofitClient
import com.practicum.playlistmaker.search.presentation.adapter.SearchHistoryAdapter
import com.practicum.playlistmaker.search.presentation.adapter.TrackAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
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
    }

    private fun setupUI() {
        with(binding) {
            topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
            inputSearch.setText(inputText)
            inputSearch.requestFocus()

            inputSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchForTracks(inputSearch.text.toString())
                    true
                } else false
            }

            searchUpdateButton.setOnClickListener { searchForTracks(lastQuery) }
            clearIcon.setOnClickListener { clearSearch() }
            clearHistoryButton.setOnClickListener { clearSearchHistory() }

            searchRecycler.layoutManager = LinearLayoutManager(this@SearchActivity)
            searchRecycler.adapter = TrackAdapter(tracks) { track ->
                SearchHistory.addTrack(track)
                Log.d("AddTrackInHistory", "В историю поиска добавлен трек: ${track.trackName}")
            }

            inputSearch.addTextChangedListener(object : TextWatcher {
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

            inputSearch.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) updateSearchHistoryVisibility()
            }
        }
    }

    private fun clearSearch() {
        with(binding) {
            inputSearch.setText("")
            searchPlaceholderViewGroup.visibility = View.GONE
            hideKeyboard()
            tracks.clear()
            searchRecycler.adapter?.notifyDataSetChanged()
            loadSearchHistory()
        }
    }

    private fun clearSearchHistory() {
        SearchHistory.clearHistory()
        loadSearchHistory()
    }

    private fun updateUIForSearchInput(s: CharSequence?) {
        with(binding) {
            if (s.isNullOrEmpty()) {
                clearIcon.visibility = View.GONE
                searchRecycler.visibility = View.GONE
                searchPlaceholderViewGroup.visibility = View.GONE
                loadSearchHistory()
            } else {
                clearIcon.visibility = View.VISIBLE
                searchHistoryViewGroup.visibility = View.GONE
            }
        }
    }


    private fun loadSearchHistory() {
        val history = SearchHistory.getHistory()
        with(binding) {
            searchHistoryRecycler.layoutManager = LinearLayoutManager(this@SearchActivity)
            searchHistoryRecycler.adapter = SearchHistoryAdapter(history)
            searchHistoryViewGroup.visibility =
                if (inputSearch.text.isNullOrEmpty() && inputSearch.hasFocus() && history.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun updateSearchHistoryVisibility() {
        with(binding) {
            searchHistoryViewGroup.visibility =
                if (inputSearch.text.isNullOrEmpty() && inputSearch.hasFocus()) View.VISIBLE else View.GONE
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        if (view != null) {
            inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
            if (view is EditText) view.clearFocus()
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