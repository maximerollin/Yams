package io.github.maximerollin.yams

import androidx.lifecycle.ViewModel
import io.github.maximerollin.yams.feature.home.navigation.HomeRoute
import io.github.maximerollin.yams.feature.welcome.navigation.WelcomeRoute
import io.github.maximerollin.yams.data.game.GameRepository
import kotlinx.coroutines.flow.first

class AppViewModel(
    private val gameRepository: GameRepository,
) : ViewModel() {
    lateinit var startDestination: Any
        private set

    suspend fun initializeApp() {
        initStartDestination()
    }

    private suspend fun initStartDestination() {
        val gameCount = gameRepository.getNumberOfGames().first()
        startDestination = when {
            gameCount == 0 -> WelcomeRoute
            else -> HomeRoute
        }
    }
}
