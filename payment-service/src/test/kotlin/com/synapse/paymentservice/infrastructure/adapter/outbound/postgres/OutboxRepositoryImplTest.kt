package com.synapse.paymentservice.infrastructure.adapter.outbound.postgres

import com.synapse.paymentservice.domain.port.driven.OutboxRepository
import com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.postgres.entity.Outbox
import com.synapse.paymentservice.infrastructure.adapter.inbound.kafka.utils.Topics
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier
import java.util.*

@SpringBootTest
class OutboxRepositoryImplTest {

    @Autowired
    lateinit var outboxRepository: OutboxRepository

    @Test
    fun save_Test() {
        val outbox = Outbox(aggregateType = Topics.ORDER_CREATED, aggregateId = UUID.randomUUID(), eventType = "OrderCreated", payload = "some payload")
        outboxRepository.save(outbox).`as`(StepVerifier::create).assertNext { actual ->
            assertThat(actual.id).isEqualTo(outbox.id)
        }.verifyComplete()
    }
}