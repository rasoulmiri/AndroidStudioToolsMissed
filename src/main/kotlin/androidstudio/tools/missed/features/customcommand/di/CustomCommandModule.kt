package androidstudio.tools.missed.features.customcommand.di

import androidstudio.tools.missed.features.customcommand.domain.CustomCommandRepository
import androidstudio.tools.missed.features.customcommand.domain.CustomCommandUseCase
import androidstudio.tools.missed.features.customcommand.domain.CustomCommandUseCaseImpl
import androidstudio.tools.missed.features.customcommand.presenter.CustomCommandViewModel
import androidstudio.tools.missed.features.customcommand.presenter.customcommanddialog.CustomDialogViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val customCommandModule = module {
    single<CustomCommandRepository> { CustomCommandRepository() }
    single<CustomCommandUseCase> { CustomCommandUseCaseImpl(get()) }
    factory<CustomCommandViewModel> { CustomCommandViewModel(get(named("IODispatcher")), get(), get(), get()) }
    factory<CustomDialogViewModel> { CustomDialogViewModel(get(named("IODispatcher")), get(), get(), get()) }
}
