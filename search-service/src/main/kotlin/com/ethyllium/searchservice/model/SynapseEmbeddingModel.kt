package com.ethyllium.searchservice.model

import org.springframework.ai.document.Document
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.embedding.EmbeddingRequest
import org.springframework.ai.embedding.EmbeddingResponse
import org.springframework.context.annotation.Configuration

@Configuration
class SynapseEmbeddingModel: EmbeddingModel {
    override fun call(request: EmbeddingRequest): EmbeddingResponse {
        return EmbeddingResponse(listOf())
    }

    override fun embed(document: Document): FloatArray {
        return FloatArray(0)
    }
}