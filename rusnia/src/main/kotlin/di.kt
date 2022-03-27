import data.remote.LossesRemoteDataSource
import domain.datasource.LossesDataSource
import domain.interactor.GetLossesUseCase

internal object Provider {
    val getLossesUseCase by lazy { GetLossesUseCase(lossesRemoteDataSource) }

    private val lossesRemoteDataSource: LossesDataSource by lazy {
        LossesRemoteDataSource()
    }
}
