package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.any
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.User
import kotlinx.coroutines.runBlocking
import org.mockito.Mockito
import kotlin.test.Test
import kotlin.test.assertEquals

internal class CreateUserUseCaseImplTest {
    private val ds = Mockito.mock(UserDataSource::class.java)
    private val useCase = CreateUserUseCaseImpl(ds)

    @Test
    fun `test created user is an entrant by default`() = runBlocking {
        // GIVEN
        var receivedUser: User? = null
        Mockito.`when`(ds.addUser(any())).then {
            receivedUser = it.getArgument(0, User::class.java)
            return@then Unit
        }

        // WHEN
        useCase("Test user 1")

        // THEN
        assertEquals(User.Role.Entrant, receivedUser?.role)
    }

    @Test
    fun `test user is created with specified name`() = runBlocking {
        // GIVEN
        var receivedUser: User? = null
        Mockito.`when`(ds.addUser(any())).then {
            receivedUser = it.getArgument(0, User::class.java)
            return@then Unit
        }

        // WHEN
        useCase("Test user 1")

        // THEN
        assertEquals("Test user 1", receivedUser?.name)
    }

    @Test
    fun `test created user is the same that was returned from use case`() = runBlocking {
        // GIVEN
        var receivedUser: User? = null
        Mockito.`when`(ds.addUser(any())).then {
            receivedUser = it.getArgument(0, User::class.java)
            return@then Unit
        }

        // WHEN
        val createdUser = useCase("Test user 1")

        // THEN
        assertEquals(receivedUser, createdUser)
    }
}
