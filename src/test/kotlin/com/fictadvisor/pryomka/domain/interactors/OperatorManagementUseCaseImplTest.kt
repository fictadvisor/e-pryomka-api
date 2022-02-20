package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.operator
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.times
import kotlin.test.Test
import kotlin.test.assertEquals

class OperatorManagementUseCaseImplTest {
    private val userDataSource = Mockito.mock(UserDataSource::class.java)
    private val registerStaffUseCase = Mockito.mock(RegisterStaffUseCase::class.java)
    private val useCase = OperatorManagementUseCaseImpl(userDataSource, registerStaffUseCase)

    private val login = "lelouch"
    private val password = "lamperouge"
    private val operator = operator(name = login)

    @Test
    fun `should register operator`(): Unit = runBlocking {
        // GIVEN
        Mockito.`when`(userDataSource.findStaffByCredentials(login, password))
            .thenReturn(null)
        Mockito.`when`(registerStaffUseCase.register(login, password, User.Staff.Role.Operator))
            .thenReturn(operator)

        // WHEN
        useCase.add(login, password)

        // THEN
        Mockito.verify(userDataSource, times(1))
            .findStaffByCredentials(login, null)
        Mockito.verify(registerStaffUseCase, times(1))
            .register(login, password, User.Staff.Role.Operator)
    }

    @Test
    fun `register duplicated operator`(): Unit = runBlocking {
        // GIVEN
        Mockito.`when`(userDataSource.findStaffByCredentials(login, null)).thenReturn(operator)

        // WHEN + THEN
        assertThrows<IllegalStateException> { useCase.add(login, password) }
    }

    @Test
    fun `get all operators`(): Unit = runBlocking {
        // GIVEN
        val operators = listOf(
            operator(name = "C.C."),
            operator(name = "Shirley"),
            operator(name = "Euphemia")
        )
        Mockito.`when`(userDataSource.findAllByRole(User.Staff.Role.Operator)).thenReturn(operators)

        // WHEN+THEN
        assertEquals(operators, useCase.getAll())
    }

    @Test
    fun `get all operators when list is empty`() = runBlocking {
        // GIVEN
        Mockito.`when`(userDataSource.findAllByRole(User.Staff.Role.Operator)).thenReturn(listOf())

        // WHEN+THEN
        assertEquals(listOf(), useCase.getAll())
    }

    @Test
    fun `delete operator`(): Unit = runBlocking {
        // GIVEN
        Mockito.`when`(userDataSource.deleteStaff(operator.id)).thenReturn(Unit)

        // WHEN
        useCase.delete(operator.id)

        // THEN
        Mockito.verify(userDataSource, times(1)).deleteStaff(operator.id)
    }

    @Test
    fun `should return nothing on delete non-existing operator`(): Unit = runBlocking {
        // GIVEN
        Mockito.`when`(userDataSource.deleteStaff(operator.id)).thenReturn(Unit)

        // WHEN
        useCase.delete(operator.id)

        // THEN
        Mockito.verify(userDataSource, times(1)).deleteStaff(operator.id)
    }
}
