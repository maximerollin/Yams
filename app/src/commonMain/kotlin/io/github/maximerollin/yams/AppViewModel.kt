package io.github.maximerollin.yams

import androidx.lifecycle.ViewModel
import io.github.maximerollin.yams.feature.home.navigation.HomeRoute
import io.github.maximerollin.yams.feature.welcome.navigation.WelcomeRoute
import kotlinx.coroutines.flow.first

class AppViewModel(
) : ViewModel() {
    lateinit var startDestination: Any
        private set

    suspend fun initializeApp() {
        initStartDestination()
    }

    private suspend fun initStartDestination() {
        startDestination = when {
            0 == 0 -> WelcomeRoute
            else -> HomeRoute // TODO: Change to HomeRoute when available
        }
    }
}
