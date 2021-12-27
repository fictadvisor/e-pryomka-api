package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.application
import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.datasource.ReviewsDataSource
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.*
import com.fictadvisor.pryomka.user
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import kotlin.test.Test

class ChangeApplicationStatusUseCaseTest {
    private val userDataSource = Mockito.mock(UserDataSource::class.java)
    private val applicationDataSource = Mockito.mock(ApplicationDataSource::class.java)
    private val reviewsDataSource = Mockito.mock(ReviewsDataSource::class.java)

    private val useCase = ChangeApplicationStatusUseCaseImpl(userDataSource, applicationDataSource, reviewsDataSource)

    @Test
    fun `should throw unauthorized exc if provided user is not found`(): Unit = runBlocking {
        // given
        val randomUserId = generateUserId()
        Mockito.`when`(userDataSource.findUser(randomUserId)).thenReturn(null)

        // when + then
        assertThrows<Unauthorized> {
            useCase.changeStatus(generateApplicationId(), randomUserId, Application.Status.InReview, null)
        }
    }

    @Test
    fun `should throw notfound exc if provided application id is not found`(): Unit = runBlocking {
        // given
        val randomUser = user()
        Mockito.`when`(userDataSource.findUser(randomUser.id)).thenReturn(randomUser)

        val randomApplicationId = generateApplicationId()
        Mockito.`when`(applicationDataSource.get(randomApplicationId, randomUser.id)).thenReturn(null)

        // when + then
        assertThrows<NotFound> {
            useCase.changeStatus(randomApplicationId, randomUser.id, Application.Status.InReview, null)
        }
    }

    @Test
    fun `should throw permission denied exception if application status same as new status`(): Unit = runBlocking {
        // given
        val randomUser = user()
        Mockito.`when`(userDataSource.findUser(randomUser.id)).thenReturn(randomUser)

        val randomApplication = application(status = Application.Status.Rejected)
        Mockito.`when`(applicationDataSource.get(randomApplication.id, randomUser.id)).thenReturn(randomApplication)

        // when + then
        assertThrows<PermissionDenied> {
            useCase.changeStatus(randomApplication.id, randomUser.id, Application.Status.Rejected, null)
        }
    }

    @Test
    fun `should change role for Entrant user with Cancelled new status and app status Preparing`(): Unit = runBlocking {
        // given
        val randomUser = user(role = User.Role.Entrant)
        Mockito.`when`(userDataSource.findUser(randomUser.id)).thenReturn(randomUser)

        val randomApplication = application(status = Application.Status.Preparing)
        Mockito.`when`(applicationDataSource.get(randomApplication.id, randomUser.id)).thenReturn(randomApplication)

        // when
        useCase.changeStatus(randomApplication.id, randomUser.id, Application.Status.Cancelled, null)

        // then
        Mockito.verify(applicationDataSource, Mockito.times(1))
            .changeStatus(randomApplication.id, Application.Status.Cancelled, null)
    }

    @Test
    fun `should change role for Entrant user with Pending new status and app status Preparing`(): Unit = runBlocking {
        // given
        val randomUser = user(role = User.Role.Entrant)
        Mockito.`when`(userDataSource.findUser(randomUser.id)).thenReturn(randomUser)

        val randomApplication = application(status = Application.Status.Preparing)
        Mockito.`when`(applicationDataSource.get(randomApplication.id, randomUser.id)).thenReturn(randomApplication)

        // when
        useCase.changeStatus(randomApplication.id, randomUser.id, Application.Status.Pending, null)

        // then
        Mockito.verify(applicationDataSource, Mockito.times(1))
            .changeStatus(randomApplication.id, Application.Status.Pending, null)
    }

    @Test
    fun `should throw perm exc for Entrant user with non Cancelled or Pending new status and app status Preparing`(

    ): Unit = runBlocking {
        // given
        val randomUser = user(role = User.Role.Entrant)
        Mockito.`when`(userDataSource.findUser(randomUser.id)).thenReturn(randomUser)

        val randomApplication = application(status = Application.Status.Preparing)
        Mockito.`when`(applicationDataSource.get(randomApplication.id, randomUser.id)).thenReturn(randomApplication)

        // when
        assertThrows<PermissionDenied> {
            useCase.changeStatus(randomApplication.id, randomUser.id, Application.Status.Rejected, null)
        }
    }

    @Test
    fun `should change role for Entrant user with Cancelled new status and app status Pending`(): Unit = runBlocking {
        // given
        val randomUser = user(role = User.Role.Entrant)
        Mockito.`when`(userDataSource.findUser(randomUser.id)).thenReturn(randomUser)

        val randomApplication = application(status = Application.Status.Pending)
        Mockito.`when`(applicationDataSource.get(randomApplication.id, randomUser.id)).thenReturn(randomApplication)

        // when
        useCase.changeStatus(randomApplication.id, randomUser.id, Application.Status.Cancelled, null)

        // then
        Mockito.verify(applicationDataSource, Mockito.times(1))
            .changeStatus(randomApplication.id, Application.Status.Cancelled, null)
    }

    @Test
    fun `should throw permission exc for Entrant user with non Cancelled new status and app status Pending`(

    ): Unit = runBlocking {
        // given
        val randomUser = user(role = User.Role.Entrant)
        Mockito.`when`(userDataSource.findUser(randomUser.id)).thenReturn(randomUser)

        val randomApplication = application(status = Application.Status.Pending)
        Mockito.`when`(applicationDataSource.get(randomApplication.id, randomUser.id)).thenReturn(randomApplication)

        // when
        assertThrows<PermissionDenied> {
            useCase.changeStatus(randomApplication.id, randomUser.id, Application.Status.Rejected, null)
        }
    }

    @Test
    fun `should throw permission exc for Entrant with non Preparing or Pending app status`(): Unit = runBlocking {
        // given
        val randomUser = user(role = User.Role.Entrant)
        Mockito.`when`(userDataSource.findUser(randomUser.id)).thenReturn(randomUser)

        val randomApplication = application(status = Application.Status.Cancelled)
        Mockito.`when`(applicationDataSource.get(randomApplication.id, randomUser.id)).thenReturn(randomApplication)

        // when + then
        assertThrows<PermissionDenied> {
            useCase.changeStatus(randomApplication.id, randomUser.id, Application.Status.Rejected, null)
        }
    }

    @Test
    fun `should change status for Operator with new InReview and app Pending status if it isn't in review already`(

    ): Unit = runBlocking {
        // given
        val randomUser = user(role = User.Role.Operator)
        Mockito.`when`(userDataSource.findUser(randomUser.id)).thenReturn(randomUser)

        val randomApplication = application(status = Application.Status.Pending)
        Mockito.`when`(applicationDataSource.getById(randomApplication.id)).thenReturn(randomApplication)

        Mockito.`when`(reviewsDataSource.checkInReview(randomApplication.id)).thenReturn(false)

        // when
        useCase.changeStatus(randomApplication.id, randomUser.id, Application.Status.InReview, null)

        // then
        Mockito.verify(reviewsDataSource, Mockito.times(1))
            .checkInReview(randomApplication.id)
        Mockito.verify(reviewsDataSource, Mockito.times(1))
            .addToReview(randomApplication.id, randomUser.id)
        Mockito.verify(applicationDataSource, Mockito.times(1))
            .changeStatus(randomApplication.id, Application.Status.InReview, null)
    }

    @Test
    fun `should throw perm exc for Operator with new InReview and app Pending status if it is in review already`(

    ): Unit = runBlocking {
        // given
        val randomUser = user(role = User.Role.Operator)
        Mockito.`when`(userDataSource.findUser(randomUser.id)).thenReturn(randomUser)

        val randomApplication = application(status = Application.Status.Pending)
        Mockito.`when`(applicationDataSource.getById(randomApplication.id)).thenReturn(randomApplication)

        Mockito.`when`(reviewsDataSource.checkInReview(randomApplication.id)).thenReturn(true)

        // when + then
        assertThrows<PermissionDenied> {
            useCase.changeStatus(randomApplication.id, randomUser.id, Application.Status.InReview, null)
        }
    }

    @Test
    fun `throw perm exc for Operator with new non InReview and app Pending status if it isn't in review already`(

    ): Unit = runBlocking {
        // given
        val randomUser = user(role = User.Role.Operator)
        Mockito.`when`(userDataSource.findUser(randomUser.id)).thenReturn(randomUser)

        val randomApplication = application(status = Application.Status.Pending)
        Mockito.`when`(applicationDataSource.getById(randomApplication.id)).thenReturn(randomApplication)

        Mockito.`when`(reviewsDataSource.checkInReview(randomApplication.id)).thenReturn(false)

        // when + then
        assertThrows<PermissionDenied> {
            useCase.changeStatus(randomApplication.id, randomUser.id, Application.Status.Rejected, null)
        }
    }

    @Test
    fun `change status for Operator with new Approved and app InReview status if user the same`(): Unit = runBlocking {
        // given
        val randomUser = user(role = User.Role.Operator)
        Mockito.`when`(userDataSource.findUser(randomUser.id)).thenReturn(randomUser)

        val randomApplication = application(status = Application.Status.InReview)
        Mockito.`when`(applicationDataSource.getById(randomApplication.id)).thenReturn(randomApplication)

        Mockito.`when`(reviewsDataSource.getReviewerId(randomApplication.id)).thenReturn(randomUser.id)

        // when
        useCase.changeStatus(randomApplication.id, randomUser.id, Application.Status.Approved, null)

        // then
        Mockito.verify(reviewsDataSource, Mockito.times(1))
            .getReviewerId(randomApplication.id)
        Mockito.verify(reviewsDataSource, Mockito.times(1))
            .removeFromReview(randomApplication.id)
        Mockito.verify(applicationDataSource, Mockito.times(1))
            .changeStatus(randomApplication.id, Application.Status.Approved, null)
    }

    @Test
    fun `change status for Operator with new Rejected && app InReview status if user the same`(): Unit = runBlocking {
        // given
        val randomUser = user(role = User.Role.Operator)
        Mockito.`when`(userDataSource.findUser(randomUser.id)).thenReturn(randomUser)

        val randomApplication = application(status = Application.Status.InReview)
        Mockito.`when`(applicationDataSource.getById(randomApplication.id)).thenReturn(randomApplication)

        Mockito.`when`(reviewsDataSource.getReviewerId(randomApplication.id)).thenReturn(randomUser.id)

        // when
        useCase.changeStatus(randomApplication.id, randomUser.id, Application.Status.Rejected, null)

        // then
        Mockito.verify(reviewsDataSource, Mockito.times(1))
            .getReviewerId(randomApplication.id)
        Mockito.verify(reviewsDataSource, Mockito.times(1))
            .removeFromReview(randomApplication.id)
        Mockito.verify(applicationDataSource, Mockito.times(1))
            .changeStatus(randomApplication.id, Application.Status.Rejected, null)
    }

    @Test
    fun `throw perm exc for Operator with any new && app InReview status if user isn't the same`(): Unit = runBlocking {
        // given
        val randomUser = user(role = User.Role.Operator)
        Mockito.`when`(userDataSource.findUser(randomUser.id)).thenReturn(randomUser)

        val randomApplication = application(status = Application.Status.InReview)
        Mockito.`when`(applicationDataSource.getById(randomApplication.id)).thenReturn(randomApplication)

        Mockito.`when`(reviewsDataSource.getReviewerId(randomApplication.id)).thenReturn(generateUserId())

        // when + then
        assertThrows<PermissionDenied> {
            useCase.changeStatus(randomApplication.id, randomUser.id, Application.Status.Rejected, null)
        }
    }

    @Test
    fun `throw perm exc for Operator with non Rejected or Approved new && app InReview status if user is the same`(

    ): Unit = runBlocking {
        // given
        val randomUser = user(role = User.Role.Operator)
        Mockito.`when`(userDataSource.findUser(randomUser.id)).thenReturn(randomUser)

        val randomApplication = application(status = Application.Status.InReview)
        Mockito.`when`(applicationDataSource.getById(randomApplication.id)).thenReturn(randomApplication)

        Mockito.`when`(reviewsDataSource.getReviewerId(randomApplication.id)).thenReturn(randomUser.id)

        // when + then
        assertThrows<PermissionDenied> {
            useCase.changeStatus(randomApplication.id, randomUser.id, Application.Status.Pending, null)
        }
    }

    @Test
    fun `throw perm exc for Operator with any new && app non InReview and Pending status`(

    ): Unit = runBlocking {
        // given
        val randomUser = user(role = User.Role.Operator)
        Mockito.`when`(userDataSource.findUser(randomUser.id)).thenReturn(randomUser)

        val randomApplication = application(status = Application.Status.Rejected)
        Mockito.`when`(applicationDataSource.getById(randomApplication.id)).thenReturn(randomApplication)

        // when + then
        assertThrows<PermissionDenied> {
            useCase.changeStatus(randomApplication.id, randomUser.id, Application.Status.Pending, null)
        }
    }

    @Test
    fun `add to review ds for Admin user if new status inReview`(): Unit = runBlocking {
        // given
        val randomUser = user(role = User.Role.Admin)
        Mockito.`when`(userDataSource.findUser(randomUser.id)).thenReturn(randomUser)

        val randomApplication = application(status = Application.Status.Pending)
        Mockito.`when`(applicationDataSource.getById(randomApplication.id)).thenReturn(randomApplication)

        // when
        useCase.changeStatus(randomApplication.id, randomUser.id, Application.Status.InReview, null)

        // then
        Mockito.verify(reviewsDataSource, Mockito.times(1))
            .addToReview(randomApplication.id, randomUser.id)
        Mockito.verify(applicationDataSource, Mockito.times(1))
            .changeStatus(randomApplication.id, Application.Status.InReview, null)
    }

    @Test
    fun `remove from review ds for Admin user if new status inReview`(): Unit = runBlocking {
        // given
        val randomUser = user(role = User.Role.Admin)
        Mockito.`when`(userDataSource.findUser(randomUser.id)).thenReturn(randomUser)

        val randomApplication = application(status = Application.Status.InReview)
        Mockito.`when`(applicationDataSource.getById(randomApplication.id)).thenReturn(randomApplication)

        // when
        useCase.changeStatus(randomApplication.id, randomUser.id, Application.Status.Pending, null)

        // then
        Mockito.verify(reviewsDataSource, Mockito.times(1))
            .removeFromReview(randomApplication.id)
        Mockito.verify(applicationDataSource, Mockito.times(1))
            .changeStatus(randomApplication.id, Application.Status.Pending, null)
    }
}
