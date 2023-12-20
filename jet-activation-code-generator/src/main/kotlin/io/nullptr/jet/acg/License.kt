package io.nullptr.jet.acg

import kotlinx.serialization.Serializable

@Serializable
data class License(
    val licenseId: String,
    val assigneeEmail: String,
    val assigneeName: String,
    val autoProlongated: Boolean,
    val checkConcurrentUse: Boolean,
    val gracePeriodDays: Int,
    val hash: String,
    val isAutoProlongated: Boolean,
    val licenseRestriction: String,
    val licenseeName: String,
    val metadata: String,
    val products: List<Product>
) {

    @Serializable
    data class Product(
        val code: String,
        val extended: Boolean,
        val fallbackDate: String,
        val paidUpTo: String
    )
}


