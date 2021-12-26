package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.any
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.generateUserId
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.times
import kotlin.test.Test
import kotlin.test.assertEquals

class OperatorManagementUseCaseImplTest {
    private val ds = Mockito.mock(UserDataSource::class.java)
    private val useCase = OperatorManagementUseCaseImpl(ds)

    @Test
    fun `test add user`() = runBlocking {
        // GIVEN
        Mockito.`when`(ds.findUser(any<String>())).thenReturn(null)
        Mockito.`when`(ds.addUser(any())).thenReturn(Unit)

        // WHEN
        useCase.add("C.C.")

        // THEN
        Mockito.verify(ds, times(1)).addUser(any())
    }

    @Test
    fun `test add duplicated user`() = runBlocking {
        // GIVEN
        Mockito.`when`(ds.findUser(any<String>())).thenReturn(User(
            id = generateUserId(),
            name = "C.C.",
            role = User.Role.Operator,
        ))

        // WHEN + THEN
        assertThrows<IllegalStateException> {
            useCase.add("C.C.")
        }

        return@runBlocking
    }

    @Test
    fun `test get all operators`() = runBlocking {
        // GIVEN
        val operators = listOf(
            User(
                id = generateUserId(),
                name = "C.C.",
                role = User.Role.Operator,
            ),
            User(
                id = generateUserId(),
                name = "Shirley Fenette",
                role = User.Role.Operator,
            ),
            User(
                id = generateUserId(),
                name = "Euphemia li Britannia",
                role = User.Role.Operator,
            )
        )
        Mockito.`when`(ds.findByRole(User.Role.Operator)).thenReturn(operators)

        // WHEN
        val received = useCase.getAll()

        // THEN
        assertEquals(operators, received)
    }

    @Test
    fun `test get all operators when list is empty`() = runBlocking {
        // GIVEN
        val operators = listOf<User>()
        Mockito.`when`(ds.findByRole(User.Role.Operator)).thenReturn(operators)

        // WHEN
        val received = useCase.getAll()

        // THEN
        assertEquals(operators, received)
    }

    @Test
    fun `test delete operator`() = runBlocking {
        // GIVEN
        val user = User(
            id = generateUserId(),
            name = "Charles zi Britannia",
            role = User.Role.Operator
        )

        Mockito.`when`(ds.findUser(user.id)).thenReturn(user)
        Mockito.`when`(ds.deleteUser(user)).thenReturn(Unit)

        // WHEN
        useCase.delete(user.id)

        // THEN
        Mockito.verify(ds, times(1)).findUser(user.id)
        Mockito.verify(ds, times(1)).deleteUser(user)

        return@runBlocking
    }

    @Test
    fun `test delete non-existing operator`() = runBlocking {
        // GIVEN
        val user = User(
            id = generateUserId(),
            name = "Charles zi Britannia",
            role = User.Role.Operator
        )

        Mockito.`when`(ds.findUser(user.id)).thenReturn(null)

        // WHEN + THEN
        assertThrows<IllegalStateException> {
            useCase.delete(user.id)
        }

        Mockito.verify(ds, times(0)).deleteUser(user)
        return@runBlocking
    }

    @Test
    fun `test delete user that is not an operator`() = runBlocking {
        // GIVEN
        val user = User(
            id = generateUserId(),
            name = "Charles zi Britannia",
            role = User.Role.Admin
        )

        Mockito.`when`(ds.findUser(user.id)).thenReturn(user)

        // WHEN + THEN
        assertThrows<IllegalStateException> {
            useCase.delete(user.id)
        }

        Mockito.verify(ds, times(0)).deleteUser(user)
        return@runBlocking
    }
}
