import data.remote.LossesRemoteDataSource
import domain.datasource.LossesDataSource
import domain.interactor.GetLossesUseCase

internal object Provider {
    val getLossesUseCase by lazy { GetLossesUseCase(lossesRemoteDataSource) }
    val password = System.getenv()["RUSNIA_PASSWORD"]

    private val lossesRemoteDataSource: LossesDataSource by lazy {
        LossesRemoteDataSource()
    }
}
