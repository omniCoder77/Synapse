package com.ethyllium.searchservice.infrastructure.adapter.outbound.kafka

import com.ethyllium.searchservice.domain.model.Product
import com.ethyllium.searchservice.infrastructure.adapter.outbound.elasticsearch.entity.SearchProduct
import com.ethyllium.searchservice.infrastructure.adapter.outbound.elasticsearch.entity.toSearchDocument
import com.ethyllium.searchservice.infrastructure.adapter.outbound.kafka.event.*
import com.ethyllium.searchservice.infrastructure.adapter.outbound.kafka.util.Topics
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.document.Document
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.UpdateQuery
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers

