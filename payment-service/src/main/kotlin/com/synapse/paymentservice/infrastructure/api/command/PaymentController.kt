package com.synapse.paymentservice.infrastructure.api.command

import com.synapse.paymentservice.application.dto.request.OrderRequest
import com.synapse.paymentservice.application.dto.response.OrderResponse
import com.synapse.paymentservice.application.service.OrderService
import com.synapse.paymentservice.domain.port.incoming.WebhookHandler
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/payment")
class PaymentController(
    private val orderService: OrderService, private val webhookHandler: WebhookHandler
) {
    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun payment(@RequestBody productOrderRequest: OrderRequest): ResponseEntity<OrderResponse> {
        val order = orderService.createOrder(productOrderRequest)
        return ResponseEntity(order, HttpStatus.CREATED)
    }

    @PostMapping("/successful-payment")
    fun updateOrder(responsePayload: Map<String, String>): ResponseEntity<String> {
        val orderId = responsePayload["razorpay_order_id"]!!
        orderService.paid(orderId)
        return ResponseEntity.ok("success")
    }


    @PostMapping("/webhook")
    fun handleWebhook(
        @RequestHeader("X-Razorpay-Signature") signature: String, @RequestBody payload: String
    ): ResponseEntity<String> {
        val result = webhookHandler.handleWebhook(payload, signature)
        return ResponseEntity.status(result.statusCode).body(result.message)
    }
}