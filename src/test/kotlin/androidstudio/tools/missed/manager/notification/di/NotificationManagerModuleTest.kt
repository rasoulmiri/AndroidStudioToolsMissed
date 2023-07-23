package androidstudio.tools.missed.manager.notification.di


import androidstudio.tools.missed.manager.notification.NotificationManager
import androidstudio.tools.missed.manager.resource.di.resourceManagerModule
import androidstudio.tools.missed.utils.coroutines.scope.di.applicationCoroutinesScopeModule
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

class NotificationManagerModuleTest : KoinTest {

    private val preDependencies =  applicationCoroutinesScopeModule +
            resourceManagerModule
    private val testModule = preDependencies + notificationManagerModule

    private val notificationManager: NotificationManager by inject()

    @Before
    fun setUp() {
        startKoin {
            modules(testModule)
        }
    }

    @After
    fun cleanup() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun `notificationManager should not be null`() {
        assertNotNull(notificationManager)
    }

}
