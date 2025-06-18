package br.com.challenge.features.exchanges.presentation.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import br.com.challenge.core.common.CurrencyUtils.formatCurrencyAbbreviated
import br.com.challenge.core.common.DateUtils.formatIsoDate
import br.com.challenge.core.network.ApiConfig
import br.com.challenge.core.presentation.component.DetailRow
import br.com.challenge.core.presentation.component.ErrorComponent
import br.com.challenge.core.presentation.component.LoadingComponent
import br.com.challenge.core.presentation.state.UiState
import br.com.challenge.core.presentation.utils.ChromeCustomTabNavigator
import br.com.challenge.features.exchanges.R
import br.com.challenge.core.presentation.R as commonPresentation
import br.com.challenge.features.exchanges.domain.model.Exchange
import br.com.challenge.features.exchanges.presentation.EXCHANGE_DETAIL
import org.koin.compose.getKoin
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeDetailScreen(
    exchangeId: String,
    onBackClick: () -> Unit,
    apiConfig: ApiConfig,
    viewModel: ExchangeDetailViewModel = koinViewModel(
        parameters = { parametersOf(apiConfig) }
    ),
) {
    LaunchedEffect(exchangeId) {
        viewModel.loadExchangeDetail(exchangeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.exchange_details_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(commonPresentation.string.button_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
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
                ExchangeDetailContent(
                    exchange = state.data,
                    modifier = Modifier.padding(paddingValues),
                )
            }

            is UiState.Error -> {
                ErrorComponent(
                    message = state.exception.message ?: stringResource(commonPresentation.string.error_unknown),
                    onRetry = { viewModel.retry(exchangeId) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun ExchangeDetailContent(
    exchange: Exchange,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag(EXCHANGE_DETAIL),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Header(exchange)
        VolumeInformation(exchange)
        DataInformation(exchange)
    }
}

@Composable
fun Header(
    exchange: Exchange,
    webNavigator: ChromeCustomTabNavigator = getKoin().get()
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = exchange.name ?: stringResource(R.string.exchange_name_unknown),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ID: ${exchange.exchangeId}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            exchange.websiteUrl?.let { url ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = url,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        webNavigator.openChromeCustomTab(context, url, Color.LightGray)
                    }
                )
            }
        }
    }
}

@Composable
fun VolumeInformation(exchange: Exchange) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.exchange_details_group_data_volume),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            DetailRow(
                label = stringResource(R.string.exchange_details_volume_24h),
                value = formatCurrencyAbbreviated(exchange.volume1dayUsd) ?: stringResource(commonPresentation.string.not_applicable_abbreviation)
            )

            DetailRow(
                label = stringResource(R.string.exchange_details_volume_1h),
                value = formatCurrencyAbbreviated(exchange.volume1hrsUsd) ?: stringResource(commonPresentation.string.not_applicable_abbreviation)
            )

            DetailRow(
                label = stringResource(R.string.exchange_details_volume_30d),
                value = formatCurrencyAbbreviated(exchange.volume1mthUsd) ?: stringResource(commonPresentation.string.not_applicable_abbreviation)
            )
        }
    }
}

@Composable
fun DataInformation(exchange: Exchange) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.exchange_details_group_data),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            DetailRow(
                label = stringResource(R.string.exchange_details_symbols),
                value = exchange.dataSymbolsCount?.toString() ?: stringResource(commonPresentation.string.not_applicable_abbreviation)
            )

            exchange.dataQuoteStart?.let {
                DetailRow(
                    label = stringResource(R.string.exchange_details_data_start),
                    value = formatIsoDate(it) ?: stringResource(commonPresentation.string.validation_invalid_date)
                )
            }

            exchange.dataQuoteEnd?.let {
                DetailRow(
                    label = stringResource(R.string.exchange_details_data_end),
                    value = formatIsoDate(it) ?: stringResource(commonPresentation.string.not_applicable_abbreviation)
                )
            }

            exchange.dataTradeStart?.let {
                DetailRow(
                    label = stringResource(R.string.exchange_details_trading_start),
                    value = formatIsoDate(it) ?: stringResource(commonPresentation.string.validation_invalid_date)
                )
            }

            exchange.dataTradeEnd?.let {
                DetailRow(
                    label = stringResource(R.string.exchange_details_trading_end),
                    value = formatIsoDate(it) ?: stringResource(commonPresentation.string.validation_invalid_date)
                )
            }
        }
    }
}
