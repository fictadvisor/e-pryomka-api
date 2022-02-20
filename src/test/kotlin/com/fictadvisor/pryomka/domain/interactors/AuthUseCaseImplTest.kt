package com.fictadvisor.pryomka.domain.interactors

import com.auth0.jwt.JWT
import com.fictadvisor.pryomka.*
import com.fictadvisor.pryomka.any
import com.fictadvisor.pryomka.domain.datasource.TokenDataSource
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.TokenMetadata
import com.fictadvisor.pryomka.mock
import com.fictadvisor.pryomka.verify
import com.fictadvisor.pryomka.whenever
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.*

class AuthUseCaseImplTest {
    private val userDataSource: UserDataSource = mock()
    private val tokenDataSource: TokenDataSource = mock()
    private val config = AuthUseCase.Config(
        accessTTL = 60 * 1000L,
        refreshTTL = 5 * 60 * 1000L,
        audience = "e-pryomka",
        issuer = "fictadvisor",
        secret = "9+FaLoftq7pK0mXiQf5IfH4tpYYJ6zutDfk28jSX5uQ=",
        realm = "vstup",
        tgBotId = "4002278938:ABGEHE_2_9razcj9t1zAw1JaYA31zz16bQp",
    )

    private val future = Instant.DISTANT_FUTURE.toEpochMilliseconds().let(::Date)
    private val past = Instant.DISTANT_PAST.toEpochMilliseconds().let(::Date)

    private val useCase = AuthUseCaseImpl(userDataSource, tokenDataSource, config)

    private val login = "lelouch"
    private val password = "lamperouge"
    private val operator = operator(name = "lelouch")
    private val accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJodHRwczovL3ZzdHVwLmZpY3RhZHZpc29yLmNvbS9hcGkvIiwidXNlcl9pZCI6ImUxNGNmZGZmLTM1ZTEtNGNlYi05NzgwLTkyMDYxNjEwMjU3MiIsImlzcyI6Imh0dHBzOi8vdnN0dXAuZmljdGFkdmlzb3IuY29tLyIsImV4cCI6MTY0NTM0OTY0NH0.rxLeRplrgsl8VfSNZHqQegBdvPSGSFFpGcQmKN9bN8c"
    private val refreshToken = "t5hsSBhlpS6fVu92go4liGbthT3ta6frmexqLaT674g="

    @Test
    fun `should generate tokens for staff login`(): Unit = runBlocking {
        // GIVEN
        whenever(userDataSource.findStaffByCredentials(login, password)).thenReturn(operator)

        // WHEN
        val now = Date()
        val (access, refresh) = useCase.logIn(login, password)
        val decodedJWT = JWT.decode(access)

        // THEN
        assertTrue(refresh.isNotBlank(), "refresh token should not be empty")
        assertEquals(config.audience, decodedJWT.audience.first())
        assertEquals(config.issuer, decodedJWT.issuer)
        assertEquals(operator.id.value.toString(), decodedJWT.getClaim("user_id").asString())
        assertTrue("expiredAt should be in future") { decodedJWT.expiresAt.after(now) }

        verify(tokenDataSource, 2).saveToken(any(), any())
    }

    @Test
    fun `should not generate tokens is staff not found`(): Unit = runBlocking {
        // GIVEN
        whenever(userDataSource.findStaffByCredentials(login, password)).thenReturn(null)

        // WHEN+THEN
        assertThrows<IllegalStateException> { useCase.logIn(login, password) }
        verify(tokenDataSource, 0).saveToken(any(), any())
    }

    @Test
    fun `should refresh both tokens`(): Unit = runBlocking {
        // GIVEN
        whenever(tokenDataSource.findRefreshToken(refreshToken)).thenReturn(
            TokenMetadata(
                userId = operator.id,
                validUntil = future,
                type = TokenMetadata.Type.Refresh
            )
        )

        // WHEN
        val (access, refresh) = useCase.refresh(refreshToken)

        // THEN
        assertTrue(access.isNotBlank(), "should return access token")
        assertTrue(refresh.isNotBlank(), "should return refresh token")

        verify(tokenDataSource).deleteToken(refreshToken)
        verify(tokenDataSource, 2).saveToken(any(), any())
    }

    @Test
    fun `should not refresh non-existent token`(): Unit = runBlocking {
        // GIVEN
        whenever(tokenDataSource.findRefreshToken(refreshToken)).thenReturn(null)

        // WHEN+THEN
        assertThrows<IllegalStateException> { useCase.refresh(refreshToken) }
        verify(tokenDataSource, 0).deleteToken(refreshToken)
        verify(tokenDataSource, 0).saveToken(any(), any())
    }

    @Test
    fun `should not refresh expired token but should delete it`(): Unit = runBlocking {
        // GIVEN
        whenever(tokenDataSource.findRefreshToken(refreshToken)).thenReturn(
            TokenMetadata(
                userId = operator.id,
                validUntil = past,
                type = TokenMetadata.Type.Refresh
            )
        )

        // WHEN+THEN
        assertThrows<IllegalStateException> { useCase.refresh(refreshToken) }
        verify(tokenDataSource, 1).deleteToken(refreshToken)
        verify(tokenDataSource, 0).saveToken(any(), any())
    }

    @Test
    fun `should authenticate access token`() = runBlocking {
        // GIVEN
        whenever(tokenDataSource.findAccessToken(accessToken)).thenReturn(
            TokenMetadata(
                userId = operator.id,
                validUntil = future,
                type = TokenMetadata.Type.Access
            )
        )

        // WHEN+THEN
        assertTrue(useCase.auth(accessToken))
    }

    @Test
    fun `should not authenticate non-existent access token`() = runBlocking {
        // GIVEN
        whenever(tokenDataSource.findAccessToken(accessToken)).thenReturn(null)

        // WHEN+THEN
        assertFalse(useCase.auth(accessToken))
    }

    @Test
    fun `should not authenticate expired access token but should delete it`() = runBlocking {
        // GIVEN
        whenever(tokenDataSource.findAccessToken(accessToken)).thenReturn(
            TokenMetadata(
                userId = operator.id,
                validUntil = past,
                type = TokenMetadata.Type.Access
            )
        )

        // WHEN+THEN
        assertFalse(useCase.auth(accessToken))
        verify(tokenDataSource, 1).deleteToken(accessToken)
    }

    @Test
    fun `should find entrant`(): Unit = runBlocking {
        // GIVEN
        val entrant = entrant()
        whenever(userDataSource.findEntrant(entrant.id)).thenReturn(entrant)

        // WHEN+THEN
        assertEquals(entrant, useCase.findUser(entrant.id))
    }

    @Test
    fun `should find operator`(): Unit = runBlocking {
        // GIVEN
        val operator = operator()
        whenever(userDataSource.findEntrant(operator.id)).thenReturn(null)
        whenever(userDataSource.findStaff(operator.id)).thenReturn(operator)

        // WHEN+THEN
        assertEquals(operator, useCase.findUser(operator.id))
    }

    @Test
    fun `should find admin`(): Unit = runBlocking {
        // GIVEN
        val admin = admin()
        whenever(userDataSource.findEntrant(admin.id)).thenReturn(null)
        whenever(userDataSource.findStaff(admin.id)).thenReturn(admin)

        // WHEN+THEN
        assertEquals(admin, useCase.findUser(admin.id))
    }

    @Test
    fun `should return null if user not found`(): Unit = runBlocking {
        // GIVEN
        val operator = operator()
        whenever(userDataSource.findEntrant(operator.id)).thenReturn(null)
        whenever(userDataSource.findStaff(operator.id)).thenReturn(null)

        // WHEN+THEN
        assertNull(useCase.findUser(operator.id))
    }

    @Test
    fun `getMe for entrant`(): Unit = runBlocking {
        // GIVEN
        val entrant = entrant()
        whenever(tokenDataSource.findAccessToken(accessToken)).thenReturn(
            TokenMetadata(
                userId = entrant.id,
                validUntil = future,
                type = TokenMetadata.Type.Access
            )
        )
        whenever(userDataSource.findEntrant(entrant.id)).thenReturn(entrant)

        // WHEN
        val me = useCase.getMe(accessToken)

        // THEN
        assertEquals(entrant, me)
    }

    @Test
    fun `getMe for operator`(): Unit = runBlocking {
        // GIVEN
        val operator = operator()
        whenever(tokenDataSource.findAccessToken(accessToken)).thenReturn(
            TokenMetadata(
                userId = operator.id,
                validUntil = future,
                type = TokenMetadata.Type.Access
            )
        )
        whenever(userDataSource.findEntrant(operator.id)).thenReturn(null)
        whenever(userDataSource.findStaff(operator.id)).thenReturn(operator)

        // WHEN
        val me = useCase.getMe(accessToken)

        // THEN
        assertEquals(operator, me)
    }

    @Test
    fun `getMe for admin`(): Unit = runBlocking {
        // GIVEN
        val admin = admin()
        whenever(tokenDataSource.findAccessToken(accessToken)).thenReturn(
            TokenMetadata(
                userId = admin.id,
                validUntil = future,
                type = TokenMetadata.Type.Access
            )
        )
        whenever(userDataSource.findEntrant(admin.id)).thenReturn(null)
        whenever(userDataSource.findStaff(admin.id)).thenReturn(admin)

        // WHEN
        val me = useCase.getMe(accessToken)

        // THEN
        assertEquals(admin, me)
    }

    @Test
    fun `should return null on getMe with expired token`(): Unit = runBlocking {
        // GIVEN
        val entrant = entrant()
        whenever(tokenDataSource.findAccessToken(accessToken)).thenReturn(
            TokenMetadata(
                userId = entrant.id,
                validUntil = past,
                type = TokenMetadata.Type.Access
            )
        )
        whenever(userDataSource.findEntrant(entrant.id)).thenReturn(entrant)

        // WHEN
        val me = useCase.getMe(accessToken)

        // THEN
        assertNull(me)
    }

    @Test
    fun `should return null on getMe with non-existent token`(): Unit = runBlocking {
        // GIVEN
        val entrant = entrant()
        whenever(tokenDataSource.findAccessToken(accessToken)).thenReturn(null)
        whenever(userDataSource.findEntrant(entrant.id)).thenReturn(entrant)

        // WHEN
        val me = useCase.getMe(accessToken)

        // THEN
        assertNull(me)
    }
}
