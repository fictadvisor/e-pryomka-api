package domain.interactor

import domain.datasource.LossesDataSource
import domain.model.TotalLosses
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

private typealias Cache = Pair<Instant, TotalLosses>

class GetLossesUseCase(
    private val remote: LossesDataSource
) {
    private var cache: Cache? = null
    private val Cache.isValid get() = (now - first).inWholeHours >= 12
    private val now get() = Clock.System.now()

    suspend operator fun invoke(): TotalLosses {
        cache?.let { if (it.isValid) return it.second }
        val losses = remote.getLosses()
        cache = now to losses
        return losses
    }
}
