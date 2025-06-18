package br.com.challenge.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.challenge.core.network.ApiConfig
import br.com.challenge.features.exchanges.presentation.detail.ExchangeDetailScreen
import br.com.challenge.features.exchanges.presentation.list.ExchangeListScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    apiConfig: ApiConfig,
) {
    NavHost(navController, startDestination = "exchange_list", modifier = Modifier.testTag(NAV_HOST)) {
        composable("exchange_list") {
            ExchangeListScreen(
                navController,
                apiConfig,
            )
        }

        composable("exchange_detail/{exchangeId}") { backStackEntry ->
            val exchangeId = backStackEntry.arguments?.getString("exchangeId") ?: ""
            ExchangeDetailScreen(
                exchangeId = exchangeId,
                onBackClick = { navController.popBackStack() },
                apiConfig = apiConfig,
            )
        }
    }
}
