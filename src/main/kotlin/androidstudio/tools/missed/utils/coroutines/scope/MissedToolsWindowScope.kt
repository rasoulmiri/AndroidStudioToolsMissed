package androidstudio.tools.missed.utils.coroutines.scope

import androidstudio.tools.missed.utils.coroutines.exception.coroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

internal val missedToolsWindowScope = CoroutineScope(Dispatchers.IO + SupervisorJob() + coroutineExceptionHandler)
