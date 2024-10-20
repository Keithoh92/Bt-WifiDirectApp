package com.example.peer2peer.ui.compose.contract

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import com.retailinmotion.ui.compose.contract.EmptyType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

interface ComposeContract<State, Event, Effect> {
    val uiState: StateFlow<State>
    val uiResult: StateFlow<UIResult<State>>
    val effect: Flow<Effect>

    fun onEvent(event: Event)

    fun setLoadedResult(block: State.() -> State)

    fun setLoadingResult(loadingType: LoadingType)

    fun setErrorResult(errorType: ErrorType)

    fun setEmptyResult(emptyType: EmptyType)

    fun CoroutineScope.emitEffect(effect: Effect)
}

@Stable
@Composable
fun <State, Event, Effect> ComposeContract<State, Event, Effect>.unpack() = Triple(
    uiState.collectAsState().value, ::onEvent, effect
)

@Stable
@Composable
fun <State, Event, Effect> ComposeContract<State, Event, Effect>.unpackWithUiResult() = Triple(
    uiResult.collectAsState().value, ::onEvent, effect
)

@Composable
fun <Effect> CollectSideEffect(sideEffect: Flow<Effect>, onSideEffect: (Effect) -> Unit) {
    LaunchedEffect(key1 = Unit) {
        sideEffect.collectLatest {
            onSideEffect(it)
        }
    }
}