package com.synapse.paymentservice.infrastructure.adapter.outbound.razorpay

import com.razorpay.RazorpayClient
import com.razorpay.RazorpayException
import com.razorpay.Refund
import com.synapse.paymentservice.infrastructure.adapter.outbound.razorpay.dto.request.FetchMultipleRefundsRequest
import com.synapse.paymentservice.infrastructure.adapter.outbound.razorpay.dto.request.RefundRequest
import com.synapse.paymentservice.infrastructure.adapter.outbound.razorpay.dto.response.AcquirerData
import com.synapse.paymentservice.infrastructure.adapter.outbound.razorpay.dto.response.RefundResponse
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class RazorpayRefundOperation(private val instance: RazorpayClient) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun refundOrderPayment(refundRequest: RefundRequest): Result<RefundResponse?> {
        return try {
            val refund = JSONObject().apply {
                put("speed", refundRequest.speed)
                put("amount", refundRequest.amountInPaise)
                refundRequest.notes?.let { put("notes", JSONObject(it)) }
                refundRequest.receipt?.let { put("receipt", it) }
            }

            val response = instance.payments.refund(refundRequest.paymentId, refund)
            Result.success(response.toRefundResponse())
        } catch (e: RazorpayException) {
            logger.error("Refund failed for payment ${refundRequest.paymentId}: ${e.message}", e)

            when {
                e.message!!.startsWith(
                    "BAD_REQUEST_ERROR:The refund amount provided is greater than amount captured", false
                ) -> Result.failure(IllegalArgumentException("Refund amount exceeds captured amount"))

                e.message!!.startsWith(
                    "Status Code: 404", false
                ) -> Result.success(null)

                else -> Result.failure(e)
            }
        } catch (e: Exception) {
            logger.error("Unexpected error during refund for payment ${refundRequest.paymentId}", e)
            Result.failure(RuntimeException("Unexpected error processing refund"))
        }
    }

    fun getRefunds(
        multipleRefundsRequest: FetchMultipleRefundsRequest,
        paymentId: String
    ): Result<List<RefundResponse>> {
        return try {
            val params = JSONObject()
            params.put("count", multipleRefundsRequest.count)
            params.put("status", multipleRefundsRequest.status)
            params.put("receipt", multipleRefundsRequest.receipt)

            val refunds: MutableList<Refund> = instance.payments.fetchAllRefunds(paymentId, params)
            Result.success(refunds.map { it.toRefundResponse() })
        } catch (e: RazorpayException) {
            when {
                e.message!!.startsWith(
                    "Status Code: 404", false
                ) -> Result.success(listOf<RefundResponse>())

                else -> Result.failure(e)
            }
        }
    }

    fun getRefund(paymentId: String, refundId: String): RefundResponse? {
        return try {
            val refund = instance.payments.fetchRefund(paymentId, refundId)
            refund.toRefundResponse()
        } catch (e: RazorpayException) {
            return null
        }
    }
}

fun Refund.toRefundResponse(): RefundResponse {
    val notesObject = this.get("notes") as? JSONObject
    val notesMap = notesObject?.let { jo ->
        jo.keys().asSequence().associateWith { key -> jo.get(key).toString() }
    }

    val acquirerDataObject = this.get("acquirer_data") as? JSONObject
    val acquirerData = acquirerDataObject?.let {
        val arn = if (it.has("arn") && !it.isNull("arn")) it.getString("arn") else null
        AcquirerData(arn = arn)
    }

    val receiptValue = this.get<Any>("receipt")

    return RefundResponse(
        id = this.get("id") as String,
        paymentId = this.get("payment_id") as String,
        amount = (this.get("amount") as Number).toLong(),
        currency = this.get("currency") as String,
        notes = notesMap,
        receipt = if (receiptValue == JSONObject.NULL) null else receiptValue as? String,
        status = this.get("status") as String,
        speedProcessed = this.get("speed_processed") as String,
        speedRequested = this.get("speed_requested") as String,
        acquirerData = acquirerData,
        createdAt = this.get("created_at")
    )
}