\c payment_db;
CREATE TABLE outbox (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id UUID NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE payment (
    id UUID PRIMARY KEY,
    amount BIGINT NOT NULL,
    order_id VARCHAR(255) NOT NULL,
    refund_id VARCHAR(255),
    payment_id VARCHAR(255),
    product_order_id UUID NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    receipt TEXT,
    user_id VARCHAR(255) NOT NULL
);

CREATE TABLE refund (
    refund_id VARCHAR(255) PRIMARY KEY,
    payment_id VARCHAR(255) NOT NULL,
    amount BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
