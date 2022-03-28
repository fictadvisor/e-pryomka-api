package domain.model

import kotlinx.datetime.LocalDate

enum class LossesCategory {
    Personnel,                          // особовий склад
    Captives,                           // полонені
    Tanks,                              // танки
    ArmoredCarriers,                    // ББМ
    ArtillerySystems,                   // гармати
    RocketSystems,                      // РСЗО
    AirDefenceSystems,                  // ППО
    Planes,                             // літаки
    Iskanders,                          // пускові установки ОТРК
    Helicopters,                        // гелікоптери
    AutomotiveTechnology,               // автомобілі
    Ships,                              // кораблі (катери)
    Tankers,                            // цистерни з ППМ
    UnmannedAircraft,                   // БПЛА
    SpecialEquipment,                   // спецтехніка
    Other,
}

typealias DailyLosses = Map<LossesCategory, Long>
typealias TotalLosses = Map<LocalDate, DailyLosses>

typealias DailyLossesLocalized = Map<String, Long>
typealias TotalLossesLocalized = Map<LocalDate, DailyLossesLocalized>
