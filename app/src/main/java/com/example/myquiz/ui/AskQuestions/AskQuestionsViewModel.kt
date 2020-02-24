package com.example.myquiz.ui.AskQuestions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AskQuestionsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "API INNEHÅLL"
    }
    val text: LiveData<String> = _text
}