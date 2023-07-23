package androidstudio.tools.missed.features.permission.di

import androidstudio.tools.missed.features.permission.domain.usecase.fetchall.FetchAllPermissionsUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.grant.GrantPermissionUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.grantall.GrantAllPermissionUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.restartApp.RestartAppUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.revoke.RevokePermissionUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.revokeall.RevokeAllPermissionUseCase
import androidstudio.tools.missed.features.permission.presenter.PermissionViewModel
import androidstudio.tools.missed.manager.adb.di.adbManagerModule
import androidstudio.tools.missed.manager.device.di.deviceManagerModule
import androidstudio.tools.missed.manager.resource.di.resourceManagerModule
import androidstudio.tools.missed.utils.coroutines.dispatcher.coroutinesDispatcherIOModule
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.test.KoinTest
import org.koin.test.inject

class PermissionModuleTest : KoinTest {

    private val preDependencies =
        coroutinesDispatcherIOModule +
                resourceManagerModule +
                adbManagerModule +
                deviceManagerModule
    private val testModule = preDependencies + permissionModule

    private val restartAppUseCase: RestartAppUseCase by inject()
    private val fetchAllPermissionsUseCase: FetchAllPermissionsUseCase by inject()
    private val grantPermissionUseCase: GrantPermissionUseCase by inject()
    private val revokePermissionUseCase: RevokePermissionUseCase by inject()
    private val grantAllPermissionUseCase: GrantAllPermissionUseCase by inject()
    private val revokeAllPermissionUseCase: RevokeAllPermissionUseCase by inject()
    private val permissionViewModel: PermissionViewModel by inject()

    @Before
    fun setUp() {
        startKoin {
            modules(testModule)
        }
    }

    @After
    fun cleanup() {
        unmockkAll()
        GlobalContext.stopKoin()
    }


    @Test
    fun `restartAppUseCase should not be null`() {
        assertNotNull(restartAppUseCase)
    }

    @Test
    fun `fetchAllPermissionsUseCase should not be null`() {
        assertNotNull(fetchAllPermissionsUseCase)
    }

    @Test
    fun `grantPermissionUseCase should not be null`() {
        assertNotNull(grantPermissionUseCase)
    }

    @Test
    fun `revokePermissionUseCase should not be null`() {
        assertNotNull(revokePermissionUseCase)
    }

    @Test
    fun `grantAllPermissionUseCase should not be null`() {
        assertNotNull(grantAllPermissionUseCase)
    }

    @Test
    fun `revokeAllPermissionUseCase should not be null`() {
        assertNotNull(revokeAllPermissionUseCase)
    }

    @Test
    fun `permissionViewModel should not be null`() {
        assertNotNull(permissionViewModel)
    }
}
