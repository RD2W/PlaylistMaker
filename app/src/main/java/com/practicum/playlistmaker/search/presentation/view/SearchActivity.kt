package com.practicum.playlistmaker.search.presentation.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.constants.AppConstants.TRACK_SHARE_KEY
import com.practicum.playlistmaker.common.domain.mapper.impl.TrackMapperImpl
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.presentation.view.PlayerActivity
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.search.presentation.adapter.SearchHistoryAdapter
import com.practicum.playlistmaker.search.presentation.adapter.TrackAdapter
import com.practicum.playlistmaker.search.presentation.state.SearchScreenState
import com.practicum.playlistmaker.search.presentation.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private var _binding: ActivitySearchBinding? = null
    private val binding: ActivitySearchBinding
        get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }

    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private lateinit var foundTrackAdapter: TrackAdapter

    private val searchViewModel: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        observeViewModel()
    }

    private fun observeViewModel() {
        searchViewModel.searchScreenState.observe(this) { state ->
            when (state) {
                is SearchScreenState.Idle -> {
                    // Ничего не делать, начальное состояние
                }

                is SearchScreenState.Loading -> showProgressBar()
                is SearchScreenState.Content -> showFoundTracks(state.tracks)
                is SearchScreenState.NotFound -> showNotFoundPlaceholder()
                is SearchScreenState.NetworkError -> showNetworkErrorPlaceholder()
                is SearchScreenState.SearchHistory -> showSearchHistory(state.history)
            }
        }
    }

    private fun showProgressBar() {
        with(binding) {
            pbSearchProgressBar.isVisible = true
            searchRecycler.isVisible = false
            searchPlaceholderViewGroup.isVisible = false
        }
    }

    private fun showFoundTracks(foundTracks: List<Track>) {
        with(binding) {
            pbSearchProgressBar.isVisible = false
            searchPlaceholderViewGroup.visibility = View.GONE
            searchRecycler.visibility = View.VISIBLE
        }
        foundTrackAdapter.updateTracks(foundTracks)
    }

    private fun showNotFoundPlaceholder() {
        setPlaceholder(
            placeholderImageResId = R.drawable.ic_not_found_placeholder,
            placeholderText = getString(R.string.search_placeholder_not_found),
            isUpdateButtonVisible = false
        )
    }

    private fun showNetworkErrorPlaceholder() {
        setPlaceholder(
            placeholderImageResId = R.drawable.ic_no_internet_placeholder,
            placeholderText = getString(R.string.search_placeholder_network_error),
            isUpdateButtonVisible = true
        )
    }

    private fun showSearchHistory(history: List<Track>) {
        searchHistoryAdapter.updateTracks(history)
        updateSearchHistoryVisibility(history)
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

    private fun launchPlayer(track: Track) {
        if (searchViewModel.clickDebounce()) {
            val trackParcel = TrackMapperImpl.toParcel(track)
            val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
            intent.putExtra(TRACK_SHARE_KEY, trackParcel)
            startActivity(intent)
        }
    }

    private fun setupUI() {
        setupSearchInput()
        setupButtons()
        initializeSearchRecycler()
        initializeHistoryRecycler()
    }

    private fun initializeSearchRecycler() {
        if (!::foundTrackAdapter.isInitialized) {
            foundTrackAdapter = TrackAdapter() { track ->
                searchViewModel.addTrackToHistory(track)
                launchPlayer(track)
            }
        }
        with(binding) {
            searchRecycler.adapter = foundTrackAdapter
            searchRecycler.layoutManager = LinearLayoutManager(this@SearchActivity)
        }
    }

    private fun initializeHistoryRecycler() {
        if (!::searchHistoryAdapter.isInitialized) {
            searchHistoryAdapter = SearchHistoryAdapter() { track ->
                launchPlayer(track)
            }
        }
        with(binding) {
            searchHistoryRecycler.adapter = searchHistoryAdapter
            searchHistoryRecycler.layoutManager = LinearLayoutManager(this@SearchActivity)
        }
    }

    private fun setupButtons() {
        with(binding) {
            topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
            searchUpdateButton.setOnClickListener { searchViewModel.researchDebounce() }
            clearInputButton.setOnClickListener { clearSearch() }
            clearHistoryButton.setOnClickListener { searchViewModel.clearSearchHistory() }
        }
    }

    private fun setupSearchInput() {
        binding.inputSearch.apply {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchViewModel.researchDebounce()
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
                    searchViewModel.searchDebounce(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) updateSearchHistoryVisibility(searchHistoryAdapter.getCurrentHistory())
            }
        }
    }

    private fun updateSearchHistoryVisibility(history: List<Track>) {
        with(binding) {
            searchHistoryViewGroup.visibility =
                if (inputSearch.text.isNullOrEmpty() && inputSearch.hasFocus() && history.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun clearSearch() {
        with(binding) {
            inputSearch.text.clear()
            searchRecycler.visibility = View.GONE
            searchPlaceholderViewGroup.visibility = View.GONE
            foundTrackAdapter.clearTracks()
        }
        hideKeyboard()
    }

    private fun updateUIForSearchInput(s: CharSequence?) {
        with(binding) {
            if (s.isNullOrEmpty()) {
                clearInputButton.visibility = View.GONE
                searchRecycler.visibility = View.GONE
                searchPlaceholderViewGroup.visibility = View.GONE
                updateSearchHistoryVisibility(searchHistoryAdapter.getCurrentHistory())
            } else {
                clearInputButton.visibility = View.VISIBLE
                searchHistoryViewGroup.visibility = View.GONE
            }
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        view?.let {
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}