package com.synapse.paymentservice.infrastructure.adapter.outbound.razorpay

import com.razorpay.Order
import com.razorpay.RazorpayClient
import com.razorpay.RazorpayException
import com.synapse.paymentservice.infrastructure.adapter.outbound.razorpay.dto.response.RazorpayOrderResponse
import org.json.JSONObject
import org.springframework.stereotype.Component
import java.util.*

@Component
class RazorpayOrderOperations(private val razorpayClient: RazorpayClient) {

    fun createOrderPayment(
        amountInPaise: Double,
        currency: String = "INR",
        receipt: String?,
        paymentCapture: Int = 1,
        notes: Map<String, String>? = emptyMap()
    ): Result<RazorpayOrderResponse> {
        return try {
            if (amountInPaise <= 0) {
                return Result.failure(IllegalArgumentException("Amount must be positive"))
            }

            val order = JSONObject().apply {
                put("amount", amountInPaise.toInt())
                put("currency", currency)
                put("receipt", receipt)
                put("payment_capture", paymentCapture)
                if (!notes.isNullOrEmpty()) {
                    put("notes", JSONObject(notes))
                }
            }

            Result.success(razorpayClient.orders.create(order).toRazorpayOrderResponse())
        } catch (e: RazorpayException) {
            when {
                e.message?.contains("Currency is not supported") == true -> {
                    Result.failure(IllegalArgumentException("Unsupported currency: $currency", e))
                }

                e.message?.contains("Amount exceeds maximum amount allowed") == true -> {
                    Result.failure(IllegalArgumentException("Amount is too large", e))
                }

                e.message?.contains("not a valid id") == true -> {
                    Result.failure(IllegalArgumentException("Invalid order ID", e))
                }

                e.message?.contains("Order amount less than minimum amount allowed") == true -> {
                    Result.failure(IllegalArgumentException("Amount is too small", e))
                }

                else -> Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getOrder(orderId: String): Result<RazorpayOrderResponse> {
        return try {
            Result.success(razorpayClient.orders.fetch(orderId).toRazorpayOrderResponse())
        } catch (e: RazorpayException) {
            Result.failure(e)
        }
    }
}

fun Order.toRazorpayOrderResponse(): RazorpayOrderResponse {
    val notesObject = this.get("notes") as? JSONObject
    val notesMap = notesObject?.let { jo ->
        jo.keys().asSequence().associateWith { key -> jo.get(key).toString() }
    }
    val receiptValue = this.get<Any>("receipt")

    return RazorpayOrderResponse(
        id = this.get("id") as String,
        amount = (this.get("amount") as Number).toLong(),
        currency = this.get("currency") as String,
        receipt = if (receiptValue == JSONObject.NULL) null else receiptValue as? String,
        status = this.get("status") as String,
        attempts = (this.get("attempts") as Number).toInt(),
        created_at = this.get("created_at") as Date,
        notes = notesMap,
        amount_paid = (this.get("amount_paid") as Number).toLong(),
        amount_due = (this.get("amount_due") as Number).toLong()
    )
}