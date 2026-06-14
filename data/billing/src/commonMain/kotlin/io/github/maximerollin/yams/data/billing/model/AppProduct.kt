package io.github.maximerollin.yams.data.billing.model

public expect class AppPackage {
    public val product: AppProduct
}

public data class AppProduct(
    val id: String,
    val title: String,
    val price: AppPrice,
)

public data class AppPrice(
    val formattedPrice: String,
    val amountMicros: Long,
    val currencyCode: String,
)
