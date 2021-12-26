package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.application
import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.Duplicated
import com.fictadvisor.pryomka.domain.models.generateApplicationId
import com.fictadvisor.pryomka.domain.models.generateUserId
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import kotlin.test.Test

class ApplicationUseCaseImplTest {
    private val ds = Mockito.mock(ApplicationDataSource::class.java)
    private val useCase = ApplicationUseCaseImpl(ds)

    @Test
    fun `duplicate exc if another non neg terminated exists with the same params`(): Unit = runBlocking {
        // given
        val randomUserId = generateUserId()
        val newApplication = application(
            funding = Application.Funding.Budget,
            learningFormat = Application.LearningFormat.FullTime,
            speciality = Application.Speciality.SPEC_121,
        )
        Mockito.`when`(ds.getByUserId(randomUserId)).thenReturn(listOf(
            application(
                funding = Application.Funding.Budget,
                learningFormat = Application.LearningFormat.FullTime,
                speciality = Application.Speciality.SPEC_121,
                status = Application.Status.Approved,
            ),
        ))

        // when + then
        assertThrows<Duplicated> {
            useCase.create(newApplication, randomUserId)
        }
    }

    @Test
    fun `create application if there are no others with the same params`(): Unit = runBlocking {
        // given
        val randomUserId = generateUserId()
        val newApplication = application(
            funding = Application.Funding.Budget,
            learningFormat = Application.LearningFormat.FullTime,
            speciality = Application.Speciality.SPEC_121,
        )
        Mockito.`when`(ds.getByUserId(randomUserId)).thenReturn(listOf(
            application(
                funding = Application.Funding.Budget,
                learningFormat = Application.LearningFormat.FullTime,
                speciality = Application.Speciality.SPEC_123,
                status = Application.Status.Approved,
            ),
            application(
                funding = Application.Funding.Contract,
                learningFormat = Application.LearningFormat.FullTime,
                speciality = Application.Speciality.SPEC_121,
                status = Application.Status.Approved,
            ),
            application(
                funding = Application.Funding.Budget,
                learningFormat = Application.LearningFormat.PartTime,
                speciality = Application.Speciality.SPEC_123,
                status = Application.Status.Approved,
            ),
            application(
                funding = Application.Funding.Contract,
                learningFormat = Application.LearningFormat.PartTime,
                speciality = Application.Speciality.SPEC_126,
                status = Application.Status.Approved,
            ),
        ))

        // when
        useCase.create(newApplication, randomUserId)

        // then
        Mockito.verify(ds, Mockito.times(1))
            .create(newApplication)
    }

    @Test
    fun `create application if there are others with the same params but in neg term state`(): Unit = runBlocking {
        // given
        val randomUserId = generateUserId()
        val newApplication = application(
            funding = Application.Funding.Budget,
            learningFormat = Application.LearningFormat.FullTime,
            speciality = Application.Speciality.SPEC_121,
        )
        Mockito.`when`(ds.getByUserId(randomUserId)).thenReturn(listOf(
            application(
                funding = Application.Funding.Budget,
                learningFormat = Application.LearningFormat.FullTime,
                speciality = Application.Speciality.SPEC_121,
                status = Application.Status.Cancelled,
            ),
            application(
                funding = Application.Funding.Budget,
                learningFormat = Application.LearningFormat.FullTime,
                speciality = Application.Speciality.SPEC_121,
                status = Application.Status.Rejected,
            ),
        ))

        // when
        useCase.create(newApplication, randomUserId)

        // then
        Mockito.verify(ds, Mockito.times(1))
            .create(newApplication)
    }
}
