package io.github.maximerollin.yams.feature.paywall

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.maximerollin.yams.core.designsystem.component.AppIconButton
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimaryButton
import io.github.maximerollin.yams.core.designsystem.component.YamsTextButton
import io.github.maximerollin.yams.core.designsystem.icon.AwardStar
import io.github.maximerollin.yams.core.designsystem.icon.Close
import io.github.maximerollin.yams.core.designsystem.icon.Crown
import io.github.maximerollin.yams.core.designsystem.icon.HeartSmile
import io.github.maximerollin.yams.core.designsystem.icon.History
import io.github.maximerollin.yams.core.designsystem.icon.PencilSparkles
import io.github.maximerollin.yams.core.designsystem.icon.Strategy
import io.github.maximerollin.yams.core.designsystem.icon.Timeline
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.preview.YamsStoreScreenshotPreviews
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.designsystem.util.IconInfo
import io.github.maximerollin.yams.data.billing.mock.AppProductMocks
import io.github.maximerollin.yams.data.billing.model.AppProduct
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import yams.feature.paywall.generated.resources.Res
import yams.feature.paywall.generated.resources.paywall_assistant_preview
import yams.feature.paywall.generated.resources.paywall_history_preview
import yams.feature.paywall.generated.resources.paywall_progress_preview
import yams.feature.paywall.generated.resources.paywall_stats_preview

@Composable
internal fun PaywallRoute(
    fromScreen: String,
    onNavigateBack: () -> Unit,
    onPurchaseSuccess: () -> Unit,
    viewModel: PaywallViewModel = koinViewModel { parametersOf(fromScreen) },
) {
    val productResult by viewModel.yamsPlusResult.collectAsStateWithLifecycle()
    val purchaseLoading by viewModel.purchaseLoading.collectAsStateWithLifecycle()
    val purchaseSucceeded by viewModel.purchaseSucceeded.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    LaunchedEffect(purchaseSucceeded) {
        if (purchaseSucceeded) {
            onPurchaseSuccess()
            viewModel.onPurchaseSucceededHandled()
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
    Scaffold(
        bottomBar = {
            PaywallBottomBar(
                product = product,
                purchaseLoading = purchaseLoading,
                onPurchaseClick = onPurchaseClick,
                onRestorePurchases = onRestorePurchases,
            )
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 16.dp,
                end = 20.dp,
                bottom = 20.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                PaywallHero(onClose = onNavigateBack)
            }

            item {
                PaywallFeatureShowcase()
            }

            item {
                SoloDevSupportCard()
            }

        }
    }
}

@Composable
private fun PaywallHero(
    onClose: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
    ) {
        Box {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Surface(
                    modifier = Modifier.size(68.dp),
                    shape = CircleShape,
                    color = YamsTheme.colors.gold,
                    contentColor = YamsTheme.colors.onGold,
                    tonalElevation = 2.dp,
                    shadowElevation = 4.dp,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = YamsIcons.Crown,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Yams+",
                        style = MaterialTheme.typography.headlineLarge,
                        color = YamsTheme.colors.brown,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "Passez à la version complète et gardez une vraie mémoire de vos soirées Yams.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            AppIconButton(
                icon = YamsIcons.Close,
                contentDescription = "Fermer",
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun PaywallFeatureShowcase() {
    val pagerState = rememberPagerState(pageCount = { 4 })
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Ce que vous débloquez",
            style = MaterialTheme.typography.titleMedium,
            color = YamsTheme.colors.brown,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Faites glisser les aperçus pour découvrir Yams+.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(388.dp),
        ) {
            val stackColors = listOf(
                MaterialTheme.colorScheme.tertiaryContainer,
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.primaryContainer,
            )
            val stackRotations = listOf(-7f, 6f, -2f)
            stackColors.forEachIndexed { index, color ->
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp)
                        .height(362.dp)
                        .graphicsLayer { rotationZ = stackRotations[index] },
                    shape = RoundedCornerShape(12.dp),
                    color = color,
                    shadowElevation = 3.dp,
                ) {}
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 10.dp),
                pageSpacing = 16.dp,
            ) { page ->
                when (page) {
                    0 -> PaywallFeatureCarouselCard(
                        icon = YamsIcons.PencilSparkles,
                        title = "Assistant caméra IA",
                        description = "Scannez vos 5 dés et obtenez un conseil sur les dés à garder, à relancer ou la case à remplir.",
                        screenshot = Res.drawable.paywall_assistant_preview,
                        screenshotAlignment = BiasAlignment(0f, -0.62f),
                    )

                    1 -> PaywallFeatureCarouselCard(
                        icon = YamsIcons.History,
                        title = "Historique complet",
                        description = "Toutes vos anciennes parties restent consultables, au lieu des 5 dernières.",
                        screenshot = Res.drawable.paywall_history_preview,
                        screenshotAlignment = Alignment.TopCenter,
                    )

                    2 -> PaywallFeatureCarouselCard(
                        icon = YamsIcons.Timeline,
                        title = "Graphe de progression",
                        description = "Visualisez l'évolution des points par tour sur vos dernières parties.",
                        screenshot = Res.drawable.paywall_progress_preview,
                        screenshotAlignment = Alignment.TopCenter,
                    )

                    else -> PaywallFeatureCarouselCard(
                        icon = YamsIcons.Strategy,
                        title = "Stats avancées",
                        description = "Score moyen, record, rang moyen, Yams par partie, podiums et séries.",
                        screenshot = Res.drawable.paywall_stats_preview,
                        screenshotAlignment = Alignment.TopCenter,
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(4) { page ->
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (pagerState.currentPage == page) 10.dp else 7.dp),
                    shape = CircleShape,
                    color = if (pagerState.currentPage == page) {
                        YamsTheme.colors.gold
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    },
                ) {}
            }
        }
    }
}

@Composable
private fun PaywallFeatureCarouselCard(
    icon: ImageVector,
    title: String,
    description: String,
    screenshot: DrawableResource,
    screenshotAlignment: Alignment,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 10.dp, horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 5.dp,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(214.dp),
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Image(
                        painter = painterResource(screenshot),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alignment = screenshotAlignment,
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = YamsTheme.colors.gold,
                )
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    color = YamsTheme.colors.brown,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SoloDevSupportCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = YamsTheme.colors.brown,
                contentColor = YamsTheme.colors.onBrown,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = YamsIcons.HeartSmile,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Soutenir un solo dev",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Votre achat finance les prochaines améliorations, sans pub ni abonnement.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.76f),
                )
            }
        }
    }
}

@Composable
private fun PaywallBottomBar(
    product: AppProduct?,
    purchaseLoading: Boolean,
    onPurchaseClick: () -> Unit,
    onRestorePurchases: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = product?.price?.formattedPrice ?: "Prix indisponible",
                style = MaterialTheme.typography.titleMedium,
                color = YamsTheme.colors.brown,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Text(
                text = "Paiement unique. Yams+ reste à vous.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            YamsPrimaryButton(
                onClick = onPurchaseClick,
                enabled = product != null,
                loading = purchaseLoading,
                text = "Débloquer Yams+",
                icon = IconInfo(
                    vector = YamsIcons.AwardStar,
                    contentDescription = "Débloquer Yams+",
                ),
            )

            YamsTextButton(
                onClick = onRestorePurchases,
                enabled = !purchaseLoading,
            ) {
                Text("Restaurer mes achats")
            }
        }
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
