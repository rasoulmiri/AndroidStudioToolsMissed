package androidstudio.tools.missed.base

import androidstudio.tools.missed.utils.coroutines.exception.coroutineExceptionHandler
import io.mockk.mockkStatic
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var viewModel: ViewModel


    @Before
    fun setup() {
        viewModel = ViewModel(testDispatcher)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `viewScope should use Dispatchers Swing`() {
        // Arrange
        mockkStatic("kotlinx.coroutines.DispatchersKt")

        // Act
        val viewScope = viewModel.viewModelScope

        // Assert
        Assert.assertEquals(viewScope.coroutineContext[CoroutineDispatcher], testDispatcher)
    }

    @Test
    fun `viewScope should use SupervisorJob`() {
        // Act
        val viewScope = viewModel.viewModelScope

        // Assert
        Assert.assertEquals(viewScope.coroutineContext[Job]?.javaClass, SupervisorJob()::class.java)
    }

    @Test
    fun `viewScope should use coroutineExceptionHandler`() {
        // Act
        val viewScope = viewModel.viewModelScope

        // Assert
        Assert.assertEquals(viewScope.coroutineContext[CoroutineExceptionHandler], coroutineExceptionHandler)
    }

    @Test
    fun `onClear should cancel all child coroutines in viewModelScope`() = testScope.runTest {
        // Arrange
        val childCoroutine1: Job = launch(viewModel.viewModelScope.coroutineContext) {
            // Some coroutine logic
            delay(1000)
        }

        val childCoroutine2: Job = launch(viewModel.viewModelScope.coroutineContext) {
            // Some coroutine logic
            delay(2000)
        }

        // Act
        viewModel.onClear()

        // Assert
        assertTrue(childCoroutine1.isCancelled)
        assertTrue(childCoroutine2.isCancelled)
    }
}
