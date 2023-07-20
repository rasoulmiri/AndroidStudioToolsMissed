package androidstudio.tools.missed.base

import androidstudio.tools.missed.utils.coroutines.exception.coroutineExceptionHandler
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren

open class ViewModel(private val coroutineDispatcher: CoroutineDispatcher) {

    val viewModelScope = CoroutineScope(coroutineDispatcher + SupervisorJob() + coroutineExceptionHandler)

    fun onClear() {
        viewModelScope.coroutineContext.cancelChildren(CancellationException("onClear called"))
    }
}
