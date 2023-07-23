package androidstudio.tools.missed.features.input.di

import androidstudio.tools.missed.features.input.domain.usecase.cleartext.ClearTextUseCase
import androidstudio.tools.missed.features.input.domain.usecase.cleartext.ClearTextUseCaseImpl
import androidstudio.tools.missed.features.input.domain.usecase.sendevent.SendEventUseCase
import androidstudio.tools.missed.features.input.domain.usecase.sendevent.SendEventUseCaseImpl
import androidstudio.tools.missed.features.input.domain.usecase.sendtext.SendTextUseCase
import androidstudio.tools.missed.features.input.domain.usecase.sendtext.SendTextUseCaseImpl
import androidstudio.tools.missed.features.input.presenter.InputTextViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val inputModule = module {
    single<SendTextUseCase> { SendTextUseCaseImpl(get()) }
    single<SendEventUseCase> { SendEventUseCaseImpl(get()) }
    single<ClearTextUseCase> { ClearTextUseCaseImpl(get()) }
    single<InputTextViewModel> { InputTextViewModel(get(named("IODispatcher")), get(), get(), get(), get()) }
}
