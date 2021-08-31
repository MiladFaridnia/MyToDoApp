package com.faridnia.mytodoapp.util

import androidx.appcompat.widget.SearchView


inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {

    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listener.invoke(newText.orEmpty())
            return true
        }
    })
}