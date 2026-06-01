package com.turkcell.data.repository

import com.turkcell.core.domain.purchase.CreatePurchaseItem
import com.turkcell.core.domain.purchase.Purchase
import com.turkcell.core.domain.purchase.PurchaseRepository
import com.turkcell.data.mapper.toCreatePurchaseRequestDto
import com.turkcell.data.mapper.toDomain
import com.turkcell.data.remote.PurchaseApi
import com.turkcell.data.util.runCatchingApi

class PurchaseRepositoryImpl(
    private val purchaseApi: PurchaseApi
) : PurchaseRepository {
    override suspend fun createPurchase(items: List<CreatePurchaseItem>): Result<Purchase> =
        runCatchingApi {
            purchaseApi.createPurchase(items.toCreatePurchaseRequestDto())
        }.map { it.toDomain() }

    override suspend fun pay(purchaseId: String): Result<Purchase> = runCatchingApi {
        purchaseApi.pay(purchaseId)
    }.map { it.toDomain() }

    override suspend fun getPurchase(id: String): Result<Purchase> = runCatchingApi {
        purchaseApi.getPurchase(id)
    }.map { it.toDomain() }
}
