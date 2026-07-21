package io.github.maximerollin.yams.data.preference

import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.data.preference.preferences.PreferenceLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.Instant

public interface PreferenceRepository {
    public fun getInAppReviewShownDate(): Flow<Instant?>
    public suspend fun inAppReviewShown()
    public fun getIsInAppReviewPending(): Flow<Boolean>
    public suspend fun setIsInAppReviewPending(isPending: Boolean)
    public fun getIsUserOrderRandomized(): Flow<Boolean>
    public suspend fun setIsUserOrderRandomized(isUserOrderRandomized: Boolean)
    public fun getGameSettings(): Flow<GameSettings>
    public suspend fun setGameSettings(settings: GameSettings)
    public fun getIsHapticFeedbackEnabled(): Flow<Boolean>
    public suspend fun setIsHapticFeedbackEnabled(isEnabled: Boolean)
    public fun getGamePlayUiDensity(): Flow<GamePlayUiDensity>
    public suspend fun setGamePlayUiDensity(density: GamePlayUiDensity)
    public fun getHasSeenGamePlayDensityDiscovery(): Flow<Boolean>
    public suspend fun setHasSeenGamePlayDensityDiscovery()
    public fun getHasSeenInAppReviewDiscovery(): Flow<Boolean>
    public suspend fun setHasSeenInAppReviewDiscovery()
    public fun getYamsPlusStatus(): Flow<Boolean>
    public suspend fun setYamsPlusStatus(isSubscribed: Boolean)
}

internal class DefaultPreferenceRepository(
    private val preferenceLocalDataSource: PreferenceLocalDataSource
) : PreferenceRepository {
    override fun getInAppReviewShownDate(): Flow<Instant?> {
        return preferenceLocalDataSource.getLastInAppReviewShownDate()
    }

    override suspend fun inAppReviewShown() {
        preferenceLocalDataSource.setLastInAppReviewShownDate(Clock.System.now())
    }

    override fun getIsInAppReviewPending(): Flow<Boolean> {
        return preferenceLocalDataSource.getIsInAppReviewPending()
    }

    override suspend fun setIsInAppReviewPending(isPending: Boolean) {
        preferenceLocalDataSource.setIsInAppReviewPending(isPending)
    }

    override fun getIsUserOrderRandomized(): Flow<Boolean> {
        return preferenceLocalDataSource.getIsUserOrderRandomized()
    }

    override suspend fun setIsUserOrderRandomized(isUserOrderRandomized: Boolean) {
        preferenceLocalDataSource.setIsUserOrderRandomized(isUserOrderRandomized)
    }

    override fun getGameSettings(): Flow<GameSettings> {
        return preferenceLocalDataSource.getGameSettings().map { it ?: GameSettings.YamsSettings() }
    }

    override suspend fun setGameSettings(settings: GameSettings) {
        preferenceLocalDataSource.setGameSettings(settings)
    }

    override fun getIsHapticFeedbackEnabled(): Flow<Boolean> {
        return preferenceLocalDataSource.getIsHapticFeedbackEnabled()
    }

    override suspend fun setIsHapticFeedbackEnabled(isEnabled: Boolean) {
        preferenceLocalDataSource.setIsHapticFeedbackEnabled(isEnabled)
    }

    override fun getGamePlayUiDensity(): Flow<GamePlayUiDensity> {
        return preferenceLocalDataSource.getGamePlayUiDensity()
    }

    override suspend fun setGamePlayUiDensity(density: GamePlayUiDensity) {
        preferenceLocalDataSource.setGamePlayUiDensity(density)
    }

    override fun getHasSeenGamePlayDensityDiscovery(): Flow<Boolean> {
        return preferenceLocalDataSource.getHasSeenGamePlayDensityDiscovery()
    }

    override suspend fun setHasSeenGamePlayDensityDiscovery() {
        preferenceLocalDataSource.setHasSeenGamePlayDensityDiscovery()
    }

    override fun getHasSeenInAppReviewDiscovery(): Flow<Boolean> {
        return preferenceLocalDataSource.getHasSeenInAppReviewDiscovery()
    }

    override suspend fun setHasSeenInAppReviewDiscovery() {
        preferenceLocalDataSource.setHasSeenInAppReviewDiscovery()
    }

    override fun getYamsPlusStatus(): Flow<Boolean> {
        return preferenceLocalDataSource.getYamsPlusStatus()
    }

    override suspend fun setYamsPlusStatus(isSubscribed: Boolean) {
        preferenceLocalDataSource.setYamsPlusStatus(isSubscribed)
    }
}
