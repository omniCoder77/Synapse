package com.synapse.paymentservice.infrastructure.adapter.inbound.web

import com.synapse.paymentservice.domain.port.driver.PaymentService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController()
@RequestMapping("/api/v1/payments")
class PaymentController(private val paymentService: PaymentService) {

    @PostMapping("/webhook", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun payment(
        @RequestBody payload: String,
        @RequestHeader("X-Razorpay-Signature") signature: String,
        @RequestHeader("X-Razorpay-Event-Id") idempotencyKey: String,
    ): Mono<ResponseEntity<String>> {
        if (payload.isBlank()) {
            return Mono.just(
                ResponseEntity.badRequest().body("Empty payload received")
            )
        }
        return paymentService.webhook(payload, signature, idempotencyKey).map {
            if (it) {
                ResponseEntity.ok("Payment processed successfully")
            } else {
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment processing failed")
            }
        }
    }

    @GetMapping("/payment-order/{productOrderId}", produces = [MediaType.TEXT_HTML_VALUE])
    fun getOrderId(@PathVariable productOrderId: String, authentication: Authentication): Mono<ResponseEntity<String>> {
        return paymentService.getOrderId(productOrderId, authentication.name).map { ResponseEntity.ok(it) }
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
    }
}