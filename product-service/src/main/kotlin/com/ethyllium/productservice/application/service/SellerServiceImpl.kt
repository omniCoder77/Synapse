// src/main/kotlin/com/ethyllium/productservice/application/service/SellerServiceImpl.kt
package com.ethyllium.productservice.application.service

import com.ethyllium.productservice.domain.model.Seller
import com.ethyllium.productservice.domain.port.driven.EventPublisher
import com.ethyllium.productservice.domain.port.driven.SellerRepository
import com.ethyllium.productservice.domain.port.driver.SellerService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class SellerServiceImpl(
    private val sellerRepository: SellerRepository,
    private val eventPublisher: EventPublisher
) : SellerService {
    override fun create(seller: Seller): Mono<Seller> {
        return sellerRepository.insert(seller).doOnSuccess { sel ->
            eventPublisher.publishSellerCreated(sel).subscribeOn(Schedulers.boundedElastic()).subscribe()
        }
    }

    override fun update(sellerId: String, businessName: String?, displayName: String?, phone: String?): Mono<Boolean> {
        return sellerRepository.update(sellerId, businessName, displayName, phone)
            .doOnSuccess { updated ->
                if (updated) {
                    eventPublisher.publishSellerUpdated(sellerId, businessName, displayName, phone).subscribeOn(Schedulers.boundedElastic()).subscribe()
                }
            }
    }

    override fun delete(sellerId: String): Mono<Boolean> {
        return sellerRepository.delete(sellerId).doOnSuccess { deleted ->
            if (deleted) {
                eventPublisher.publishSellerDeleted(sellerId).subscribeOn(Schedulers.boundedElastic()).subscribe()
            }
        }
    }
}