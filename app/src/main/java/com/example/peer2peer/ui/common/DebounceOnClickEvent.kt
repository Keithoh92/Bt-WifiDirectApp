package com.example.peer2peer.ui.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class DebounceOnClickEvent(private val coroutineScope: CoroutineScope) {
    private val debounceState = MutableStateFlow { }
    private var job: Job? = null

    init {
        cancel()
        start()
    }

    private fun start() {
        job = coroutineScope.launch {
            debounceState
                .debounce(300)
                .collect { onClick ->
                    onClick.invoke()
                }
        }
    }

    fun onClick(onClick: () -> Unit) {
        debounceState.value = onClick
    }

    private fun cancel() = job?.cancel()
}