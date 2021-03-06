package domain.interactor

import domain.model.LossesCategory

internal val LossesCategory.ukrainian: String get() = when (this) {
    LossesCategory.Personnel            -> "Особовий склад"
    LossesCategory.Captives             -> "Полонені"
    LossesCategory.Tanks                -> "Танки"
    LossesCategory.ArmoredCarriers      -> "ББМ"
    LossesCategory.ArtillerySystems     -> "Гармати"
    LossesCategory.RocketSystems        -> "РСЗВ"
    LossesCategory.AirDefenceSystems    -> "ППО"
    LossesCategory.Planes               -> "Літаки"
    LossesCategory.Iskanders            -> "Пускові установки ОТРК"
    LossesCategory.Helicopters          -> "Гелікоптери"
    LossesCategory.AutomotiveTechnology -> "Автомобілі"
    LossesCategory.Ships                -> "Кораблі (катери)"
    LossesCategory.Tankers              -> "Цистерни з ППМ"
    LossesCategory.UnmannedAircraft     -> "БПЛА"
    LossesCategory.SpecialEquipment     -> "Спеціальна техніка"
    LossesCategory.Rockets              -> "Крилаті ракети"
    LossesCategory.Other                -> "Інше"
}

internal val LossesCategory.english: String get() = when (this) {
    LossesCategory.Personnel            -> "Personnel"
    LossesCategory.Captives             -> "Captives"
    LossesCategory.Tanks                -> "Tanks"
    LossesCategory.ArmoredCarriers      -> "Armored carriers"
    LossesCategory.ArtillerySystems     -> "Artillery systems"
    LossesCategory.RocketSystems        -> "Rocket systems"
    LossesCategory.AirDefenceSystems    -> "Air Defence systems"
    LossesCategory.Planes               -> "Planes"
    LossesCategory.Iskanders            -> "Iskanders"
    LossesCategory.Helicopters          -> "Helicopters"
    LossesCategory.AutomotiveTechnology -> "Automotive Technology"
    LossesCategory.Ships                -> "Ships (cutters)"
    LossesCategory.Tankers              -> "Tankers"
    LossesCategory.UnmannedAircraft     -> "Unmanned Aircraft"
    LossesCategory.SpecialEquipment     -> "Special Equipment"
    LossesCategory.Rockets              -> "Cruise missiles"
    LossesCategory.Other                -> "Other"
}
