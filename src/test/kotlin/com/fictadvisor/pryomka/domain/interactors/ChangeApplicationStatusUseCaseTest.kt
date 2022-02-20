package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.admin
import com.fictadvisor.pryomka.application
import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.datasource.ReviewsDataSource
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.*
import com.fictadvisor.pryomka.entrant
import com.fictadvisor.pryomka.operator
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.times
import kotlin.test.Test

class ChangeApplicationStatusUseCaseTest {
    private val userDataSource = Mockito.mock(UserDataSource::class.java)
    private val applicationDataSource = Mockito.mock(ApplicationDataSource::class.java)
    private val reviewsDataSource = Mockito.mock(ReviewsDataSource::class.java)

    private val useCase = ChangeApplicationStatusUseCaseImpl(userDataSource, applicationDataSource, reviewsDataSource)

    @Test
    fun `should throw unauthorized exception if user not found`(): Unit = runBlocking {
        // GIVEN
        val randomUserId = generateUserId()
        Mockito.`when`(userDataSource.findStaff(randomUserId)).thenReturn(null)
        Mockito.`when`(userDataSource.findEntrant(randomUserId)).thenReturn(null)

        // WHEN+THEN
        assertThrows<Unauthorized> {
            useCase.changeStatus(
                generateApplicationId(),
                randomUserId,
                Application.Status.InReview,
                null
            )
        }
    }

    @Test
    fun `should throw not found exception if application not found`(): Unit = runBlocking {
        // GIVEN
        val user = entrant()
        val applicationId = generateApplicationId()
        Mockito.`when`(userDataSource.findEntrant(user.id)).thenReturn(user)
        Mockito.`when`(applicationDataSource.get(applicationId, user.id)).thenReturn(null)

        // WHEN+THEN
        assertThrows<NotFound> {
            useCase.changeStatus(applicationId, user.id, Application.Status.InReview, null)
        }
    }

    @Test
    fun `should throw permission denied exception if application status same as new status`(): Unit = runBlocking {
        // GIVEN
        val user = entrant()
        val application = application(status = Application.Status.Rejected)
        Mockito.`when`(userDataSource.findEntrant(user.id)).thenReturn(user)
        Mockito.`when`(applicationDataSource.get(application.id, user.id)).thenReturn(application)

        // WHEN+THEN
        assertThrows<PermissionDenied> {
            useCase.changeStatus(application.id, user.id, Application.Status.Rejected, null)
        }
    }

    @Test
    fun `should allow entrant cancel application in status Preparing`(): Unit = runBlocking {
        // GIVEN
        val entrant = entrant()
        Mockito.`when`(userDataSource.findEntrant(entrant.id)).thenReturn(entrant)

        val application = application(status = Application.Status.Preparing)
        Mockito.`when`(applicationDataSource.get(application.id, entrant.id)).thenReturn(application)

        // WHEN
        useCase.changeStatus(application.id, entrant.id, Application.Status.Cancelled, null)

        // THEN
        Mockito.verify(applicationDataSource, times(1))
            .changeStatus(application.id, Application.Status.Cancelled, null)
    }

    @Test
    fun `should allow entrant submit application in status Preparing`(): Unit = runBlocking {
        // GIVEN
        val entrant = entrant()
        Mockito.`when`(userDataSource.findEntrant(entrant.id)).thenReturn(entrant)

        val application = application(status = Application.Status.Preparing)
        Mockito.`when`(applicationDataSource.get(application.id, entrant.id)).thenReturn(application)

        // WHEN
        useCase.changeStatus(application.id, entrant.id, Application.Status.Pending, null)

        // THEN
        Mockito.verify(applicationDataSource, times(1))
            .changeStatus(application.id, Application.Status.Pending, null)
    }

    @Test
    fun `should forbid entrant to set wrong status for Preparing application`(): Unit = runBlocking {
        // GIVEN
        val entrant = entrant()
        Mockito.`when`(userDataSource.findEntrant(entrant.id)).thenReturn(entrant)

        val application = application(status = Application.Status.Preparing)
        Mockito.`when`(applicationDataSource.get(application.id, entrant.id)).thenReturn(application)

        // WHEN+THEN
        assertThrows<PermissionDenied> {
            useCase.changeStatus(application.id, entrant.id, Application.Status.Rejected, null)
        }
    }

    @Test
    fun `should allow entrant cancel application in status Pending`(): Unit = runBlocking {
        // GIVEN
        val entrant = entrant()
        Mockito.`when`(userDataSource.findEntrant(entrant.id)).thenReturn(entrant)

        val application = application(status = Application.Status.Pending)
        Mockito.`when`(applicationDataSource.get(application.id, entrant.id)).thenReturn(application)

        // WHEN
        useCase.changeStatus(application.id, entrant.id, Application.Status.Cancelled, null)

        // THEN
        Mockito.verify(applicationDataSource, times(1))
            .changeStatus(application.id, Application.Status.Cancelled, null)
    }

    @Test
    fun `should forbid user to set wrong status for Pending application`(): Unit = runBlocking {
        // GIVEN
        val entrant = entrant()
        val application = application(status = Application.Status.Pending)
        Mockito.`when`(userDataSource.findEntrant(entrant.id)).thenReturn(entrant)
        Mockito.`when`(applicationDataSource.get(application.id, entrant.id)).thenReturn(application)

        // WHEN+THEN
        assertThrows<PermissionDenied> {
            useCase.changeStatus(application.id, entrant.id, Application.Status.Rejected, null)
        }
    }

    @Test
    fun `should forbid user to change status of application that is not Pending or Preparing`(): Unit = runBlocking {
        // GIVEN
        val entrant = entrant()
        Mockito.`when`(userDataSource.findEntrant(entrant.id)).thenReturn(entrant)

        val application = application(status = Application.Status.Cancelled)
        Mockito.`when`(applicationDataSource.get(application.id, entrant.id)).thenReturn(application)

        // WHEN+THEN
        assertThrows<PermissionDenied> {
            useCase.changeStatus(application.id, entrant.id, Application.Status.Rejected, null)
        }
    }

    @Test
    fun `should allow operator take application in review`(): Unit = runBlocking {
        // GIVEN
        val operator = operator()
        val application = application(status = Application.Status.Pending)

        Mockito.`when`(userDataSource.findEntrant(operator.id)).thenReturn(null)
        Mockito.`when`(userDataSource.findStaff(operator.id)).thenReturn(operator)
        Mockito.`when`(applicationDataSource.getById(application.id)).thenReturn(application)
        Mockito.`when`(reviewsDataSource.checkInReview(application.id)).thenReturn(false)

        // WHEN
        useCase.changeStatus(application.id, operator.id, Application.Status.InReview, null)

        // THEN
        Mockito.verify(reviewsDataSource, times(1)).checkInReview(application.id)
        Mockito.verify(reviewsDataSource, times(1)).addToReview(application.id, operator.id)
        Mockito.verify(applicationDataSource, times(1))
            .changeStatus(application.id, Application.Status.InReview, null)
    }

    @Test
    fun `should not take application in review if it is already in review`(): Unit = runBlocking {
        /* Protection from data race, when a few operators are trying to
        take same application in review */

        // GIVEN
        val operator = operator()
        val application = application(status = Application.Status.Pending)

        Mockito.`when`(userDataSource.findEntrant(operator.id)).thenReturn(null)
        Mockito.`when`(userDataSource.findStaff(operator.id)).thenReturn(operator)
        Mockito.`when`(applicationDataSource.getById(application.id)).thenReturn(application)
        Mockito.`when`(reviewsDataSource.checkInReview(application.id)).thenReturn(true)

        // WHEN+THEN
        assertThrows<PermissionDenied> {
            useCase.changeStatus(application.id, operator.id, Application.Status.InReview, null)
        }
    }

    @Test
    fun `operator should not change application status if it is not in review`(): Unit = runBlocking {
        // GIVEN
        val operator = operator()
        val application = application(status = Application.Status.Pending)

        Mockito.`when`(userDataSource.findEntrant(operator.id)).thenReturn(null)
        Mockito.`when`(userDataSource.findStaff(operator.id)).thenReturn(operator)
        Mockito.`when`(applicationDataSource.getById(application.id)).thenReturn(application)
        Mockito.`when`(reviewsDataSource.checkInReview(application.id)).thenReturn(false)

        // WHEN+THEN
        assertThrows<PermissionDenied> {
            useCase.changeStatus(application.id, operator.id, Application.Status.Rejected, null)
        }
    }

    @Test
    fun `approve application`(): Unit = runBlocking {
        // GIVEN
        val operator = operator()
        val application = application(status = Application.Status.InReview)

        Mockito.`when`(userDataSource.findEntrant(operator.id)).thenReturn(null)
        Mockito.`when`(userDataSource.findStaff(operator.id)).thenReturn(operator)
        Mockito.`when`(applicationDataSource.getById(application.id)).thenReturn(application)
        Mockito.`when`(reviewsDataSource.getReviewerId(application.id)).thenReturn(operator.id)

        // WHEN
        useCase.changeStatus(application.id, operator.id, Application.Status.Approved, null)

        // THEN
        Mockito.verify(reviewsDataSource, times(1)).getReviewerId(application.id)
        Mockito.verify(reviewsDataSource, times(1)).removeFromReview(application.id)
        Mockito.verify(applicationDataSource, times(1))
            .changeStatus(application.id, Application.Status.Approved, null)
    }

    @Test
    fun `reject application`(): Unit = runBlocking {
        // GIVEN
        val operator = operator()
        val application = application(status = Application.Status.InReview)

        Mockito.`when`(userDataSource.findEntrant(operator.id)).thenReturn(null)
        Mockito.`when`(userDataSource.findStaff(operator.id)).thenReturn(operator)
        Mockito.`when`(applicationDataSource.getById(application.id)).thenReturn(application)
        Mockito.`when`(reviewsDataSource.getReviewerId(application.id)).thenReturn(operator.id)

        // WHEN
        useCase.changeStatus(application.id, operator.id, Application.Status.Rejected, null)

        // THEN
        Mockito.verify(reviewsDataSource, times(1)).getReviewerId(application.id)
        Mockito.verify(reviewsDataSource, times(1)).removeFromReview(application.id)
        Mockito.verify(applicationDataSource, times(1)).changeStatus(application.id, Application.Status.Rejected, null)
    }

    @Test
    fun `forbid operator from changing applications that are in review by another operator`(): Unit = runBlocking {
        // GIVEN
        val operator = operator()
        val application = application(status = Application.Status.InReview)

        Mockito.`when`(userDataSource.findEntrant(operator.id)).thenReturn(null)
        Mockito.`when`(userDataSource.findStaff(operator.id)).thenReturn(operator)
        Mockito.`when`(applicationDataSource.getById(application.id)).thenReturn(application)
        Mockito.`when`(reviewsDataSource.getReviewerId(application.id)).thenReturn(generateUserId())

        // WHEN+THEN
        assertThrows<PermissionDenied> {
            useCase.changeStatus(application.id, operator.id, Application.Status.Rejected, null)
        }
    }

    @Test
    fun `operator can't return application to pending status`(): Unit = runBlocking {
        // GIVEN
        val operator = operator()
        val application = application(status = Application.Status.InReview)

        Mockito.`when`(userDataSource.findEntrant(operator.id)).thenReturn(null)
        Mockito.`when`(userDataSource.findStaff(operator.id)).thenReturn(operator)
        Mockito.`when`(applicationDataSource.getById(application.id)).thenReturn(application)
        Mockito.`when`(reviewsDataSource.getReviewerId(application.id)).thenReturn(operator.id)

        // WHEN+THEN
        assertThrows<PermissionDenied> {
            useCase.changeStatus(application.id, operator.id, Application.Status.Pending, null)
        }
    }

    @Test
    fun `operator can't change status of resolved application`(): Unit = runBlocking {
        // GIVEN
        val operator = operator()
        val application = application(status = Application.Status.Rejected)

        Mockito.`when`(userDataSource.findEntrant(operator.id)).thenReturn(null)
        Mockito.`when`(userDataSource.findStaff(operator.id)).thenReturn(operator)
        Mockito.`when`(applicationDataSource.getById(application.id)).thenReturn(application)

        // WHEN+THEN
        assertThrows<PermissionDenied> {
            useCase.changeStatus(application.id, operator.id, Application.Status.Pending, null)
        }
    }

    @Test
    fun `admin can review applications`(): Unit = runBlocking {
        // GIVEN
        val admin = admin()
        val application = application(status = Application.Status.Pending)

        Mockito.`when`(userDataSource.findEntrant(admin.id)).thenReturn(null)
        Mockito.`when`(userDataSource.findStaff(admin.id)).thenReturn(admin)
        Mockito.`when`(applicationDataSource.getById(application.id)).thenReturn(application)

        // WHEN
        useCase.changeStatus(application.id, admin.id, Application.Status.InReview, null)

        // THEN
        Mockito.verify(reviewsDataSource, times(1)).addToReview(application.id, admin.id)
        Mockito.verify(applicationDataSource, times(1))
            .changeStatus(application.id, Application.Status.InReview, null)
    }

    @Test
    fun `admin can return application from InReview to Pending`(): Unit = runBlocking {
        // GIVEN
        val admin = admin()
        val application = application(status = Application.Status.InReview)

        Mockito.`when`(userDataSource.findEntrant(admin.id)).thenReturn(null)
        Mockito.`when`(userDataSource.findStaff(admin.id)).thenReturn(admin)
        Mockito.`when`(applicationDataSource.getById(application.id)).thenReturn(application)

        // when
        useCase.changeStatus(application.id, admin.id, Application.Status.Pending, null)

        // then
        Mockito.verify(reviewsDataSource, times(1)).removeFromReview(application.id)
        Mockito.verify(applicationDataSource, times(1))
            .changeStatus(application.id, Application.Status.Pending, null)
    }
}
