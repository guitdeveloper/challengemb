package br.com.challenge.features.exchanges.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import br.com.challenge.core.network.ApiConfig
import br.com.challenge.core.network.exception.NoConnectivityException
import br.com.challenge.core.presentation.component.LoadingComponent
import br.com.challenge.core.presentation.component.ErrorComponent
import br.com.challenge.core.presentation.state.UiState
import br.com.challenge.features.exchanges.R
import br.com.challenge.core.presentation.R as commonPresentation
import br.com.challenge.features.exchanges.domain.model.ExchangeResume
import br.com.challenge.features.exchanges.presentation.EXCHANGE_LIST
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeListScreen(
    navController: NavHostController,
    apiConfig: ApiConfig,
    viewModel: ExchangeViewModel = koinViewModel(
        parameters = { parametersOf(apiConfig) }
    )
) {
    val pagedItems = viewModel.items.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.testTag(EXCHANGE_LIST),
                        text = stringResource(R.string.exchange_list_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.retry() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(commonPresentation.string.button_refresh)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        when (val state = viewModel.uiState.value) {
            is UiState.Loading -> {
                LoadingComponent(
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is UiState.Success -> {
                ListExchanges(
                    navController,
                    paddingValues,
                    pagedItems,
                )
            }

            is UiState.Error -> {
                ErrorComponent(
                    message = if (state.exception is NoConnectivityException) {
                        stringResource(commonPresentation.string.error_connection_instruction)
                    } else {
                        state.exception.message ?: stringResource(commonPresentation.string.error_unknown)
                    },
                    onRetry = { viewModel.retry() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun ListExchanges(
    navController: NavHostController,
    paddingValues: PaddingValues,
    pagedItems: LazyPagingItems<ExchangeResume>,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(count = pagedItems.itemCount) { index ->
            val item = pagedItems[index]
            item?.let { exchange ->
                ExchangeItem(
                    exchange = exchange,
                    onClick = {
                        navController.navigate("exchange_detail/${exchange.exchangeId}")
                    }
                )
            }
        }

        when (pagedItems.loadState.append) {
            is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            is LoadState.Error -> {
                val error = pagedItems.loadState.append as LoadState.Error
                item {
                    Text(
                        text = "${stringResource(commonPresentation.string.error_loading_more_message)}${error.error.message}",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> Unit
        }

        when (pagedItems.loadState.refresh) {
            is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            is LoadState.Error -> {
                val error = pagedItems.loadState.refresh as LoadState.Error
                item {
                    Text(
                        text = "${stringResource(commonPresentation.string.error_loading_initial_message)}${error.error.message}",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> Unit
        }
    }
}
