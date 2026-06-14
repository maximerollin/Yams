package io.github.maximerollin.yams.feature.paywall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.maximerollin.yams.core.designsystem.preview.YamsStoreScreenshotPreviews
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.data.billing.mock.AppProductMocks
import io.github.maximerollin.yams.data.billing.model.AppProduct
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun PaywallRoute(
    fromScreen: String,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    viewModel: PaywallViewModel = koinViewModel { parametersOf(fromScreen) },
) {
    val productResult by viewModel.yamsPlusResult.collectAsStateWithLifecycle()
    val isSubscribed by viewModel.isYamsPlusActive.collectAsStateWithLifecycle()
    val purchaseLoading by viewModel.purchaseLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    LaunchedEffect(isSubscribed) {
        if (isSubscribed == true) {
            onNavigateHome()
        }
    }

    PaywallScreen(
        product = productResult?.getOrNull()?.product,
        purchaseLoading = purchaseLoading,
        onNavigateBack = onNavigateBack,
        onPurchaseClick = viewModel::purchaseYamsPlus,
        onRestorePurchases = viewModel::restorePurchases,
    )
}

@Composable
internal fun PaywallScreen(
    product: AppProduct?,
    purchaseLoading: Boolean,
    onNavigateBack: () -> Unit,
    onPurchaseClick: () -> Unit,
    onRestorePurchases: () -> Unit,
) {
    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onNavigateBack) {
                    Text("Fermer")
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Yams+",
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Débloquez toutes les fonctionnalités premium de Yams.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                PaywallFeatureRow("Historique illimité des parties")
                PaywallFeatureRow("Statistiques détaillées des joueurs")
                PaywallFeatureRow("Soutenez le développement de l'app")
            }

            Spacer(Modifier.height(32.dp))

            if (product != null) {
                Text(
                    text = product.price.formattedPrice,
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = "Paiement unique • sans abonnement",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
            }

            Button(
                onClick = onPurchaseClick,
                enabled = product != null && !purchaseLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (purchaseLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Débloquer Yams+")
                }
            }

            TextButton(
                onClick = onRestorePurchases,
                enabled = !purchaseLoading,
            ) {
                Text("Restaurer mes achats")
            }
        }
    }
}

@Composable
private fun PaywallFeatureRow(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("•  ", style = MaterialTheme.typography.bodyLarge)
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

@YamsStoreScreenshotPreviews
@Composable
private fun PaywallScreenPreview() {
    PaywallStoreScreenshotContent()
}

@Composable
public fun PaywallStoreScreenshotContent() {
    YamsTheme {
        PaywallScreen(
            product = AppProductMocks.yamsPlus,
            purchaseLoading = false,
            onNavigateBack = {},
            onPurchaseClick = {},
            onRestorePurchases = {},
        )
    }
}
