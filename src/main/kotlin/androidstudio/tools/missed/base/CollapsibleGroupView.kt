package androidstudio.tools.missed.base

import com.intellij.ui.dsl.builder.panel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancelChildren
import javax.swing.JPanel

abstract class CollapsibleGroupView<T : ViewModel>(
    override val viewModel: T?
) : ViewMaster<T>(viewModel) {

    private var isExpanded: Boolean? = false

    open fun onExpand() {}

    open fun onCollapse() {
        viewScope.coroutineContext.cancelChildren(CancellationException("onCollapse"))
        viewModel?.onClear()
    }

    fun setContent(title: String, vararg view: JPanel) {
        add(
            panel {
                collapsibleGroup(title) {
                    view.forEach {
                        row { cell(it) }
                    }
                }.addExpandedListener { isExpanded ->
                    if (isExpanded) onExpand() else onCollapse()
                    this@CollapsibleGroupView.isExpanded = isExpanded
                }
            }
        )
    }
}
