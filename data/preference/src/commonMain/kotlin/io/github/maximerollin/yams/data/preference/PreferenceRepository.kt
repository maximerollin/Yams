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
    public fun getIsUserOrderRandomized(): Flow<Boolean>
    public suspend fun setIsUserOrderRandomized(isUserOrderRandomized: Boolean)
    public fun getGameSettings(): Flow<GameSettings>
    public suspend fun setGameSettings(settings: GameSettings)
    public fun getIsHapticFeedbackEnabled(): Flow<Boolean>
    public suspend fun setIsHapticFeedbackEnabled(isEnabled: Boolean)
    public fun getGamePlayUiDensity(): Flow<GamePlayUiDensity>
    public suspend fun setGamePlayUiDensity(density: GamePlayUiDensity)
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

    override fun getYamsPlusStatus(): Flow<Boolean> {
        return preferenceLocalDataSource.getYamsPlusStatus()
    }

    override suspend fun setYamsPlusStatus(isSubscribed: Boolean) {
        preferenceLocalDataSource.setYamsPlusStatus(isSubscribed)
    }
}
