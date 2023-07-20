package androidstudio.tools.missed.base

import androidstudio.tools.missed.utils.coroutines.exception.coroutineExceptionHandler
import com.intellij.openapi.ui.VerticalFlowLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.swing.Swing
import javax.swing.JPanel

abstract class ViewMaster<T : ViewModel>(
    open val viewModel: T?
) : JPanel(VerticalFlowLayout()) {

    val viewScope = CoroutineScope(Dispatchers.Swing + SupervisorJob() + coroutineExceptionHandler)
}
