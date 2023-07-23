package androidstudio.tools.missed.utils.coroutines.exception

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.job
import org.koin.core.component.getScopeName

val coroutineExceptionHandler = CoroutineExceptionHandler { context, exception ->
    println("🚨CoroutineExceptionHandler got $exception \n")
    println("🚨Context:Name: ${context.getScopeName()} \n")
    println("🚨Context:Job: ${context.job} \n")
    println("🚨Context: $context \n")
    exception.printStackTrace()
}
