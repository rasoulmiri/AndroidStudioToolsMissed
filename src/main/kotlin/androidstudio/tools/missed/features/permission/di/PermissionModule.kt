package androidstudio.tools.missed.features.permission.di

import androidstudio.tools.missed.features.permission.domain.usecase.fetchall.FetchAllPermissionsUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.fetchall.FetchAllPermissionsUseCaseImpl
import androidstudio.tools.missed.features.permission.domain.usecase.grant.GrantPermissionUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.grant.GrantPermissionUseCaseImpl
import androidstudio.tools.missed.features.permission.domain.usecase.grantall.GrantAllPermissionUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.grantall.GrantAllPermissionUseCaseImpl
import androidstudio.tools.missed.features.permission.domain.usecase.restartApp.RestartAppUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.restartApp.RestartAppUseCaseImpl
import androidstudio.tools.missed.features.permission.domain.usecase.revoke.RevokePermissionUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.revoke.RevokePermissionUseCaseImpl
import androidstudio.tools.missed.features.permission.domain.usecase.revokeall.RevokeAllPermissionUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.revokeall.RevokeAllPermissionUseCaseImpl
import androidstudio.tools.missed.features.permission.presenter.PermissionViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val permissionModule = module {
    single<RestartAppUseCase> { RestartAppUseCaseImpl(get()) }
    single<FetchAllPermissionsUseCase> { FetchAllPermissionsUseCaseImpl(get(), get()) }
    single<GrantPermissionUseCase> { GrantPermissionUseCaseImpl(get(), get()) }
    single<RevokePermissionUseCase> { RevokePermissionUseCaseImpl(get(), get()) }
    single<GrantAllPermissionUseCase> { GrantAllPermissionUseCaseImpl(get(), get(), get(), get()) }
    single<RevokeAllPermissionUseCase> { RevokeAllPermissionUseCaseImpl(get(), get(), get(), get()) }
    single { PermissionViewModel(get(named("IODispatcher")), get(), get(), get(), get(), get(), get(), get(), get()) }
}
