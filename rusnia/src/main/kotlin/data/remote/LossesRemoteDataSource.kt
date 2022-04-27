package data.remote

import domain.datasource.LossesDataSource
import domain.model.DailyLosses
import domain.model.LossesCategory
import domain.model.TotalLosses
import it.skrape.core.document
import it.skrape.fetcher.AsyncFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.div
import it.skrape.selects.html5.li
import it.skrape.selects.html5.span
import it.skrape.selects.html5.ul
import kotlinx.datetime.LocalDate
import java.util.*

class LossesRemoteDataSource : LossesDataSource {
    private val dateRegex = Regex("\\d{1,2}\\.\\d{1,2}\\.\\d{4}")
    private val baseUrl = "https://index.minfin.com.ua/ua/russian-invading/casualties/month.php"
    private val warStarted = Calendar.getInstance().apply {
        set(
            /* year = */ 2022,
            /* month = */ 1, // starts with 0
            /* date = */ 24,
        )
    }

    override suspend fun getLosses(): TotalLosses = parseLosses().mapValues { (_, dailyLosses) ->
        parseDailyLosses(dailyLosses)
    }

    private suspend fun parseLosses(): Map<LocalDate, List<String>> {
        println("Parsing losses from minfin.com.ua...")
        val currentMonth = Calendar.getInstance()[Calendar.MONTH] + 1
        val warStartedMonth = warStarted[Calendar.MONTH] + 1

        val totalLosses = mutableMapOf<LocalDate, List<String>>()
        for (month in warStartedMonth..currentMonth) {
            totalLosses += parseMonthlyLosses(month)
        }

        return totalLosses.toSortedMap { date1, date2 -> date2.compareTo(date1) }
    }

    private suspend fun parseMonthlyLosses(month: Int): Map<LocalDate, List<String>> {
        println("Parsing losses for ${formatMonth(month)}")
        val losses = skrape(AsyncFetcher) {
            request { url = urlForMonth(month) }

            response {
                document.li {
                    withClass = "gold"
                    findAll { this }
                }.filter { doc ->
                    val date = doc.span {
                        withClass = "black"
                        findFirst { text }
                    }

                    date.matches(dateRegex)
                }.associate { doc ->
                    val date = doc.span {
                        withClass = "black"
                        findFirst { text }
                    }.let {
                        val (dd, mm, yyyy) = it.split('.').map(String::toInt)
                        LocalDate(yyyy, mm, dd)
                    }

                    val casualties = doc.div {
                        withClass = "casualties"
                        div {
                            ul {
                                li {
                                    findAll { map { it.text } }
                                }
                            }
                        }
                    }

                    date to casualties
                }
            }
        }

        return losses
    }

    private fun urlForMonth(month: Int) = "$baseUrl?month=${formatMonth(month)}"
    private fun formatMonth(month: Int) = "%d-%02d".format(warStarted[Calendar.YEAR], month)

    private fun parseDailyLosses(dailyLosses: List<String>): DailyLosses {
        val parsed = mutableMapOf<LossesCategory, Long>()

        dailyLosses.forEach { losses ->
            val category = parseCategory(
                losses.substringBefore("—")
                    .trim()
                    .lowercase(Locale.getDefault())
            )
            val amountString = losses.substringAfter("—").trim()

            when (category) {
                LossesCategory.Personnel -> {
                    val (personnel, captives) = parseAmountSoldiers(amountString)

                    personnel?.also { parsed[LossesCategory.Personnel] = it }
                    captives?.also { parsed[LossesCategory.Captives] = it }
                }
                else -> parseAmount(amountString)?.let { parsed[category] = it }
            }
        }

        return parsed
    }

    private fun parseCategory(string: String) = LossesCategory.values()
        .find { it.matches(string) }
        ?: LossesCategory.Other

    private fun LossesCategory.matches(string: String) = when (this) {
        LossesCategory.Personnel -> string == "особовий склад"
        LossesCategory.Tanks -> string == "танки"
        LossesCategory.ArmoredCarriers -> string == "ббм"
        LossesCategory.ArtillerySystems -> string == "гармати"
        LossesCategory.RocketSystems -> string in listOf("рсзв", "рсзв град")
        LossesCategory.AirDefenceSystems -> string in listOf("засоби ппо", "зрк бук")
        LossesCategory.Planes -> string == "літаки"
        LossesCategory.Iskanders -> string == "пускові установки отрк"
        LossesCategory.Helicopters -> string == "гелікоптери"
        LossesCategory.AutomotiveTechnology -> string == "автомобілі"
        LossesCategory.Ships -> string == "кораблі (катери)"
        LossesCategory.Tankers -> string == "цистерни з ппм"
        LossesCategory.UnmannedAircraft -> string == "бпла"
        LossesCategory.SpecialEquipment -> string == "спеціальна техніка"
        LossesCategory.Captives, LossesCategory.Other -> false
    }

    private fun parseAmount(string: String): Long? {
        val regex = Regex("\\d+")
        return regex.find(string)?.value?.toLongOrNull()
    }

    /** @return [Pair] where [Pair.first] is amount of dead soldiers
     * and [Pair.second] is amount of captives */
    private fun parseAmountSoldiers(string: String): Pair<Long?, Long?> {
        val regex = Regex("\\d+")
        val numbers = regex.findAll(string)
            .map { it.value.toLongOrNull() }
            .toList()

        val personnel = numbers.getOrNull(0)
        val captives = numbers.getOrNull(1)

        return personnel to captives
    }
}
