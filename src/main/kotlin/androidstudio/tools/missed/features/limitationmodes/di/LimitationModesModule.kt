package androidstudio.tools.missed.features.limitationmodes.di

import androidstudio.tools.missed.features.limitationmodes.domain.usecase.dozemode.get.DozeModeGetUseCase
import androidstudio.tools.missed.features.limitationmodes.domain.usecase.dozemode.get.DozeModeGetUseCaseImpl
import androidstudio.tools.missed.features.limitationmodes.domain.usecase.dozemode.set.DozeModeSetUseCase
import androidstudio.tools.missed.features.limitationmodes.domain.usecase.dozemode.set.DozeModeSetUseCaseImpl
import androidstudio.tools.missed.features.limitationmodes.domain.usecase.standby.get.StandbyGetUseCase
import androidstudio.tools.missed.features.limitationmodes.domain.usecase.standby.get.StandbyGetUseCaseImpl
import androidstudio.tools.missed.features.limitationmodes.domain.usecase.standby.set.StandbySetUseCase
import androidstudio.tools.missed.features.limitationmodes.domain.usecase.standby.set.StandbySetUseCaseImpl
import androidstudio.tools.missed.features.limitationmodes.domain.usecase.whitelist.add.WhiteListAddUseCase
import androidstudio.tools.missed.features.limitationmodes.domain.usecase.whitelist.add.WhiteListAddUseCaseImpl
import androidstudio.tools.missed.features.limitationmodes.domain.usecase.whitelist.get.WhiteListGetUseCase
import androidstudio.tools.missed.features.limitationmodes.domain.usecase.whitelist.get.WhiteListGetUseCaseImpl
import androidstudio.tools.missed.features.limitationmodes.domain.usecase.whitelist.remove.WhiteListRemoveUseCase
import androidstudio.tools.missed.features.limitationmodes.domain.usecase.whitelist.remove.WhiteListRemoveUseCaseImpl
import androidstudio.tools.missed.features.limitationmodes.presenter.LimitationModesViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val limitationModesModule = module {
    single<DozeModeGetUseCase> { DozeModeGetUseCaseImpl(get()) }
    single<DozeModeSetUseCase> { DozeModeSetUseCaseImpl(get()) }
    single<StandbyGetUseCase> { StandbyGetUseCaseImpl(get()) }
    single<StandbySetUseCase> { StandbySetUseCaseImpl(get()) }
    single<WhiteListGetUseCase> { WhiteListGetUseCaseImpl(get()) }
    single<WhiteListAddUseCase> { WhiteListAddUseCaseImpl(get()) }
    single<WhiteListRemoveUseCase> { WhiteListRemoveUseCaseImpl(get()) }
    single {
        LimitationModesViewModel(
            get(named("IODispatcher")),
            get(), get(), get(), get(), get(), get(), get(), get(), get()
        )
    }
}
