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
import com.practicum.playlistmaker.search.data.model.Track
import com.practicum.playlistmaker.search.data.model.TrackResponse
import com.practicum.playlistmaker.search.data.source.remote.RetrofitClient
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

        with(binding) {
            topAppBar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            inputSearch.setText(inputText)

            inputSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchForTracks(inputSearch.text.toString())
                    true
                } else false
            }

            searchUpdateButton.setOnClickListener {
                searchForTracks(lastQuery)
            }

            clearIcon.setOnClickListener {
                inputSearch.setText("")
                searchPlaceholderImage.visibility = View.GONE
                searchPlaceholderText.visibility = View.GONE
                searchUpdateButton.visibility = View.GONE
                hideKeyboard()
                tracks.clear()
                searchRecycler.adapter?.notifyDataSetChanged()
            }

            searchRecycler.apply {
                layoutManager = LinearLayoutManager(this@SearchActivity)
                adapter = TrackAdapter(tracks)
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                with(binding) {
                    if (s.isNullOrEmpty()) {
                        clearIcon.visibility = View.GONE
                        searchPlaceholderImage.visibility = View.GONE
                        searchPlaceholderText.visibility = View.GONE
                        searchUpdateButton.visibility = View.GONE
                        searchRecycler.visibility = View.GONE
                    } else {
                        clearIcon.visibility = View.VISIBLE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                inputText = s.toString()
            }
        }

        binding.inputSearch.addTextChangedListener(simpleTextWatcher)
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

    private fun hideKeyboard() {
        val view = currentFocus
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        if (view != null) inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        if (view is EditText) view.clearFocus()
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
        if (trackResponse.resultCount > 0) {
            showFoundTracks(trackResponse)
            Log.d(LogTags.API_RESPONSE, "Tracks found: ${trackResponse.resultCount}")
        } else {
            setNotFoundPlaceholder()
            Log.d(LogTags.API_RESPONSE, "No tracks found")
        }
    }

    private fun showFoundTracks(trackResponse: TrackResponse) {
        tracks.apply {
            clear()
            addAll(trackResponse.results)
        }

        with(binding) {
            searchPlaceholderImage.visibility = View.GONE
            searchPlaceholderText.visibility = View.GONE
            searchUpdateButton.visibility = View.GONE
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
            searchRecycler.visibility = View.GONE
            searchPlaceholderImage.setImageResource(placeholderImageResId)
            searchPlaceholderText.text = placeholderText
            searchPlaceholderImage.visibility = View.VISIBLE
            searchPlaceholderText.visibility = View.VISIBLE
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

    companion object {
        const val DEFAULT_INPUT_TEXT = ""
        const val SAVED_INPUT_TEXT = "SAVED_INPUT_TEXT"
        const val SAVED_LAST_QUERY = "SAVED_LAST_QUERY"
    }
}