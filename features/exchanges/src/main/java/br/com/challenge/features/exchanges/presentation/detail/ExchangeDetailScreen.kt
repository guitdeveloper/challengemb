package br.com.challenge.features.exchanges.presentation.detail

import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.core.net.toUri
import br.com.challenge.core.common.CurrencyUtils.formatCurrencyAbbreviated
import br.com.challenge.core.common.DateUtils.formatIsoDate
import br.com.challenge.core.network.di.ApiConfig
import br.com.challenge.core.presentation.component.ErrorComponent
import br.com.challenge.core.presentation.component.LoadingComponent
import br.com.challenge.core.presentation.state.UiState
import br.com.challenge.features.exchanges.domain.Exchange
import br.com.challenge.features.exchanges.presentation.list.ExchangeViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeDetailScreen(
    exchangeId: String,
    onBackClick: () -> Unit,
    apiConfig: ApiConfig,
    viewModel: ExchangeDetailViewModel = koinViewModel(
        parameters = { parametersOf(apiConfig) }
    )
) {
    LaunchedEffect(exchangeId) {
        viewModel.loadExchangeDetail(exchangeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalhes da Exchange",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
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
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is UiState.Error -> {
                ErrorComponent(
                    message = state.exception.message ?: "Erro desconhecido",
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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Header(exchange)
        VolumeInformation(exchange)
        DataInformation(exchange)
    }
}

@Composable
private fun Header(exchange: Exchange) {
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
                text = exchange.name ?: "Nome não disponível",
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
                        openChromeCustomTab(context, url, Color.LightGray)
                    }
                )
            }
        }
    }
}

fun openChromeCustomTab(
    context: android.content.Context,
    url: String,
    toolbarColor: Color
) {
    val builder = CustomTabsIntent.Builder().apply {
        setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder().setToolbarColor(toolbarColor.toArgb()).build())
        setStartAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(context, url.toUri())
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
                text = "Informações de Volume",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            DetailRow(
                label = "Volume 24h (USD)",
                value = formatCurrencyAbbreviated(exchange.volume1dayUsd)
            )

            DetailRow(
                label = "Volume 1h (USD)",
                value = formatCurrencyAbbreviated(exchange.volume1hrsUsd)
            )

            DetailRow(
                label = "Volume 30d (USD)",
                value = formatCurrencyAbbreviated(exchange.volume1mthUsd)
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
                text = "Informações de Dados",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            DetailRow(
                label = "Símbolos Disponíveis",
                value = exchange.dataSymbolsCount?.toString() ?: "N/A"
            )

            exchange.dataQuoteStart?.let {
                DetailRow(
                    label = "Início dos Dados",
                    value = formatIsoDate(it)
                )
            }

            exchange.dataQuoteEnd?.let {
                DetailRow(
                    label = "Fim dos Dados",
                    value = formatIsoDate(it)
                )
            }

            exchange.dataTradeStart?.let {
                DetailRow(
                    label = "Início Trading",
                    value = formatIsoDate(it)
                )
            }

            exchange.dataTradeEnd?.let {
                DetailRow(
                    label = "Fim Trading",
                    value = formatIsoDate(it)
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
