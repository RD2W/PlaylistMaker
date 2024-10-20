package com.practicum.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    var inputText: String = AMOUNT_DEF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        binding.apply {
            topAppBar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            inputSearch.setText(inputText)

            clearIcon.setOnClickListener {
                inputSearch.setText("")
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(clearIcon.windowToken, 0)
            }

            searchRecycler.apply {
                layoutManager = LinearLayoutManager(this@SearchActivity)
                adapter = TrackAdapter(MockTrackList.getTracks())
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                inputText.plus(s)
            }
        }
        binding.inputSearch.addTextChangedListener(simpleTextWatcher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_AMOUNT, inputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(TEXT_AMOUNT, AMOUNT_DEF)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    companion object {
        const val TEXT_AMOUNT = "TEXT_AMOUNT"
        const val AMOUNT_DEF = ""
    }
}