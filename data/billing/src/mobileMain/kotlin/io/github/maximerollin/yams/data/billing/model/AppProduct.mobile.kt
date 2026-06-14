package io.github.maximerollin.yams.data.billing.model

import com.revenuecat.purchases.kmp.models.Package

public actual data class AppPackage(
    internal val revenueCatPackage: Package,
) {
    public actual val product: AppProduct = AppProduct(
        id = revenueCatPackage.storeProduct.id,
        title = revenueCatPackage.storeProduct.title,
        price = AppPrice(
            formattedPrice = revenueCatPackage.storeProduct.price.formatted,
            amountMicros = revenueCatPackage.storeProduct.price.amountMicros,
            currencyCode = revenueCatPackage.storeProduct.price.currencyCode,
        ),
    )
}
