package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.*
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.mock
import com.fictadvisor.pryomka.verify
import com.fictadvisor.pryomka.whenever
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class RegisterStaffUseCaseImplTest {
    private val ds: UserDataSource = mock()
    private val useCase: RegisterStaffUseCase = RegisterStaffUseCaseImpl(ds)

    private val login = "lelouch"
    private val password = "lamperouge"

    @Test
    fun `register operator`(): Unit = runBlocking {
        // GIVEN
        val operator = operator(name = login)
        whenever(ds.findStaffByCredentials(login, password)).thenReturn(operator)

        // WHEN
        val result = useCase.register(login, password, User.Staff.Role.Operator)

        // THEN
        assertEquals(operator, result)
        verify(ds).registerStaff(login, password, User.Staff.Role.Operator)
    }

    @Test
    fun `register admin`(): Unit = runBlocking {
        // GIVEN
        val admin = admin(name = login)
        whenever(ds.findAllByRole(User.Staff.Role.Admin)).thenReturn(listOf())
        whenever(ds.findStaffByCredentials(login, password)).thenReturn(admin)

        // WHEN
        val result = useCase.register(login, password, User.Staff.Role.Admin)

        // THEN
        assertEquals(admin, result)
        verify(ds).findAllByRole(User.Staff.Role.Admin)
        verify(ds).registerStaff(login, password, User.Staff.Role.Admin)
    }

    @Test
    fun `should not register duplicate admin`(): Unit = runBlocking {
        // GIVEN
        val admin = admin(name = login)
        whenever(ds.findAllByRole(User.Staff.Role.Admin)).thenReturn(listOf(admin))

        // WHEN+THEN
        assertThrows<IllegalStateException> {
            useCase.register(login, password, User.Staff.Role.Admin)
        }
    }
}
