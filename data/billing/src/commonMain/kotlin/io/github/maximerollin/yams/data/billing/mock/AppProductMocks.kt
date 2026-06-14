package io.github.maximerollin.yams.data.billing.mock

import io.github.maximerollin.yams.data.billing.model.AppPrice
import io.github.maximerollin.yams.data.billing.model.AppProduct

public object AppProductMocks {
    public val yamsPlus: AppProduct = AppProduct(
        id = "yams_plus",
        title = "Yams+",
        price = AppPrice(
            formattedPrice = "3,99 €",
            amountMicros = 3_990_000,
            currencyCode = "EUR",
        ),
    )
}
