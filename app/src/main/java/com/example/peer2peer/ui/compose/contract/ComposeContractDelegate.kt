package com.example.peer2peer.ui.compose.contract


import com.retailinmotion.ui.compose.contract.EmptyType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ComposeContractDelegate<State, Event, Effect> internal constructor(
    initialUiState: State,
    initialUiResult: UIResult<State>
) : ComposeContract<State, Event, Effect> {

    private val _uiState = MutableStateFlow(initialUiState)
    override val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _uiResult = MutableStateFlow(initialUiResult)
    override val uiResult: StateFlow<UIResult<State>> = _uiResult.asStateFlow()

    private val _effect by lazy { Channel<Effect>() }
    override val effect: Flow<Effect> by lazy { _effect.receiveAsFlow() }

    override fun onEvent(event: Event) = Unit

    override fun setLoadedResult(block: State.() -> State) {
        _uiState.update(block)
        _uiResult.update { UIResult.Loaded(_uiState.value) }
    }

    override fun setLoadingResult(loadingType: LoadingType) {
        _uiResult.update { UIResult.Loading(loadingType) }
    }

    override fun setErrorResult(errorType: ErrorType) {
        _uiResult.update { UIResult.Error(errorType) }
    }

    override fun setEmptyResult(emptyType: EmptyType) {
        _uiResult.update { UIResult.Empty(emptyType) }
    }

    override fun CoroutineScope.emitEffect(effect: Effect) {
        this.launch { _effect.send(effect) }
    }
}

fun <State, Event, Effect> composeContractDelegate(
    initialUiState: State,
    initialUiResult: UIResult<State> = UIResult.Loading(LoadingType.WithTitle()),
): ComposeContract<State, Event, Effect> = ComposeContractDelegate(initialUiState, initialUiResult)