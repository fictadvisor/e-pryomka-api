package domain.interactor

import domain.datasource.LossesDataSource
import domain.model.TotalLosses
import domain.model.TotalLossesLocalized
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.*

private typealias Cache = Pair<Instant, TotalLosses>

class GetLossesUseCase(
    private val remote: LossesDataSource
) {
    private var cache: Cache? = null
    private val Cache.isValid get() = (now - first).inWholeHours >= 12
    private val now get() = Clock.System.now()

    suspend operator fun invoke(locale: Locale?): TotalLossesLocalized {
        cache?.let { if (it.isValid) return translate(locale, it.second) }
        val losses = remote.getLosses()
        cache = now to losses
        return translate(locale, losses)
    }

    private fun translate(locale: Locale?, losses: TotalLosses): TotalLossesLocalized {
        return when (locale?.language) {
            Locale("uk").language -> losses.mapValues {
                it.value.mapKeys { (category, _) -> category.ukrainian }
            }
            Locale("ru").language -> losses.mapValues {
                it.value.mapKeys { (category, _) -> category.russian }
            }
            Locale("en").language -> losses.mapValues {
                it.value.mapKeys { (category, _) -> category.english }
            }
            null -> losses.mapValues {
                it.value.mapKeys { (category, _) -> category.name }
            }
            else -> error("Locale not found: ${locale.language}")
        }
    }
}
