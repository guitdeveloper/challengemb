package br.com.challenge.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.compose.rememberNavController
import br.com.challenge.ui.theme.ChallengeTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val apiConfig by viewModel.apiConfig.collectAsState()

    ChallengeTheme {
        if (apiConfig != null) {
            val navController = rememberNavController()
            AppNavHost(navController, apiConfig!!)
        } else {
            Box(
                modifier = Modifier.fillMaxSize().testTag(LOADING_INDICATOR),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
