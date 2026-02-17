package core.supabase.domain.repo

import core.supabase.domain.model.AuthProvider
import core.supabase.domain.model.EmailCredentials
import core.supabase.domain.model.SupabaseUser
import kotlinx.coroutines.flow.Flow

interface SupabaseAuthRepo {
    /**
     * Sign in with email and password
     * @return Result containing SupabaseUser or SupabaseError
     */
    suspend fun signInWithEmail(credentials: EmailCredentials): Result<SupabaseUser>

    /**
     * Sign up with email and password
     * @return Result containing SupabaseUser or SupabaseError
     */
    suspend fun signUpWithEmail(credentials: EmailCredentials): Result<SupabaseUser>

    /**
     * Initiate OAuth flow (Google, Apple)
     * @param provider OAuth provider
     * @param redirectUrl Deep link URL for callback
     * @return Result indicating success or failure
     */
    suspend fun signInWithOAuth(provider: AuthProvider, redirectUrl: String): Result<Unit>

    /**
     * Get current authenticated user
     * @return SupabaseUser if authenticated, null otherwise
     */
    suspend fun getCurrentUser(): SupabaseUser?

    /**
     * Observe authentication state changes
     * @return Flow emitting SupabaseUser or null
     */
    fun observeAuthState(): Flow<SupabaseUser?>

    /**
     * Send password reset email
     * @param email User email address
     * @return Result indicating success or failure
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    /**
     * Update password with reset token
     * @param newPassword New password
     * @return Result indicating success or failure
     */
    suspend fun updatePassword(newPassword: String): Result<Unit>

    /**
     * Sign out current user
     */
    suspend fun signOut(): Result<Unit>
}
