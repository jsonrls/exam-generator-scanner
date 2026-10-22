package com.pbec.preboardexamchecker.ui.account

import org.junit.Assert.*
import org.junit.Test

class EmailProfileSyncTest {
    private class FakeService : EmailProfileSync.Service {
        var uid = "teacher-a"
        var storedEmail = "old@example.com"
        var pending: String? = "new@example.com"
        var tokenCalls = 0
        var profileCalls = 0
        lateinit var reloadResult: (Result<String>) -> Unit
        lateinit var tokenResult: (Result<Unit>) -> Unit
        lateinit var profileResult: (Result<Unit>) -> Unit
        override fun isCurrentSession() = uid == "teacher-a"
        override fun reload(callback: (Result<String>) -> Unit) { reloadResult = callback }
        override fun refreshToken(callback: (Result<Unit>) -> Unit) { tokenCalls++; tokenResult = callback }
        override fun updateProfile(email: String, callback: (Result<Unit>) -> Unit) {
            assertEquals("new@example.com", email)
            profileCalls++
            profileResult = callback
        }
        override fun commit(email: String, clearPending: Boolean) {
            storedEmail = email
            if (clearPending) pending = null
        }
    }

    @Test fun unverifiedAddressDoesNotRefreshOrUpdateProfile() {
        val service = FakeService()
        var outcome: EmailProfileSync.Outcome? = null
        EmailProfileSync(service).check(service.pending, { fail("Not verified") }) { outcome = it }
        service.reloadResult(Result.success("old@example.com"))
        assertTrue(outcome is EmailProfileSync.Outcome.Unverified)
        assertEquals(0, service.tokenCalls)
        assertEquals(0, service.profileCalls)
        assertEquals("new@example.com", service.pending)
    }

    @Test fun switchingAccountDuringAnyStageCannotCommitOrComplete() {
        for (stage in 0..2) {
            val service = FakeService()
            var completed = false
            EmailProfileSync(service).check(service.pending, {}) { completed = true }
            if (stage == 0) service.uid = "teacher-b"
            service.reloadResult(Result.success("new@example.com"))
            if (stage >= 1) {
                if (stage == 1) service.uid = "teacher-b"
                service.tokenResult(Result.success(Unit))
            }
            if (stage == 2) {
                service.uid = "teacher-b"
                service.profileResult(Result.success(Unit))
            }
            assertFalse(completed)
            assertEquals("old@example.com", service.storedEmail)
            assertEquals("new@example.com", service.pending)
        }
    }

    @Test fun tokenFailureKeepsPendingAndAllowsSuccessfulRetry() = failureThenRetry(tokenFailure = true)

    @Test fun profileFailureKeepsPendingAndAllowsSuccessfulRetry() = failureThenRetry(tokenFailure = false)

    private fun failureThenRetry(tokenFailure: Boolean) {
        val service = FakeService()
        var outcome: EmailProfileSync.Outcome? = null
        var verified: String? = null
        val sync = EmailProfileSync(service)
        sync.check(service.pending, { verified = it }) { outcome = it }
        service.reloadResult(Result.success("new@example.com"))
        if (tokenFailure) service.tokenResult(Result.failure(IllegalStateException("offline")))
        else {
            service.tokenResult(Result.success(Unit))
            service.profileResult(Result.failure(IllegalStateException("offline")))
        }
        assertEquals("new@example.com", verified)
        assertEquals("new@example.com", (outcome as EmailProfileSync.Outcome.Failed).verifiedEmail)
        assertEquals("old@example.com", service.storedEmail)
        assertEquals("new@example.com", service.pending)

        sync.check(service.pending, {}) { outcome = it }
        service.reloadResult(Result.success("new@example.com"))
        service.tokenResult(Result.success(Unit))
        service.profileResult(Result.success(Unit))
        assertEquals(EmailProfileSync.Outcome.Synced("new@example.com", true), outcome)
        assertEquals("new@example.com", service.storedEmail)
        assertNull(service.pending)
    }

    @Test fun reloadFailureDoesNotTouchStoredIdentity() {
        val service = FakeService()
        var outcome: EmailProfileSync.Outcome? = null
        EmailProfileSync(service).check(service.pending, {}) { outcome = it }
        service.reloadResult(Result.failure(IllegalStateException("offline")))
        assertEquals("email_reload", (outcome as EmailProfileSync.Outcome.Failed).stage)
        assertEquals(0, service.tokenCalls)
        assertEquals("old@example.com", service.storedEmail)
        assertNotNull(service.pending)
    }
}
