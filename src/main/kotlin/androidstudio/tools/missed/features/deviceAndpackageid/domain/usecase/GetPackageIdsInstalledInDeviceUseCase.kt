package androidstudio.tools.missed.features.deviceAndpackageid.domain.usecase

import kotlinx.coroutines.flow.Flow

interface GetPackageIdsInstalledInDeviceUseCase {

    suspend fun invoke(isSelectedShowAllPackageIds: Boolean): Flow<Result<ArrayList<String>>>
}
