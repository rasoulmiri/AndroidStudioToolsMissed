import androidstudio.tools.missed.base.ViewMaster
import androidstudio.tools.missed.base.ViewModel
import androidstudio.tools.missed.utils.coroutines.exception.coroutineExceptionHandler
import io.mockk.mockkStatic
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalStdlibApi::class)
class ViewMasterTest {

    private lateinit var viewMaster: ViewMaster<ViewModel>

    @Before
    fun setup() {
        viewMaster = object : ViewMaster<ViewModel>(null) {}
    }

    @Test
    fun `viewScope should use Dispatchers Swing`() {
        // Arrange
        mockkStatic("kotlinx.coroutines.DispatchersKt")

        // Act
        val viewScope = viewMaster.viewScope

        // Assert
        assertEquals(viewScope.coroutineContext[CoroutineDispatcher], Dispatchers.Swing)
    }

    @Test
    fun `viewScope should use SupervisorJob`() {
        // Act
        val viewScope = viewMaster.viewScope

        // Assert
        assertEquals(viewScope.coroutineContext[Job]?.javaClass, SupervisorJob()::class.java)
    }

    @Test
    fun `viewScope should use coroutineExceptionHandler`() {
        // Act
        val viewScope = viewMaster.viewScope

        // Assert
        assertEquals(viewScope.coroutineContext[CoroutineExceptionHandler], coroutineExceptionHandler)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `viewModel should be initialized correctly`() {
        // Arrange
        val testViewModel = ViewModel(UnconfinedTestDispatcher())
        val viewMasterWithViewModel = object : ViewMaster<ViewModel>(testViewModel) {}

        // Act
        val viewModel = viewMasterWithViewModel.viewModel

        // Assert
        assertEquals(testViewModel, viewModel)
    }
}
