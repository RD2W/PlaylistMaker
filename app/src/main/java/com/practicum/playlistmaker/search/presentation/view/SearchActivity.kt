package com.practicum.playlistmaker.search.presentation.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.data.model.Track
import com.practicum.playlistmaker.search.data.model.TrackResponse
import com.practicum.playlistmaker.search.data.source.remote.RetrofitClient
import com.practicum.playlistmaker.search.presentation.adapter.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val tracks: MutableList<Track> = mutableListOf()
    private var inputText: String = DEFAULT_INPUT_TEXT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

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
                val lastQuery = inputSearch.text.toString()
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
                binding.clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
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
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(SAVED_INPUT_TEXT, DEFAULT_INPUT_TEXT)
    }

    private fun hideKeyboard() {
        val view = currentFocus
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        if (view != null) inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun searchForTracks(term: String) {
        RetrofitClient.iTunesApiService.searchTracks(term).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful) {
                    val trackResponse = response.body()
                    trackResponse?.let {
                        handleTrackResponse(it)
                    } ?: run {
                        Log.d("ServerResponse", "The response from the server is empty")
                    }
                } else {
                    setNetworkErrorPlaceholder()
                    Log.e("ServerResponse", "Error code: ${response.code()}, result: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                setNetworkErrorPlaceholder()
                hideKeyboard()
                Log.e("NetworkError", "No network connection: ${t.message}")
            }
        })
    }

    private fun handleTrackResponse(trackResponse: TrackResponse) {
        if (trackResponse.resultCount > 0) {
            showFoundTracks(trackResponse)
            Log.d("ServerResponse", "Tracks found: ${trackResponse.resultCount}")
        } else {
            setNotFoundPlaceholder()
            Log.d("ServerResponse", "No tracks found")
        }
    }

    private fun showFoundTracks(trackResponse: TrackResponse) {
        with(binding) {
            searchPlaceholderImage.visibility = View.GONE
            searchPlaceholderText.visibility = View.GONE
            searchUpdateButton.visibility = View.GONE
            searchRecycler.visibility = View.VISIBLE
        }

        tracks.clear()
        tracks.addAll(trackResponse.results)
        binding.searchRecycler.adapter?.notifyDataSetChanged()
    }

    private fun setPlaceholder(placeholderImageResId: Int, placeholderText: String, isUpdateButtonVisible: Boolean) {
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
        const val SAVED_INPUT_TEXT = "SAVED_INPUT_TEXT"
        const val DEFAULT_INPUT_TEXT = ""
    }
}