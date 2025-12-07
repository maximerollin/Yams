package io.github.maximerollin.yams.data.preference

import io.github.maximerollin.yams.data.preference.preferences.PreferenceLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock
import kotlin.time.Instant

public interface PreferenceRepository {
    public fun getInAppReviewShownDate(): Flow<Instant?>
    public suspend fun inAppReviewShown()
    public fun getIsUserOrderRandomized(): Flow<Boolean>
    public suspend fun setIsUserOrderRandomized(isUserOrderRandomized: Boolean)
    // TODO add game settings
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
}
