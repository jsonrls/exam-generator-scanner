package com.pbec.preboardexamchecker.ui.account

/** One verification check. Every continuation belongs to the session that started it. */
internal class EmailProfileSync(private val service: Service) {
    interface Service {
        fun isCurrentSession(): Boolean
        fun reload(callback: (Result<String>) -> Unit)
        fun refreshToken(callback: (Result<Unit>) -> Unit)
        fun updateProfile(email: String, callback: (Result<Unit>) -> Unit)
        fun commit(email: String, clearPending: Boolean)
    }

    sealed class Outcome {
        data class Unverified(val email: String) : Outcome()
        data class Synced(val email: String, val verified: Boolean) : Outcome()
        data class Failed(val stage: String, val error: Throwable, val verifiedEmail: String? = null) : Outcome()
    }

    fun check(pending: String?, onVerified: (String) -> Unit, onComplete: (Outcome) -> Unit) {
        service.reload reload@ { result ->
            if (!service.isCurrentSession()) return@reload
            val email = result.getOrElse {
                onComplete(Outcome.Failed("email_reload", it)); return@reload
            }
            if (email.isBlank()) {
                onComplete(Outcome.Failed("email_reload", IllegalStateException("Missing email")))
                return@reload
            }
            val verified = pending != null && pending.equals(email, true)
            if (pending != null && !verified) {
                onComplete(Outcome.Unverified(email))
                return@reload
            }
            if (verified) onVerified(email)
            service.refreshToken refresh@ { token ->
                if (!service.isCurrentSession()) return@refresh
                token.exceptionOrNull()?.let {
                    onComplete(Outcome.Failed("email_refresh", it, email.takeIf { verified })); return@refresh
                }
                service.updateProfile(email) update@ { update ->
                    if (!service.isCurrentSession()) return@update
                    update.exceptionOrNull()?.let {
                        onComplete(Outcome.Failed("email_profile_sync", it, email.takeIf { verified })); return@update
                    }
                    service.commit(email, verified)
                    onComplete(Outcome.Synced(email, verified))
                }
            }
        }
    }
}
