package androidstudio.tools.missed.features.dozeandstandby.di

import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.dozemode.get.DozeModeGetUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.dozemode.get.DozeModeGetUseCaseImpl
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.dozemode.set.DozeModeSetUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.dozemode.set.DozeModeSetUseCaseImpl
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.standby.get.StandbyGetUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.standby.get.StandbyGetUseCaseImpl
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.standby.set.StandbySetUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.standby.set.StandbySetUseCaseImpl
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.add.WhiteListAddUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.add.WhiteListAddUseCaseImpl
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.get.WhiteListGetUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.get.WhiteListGetUseCaseImpl
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.remove.WhiteListRemoveUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.remove.WhiteListRemoveUseCaseImpl
import androidstudio.tools.missed.features.dozeandstandby.presenter.DozeAndStandbyViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dozeAndStandbyModule = module {
    single<DozeModeGetUseCase> { DozeModeGetUseCaseImpl(get()) }
    single<DozeModeSetUseCase> { DozeModeSetUseCaseImpl(get()) }
    single<StandbyGetUseCase> { StandbyGetUseCaseImpl(get()) }
    single<StandbySetUseCase> { StandbySetUseCaseImpl(get()) }
    single<WhiteListGetUseCase> { WhiteListGetUseCaseImpl(get()) }
    single<WhiteListAddUseCase> { WhiteListAddUseCaseImpl(get()) }
    single<WhiteListRemoveUseCase> { WhiteListRemoveUseCaseImpl(get()) }
    single {
        DozeAndStandbyViewModel(
            get(named("IODispatcher")),
            get(), get(), get(), get(), get(), get(), get(), get(), get()
        )
    }
}
