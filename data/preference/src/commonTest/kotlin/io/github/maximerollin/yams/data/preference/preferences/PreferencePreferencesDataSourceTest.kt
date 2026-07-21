package io.github.maximerollin.yams.data.preference.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PreferencePreferencesDataSourceTest {
    @Test
    fun gamePlayDensityDiscoveryIsUnseenByDefaultAndPersistsDismissal() = runTest {
        val dataStore = PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                FileSystem.SYSTEM_TEMPORARY_DIRECTORY /
                    "yams-density-discovery-${Random.nextInt()}.preferences_pb"
            },
        )
        val dataSource = PreferencePreferencesDataSource(dataStore)

        assertFalse(dataSource.getHasSeenGamePlayDensityDiscovery().first())

        dataSource.setHasSeenGamePlayDensityDiscovery()

        assertTrue(dataSource.getHasSeenGamePlayDensityDiscovery().first())
    }

    @Test
    fun inAppReviewDiscoveryIsUnseenByDefaultAndPersistsDismissal() = runTest {
        val dataStore = PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                FileSystem.SYSTEM_TEMPORARY_DIRECTORY /
                    "yams-in-app-review-discovery-${Random.nextInt()}.preferences_pb"
            },
        )
        val dataSource = PreferencePreferencesDataSource(dataStore)

        assertFalse(dataSource.getHasSeenInAppReviewDiscovery().first())

        dataSource.setHasSeenInAppReviewDiscovery()

        assertTrue(dataSource.getHasSeenInAppReviewDiscovery().first())
    }
}
