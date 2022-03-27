package domain.datasource

import domain.model.TotalLosses

interface LossesDataSource {
    suspend fun getLosses(): TotalLosses
}
