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
import org.mockito.Mockito.times
import kotlin.test.Test
import kotlin.test.assertEquals

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

    @Test
    fun `test get application by user id`(): Unit = runBlocking {
        // GIVEN
        val id = generateUserId()
        val applications = listOf(
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
        )

        Mockito.`when`(ds.getByUserId(id)).thenReturn(applications)

        // WHEN+THEN
        assertEquals(applications, useCase.getByUserId(id))
        Mockito.verify(ds, times(1)).getByUserId(id)
    }

    @Test
    fun `test get application by application id`(): Unit = runBlocking {
        // GIVEN
        val id = generateApplicationId()
        val application = application(
            funding = Application.Funding.Budget,
            learningFormat = Application.LearningFormat.FullTime,
            speciality = Application.Speciality.SPEC_121,
            status = Application.Status.Cancelled,
        )

        Mockito.`when`(ds.getById(id)).thenReturn(application)

        // WHEN+THEN
        assertEquals(application, useCase.getById(id))
        Mockito.verify(ds, times(1)).getById(id)
    }

    @Test
    fun `test get application`(): Unit = runBlocking {
        // GIVEN
        val userId = generateUserId()
        val application = application(
            funding = Application.Funding.Budget,
            learningFormat = Application.LearningFormat.FullTime,
            speciality = Application.Speciality.SPEC_121,
            status = Application.Status.Cancelled,
        )

        Mockito.`when`(ds.get(application.id, userId)).thenReturn(application)

        // WHEN+THEN
        assertEquals(application, useCase.get(application.id, userId))
        Mockito.verify(ds, times(1)).get(application.id, userId)
    }

    @Test
    fun `test get all applications`(): Unit = runBlocking {
        // GIVEN
        val applications = listOf(
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
        )

        Mockito.`when`(ds.getAll()).thenReturn(applications)

        // WHEN+THEN
        assertEquals(applications, useCase.getAll())
        Mockito.verify(ds, times(1)).getAll()
    }
}
