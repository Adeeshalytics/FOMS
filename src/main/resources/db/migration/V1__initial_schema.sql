CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE instruments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    symbol VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    asset_type VARCHAR(32) NOT NULL,
    currency CHAR(3) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    tick_size NUMERIC(20, 8),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_instrument_tick_size CHECK (tick_size IS NULL OR tick_size > 0)
);

CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id VARCHAR(100) NOT NULL,
    client_order_id VARCHAR(100) NOT NULL,
    instrument_id UUID NOT NULL REFERENCES instruments(id),
    side VARCHAR(4) NOT NULL,
    order_type VARCHAR(8) NOT NULL,
    original_quantity BIGINT NOT NULL,
    filled_quantity BIGINT NOT NULL DEFAULT 0,
    limit_price NUMERIC(20, 8),
    average_execution_price NUMERIC(20, 8),
    status VARCHAR(32) NOT NULL,
    accepted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_orders_client_order UNIQUE (client_id, client_order_id),
    CONSTRAINT chk_orders_side CHECK (side IN ('BUY', 'SELL')),
    CONSTRAINT chk_orders_type CHECK (order_type IN ('LIMIT', 'MARKET')),
    CONSTRAINT chk_orders_quantity CHECK (original_quantity > 0 AND filled_quantity >= 0 AND filled_quantity <= original_quantity),
    CONSTRAINT chk_orders_status CHECK (status IN ('NEW', 'ACCEPTED', 'PARTIALLY_FILLED', 'FILLED', 'CANCELLED', 'REJECTED', 'EXPIRED')),
    CONSTRAINT chk_orders_limit_price CHECK ((order_type = 'LIMIT' AND limit_price IS NOT NULL AND limit_price > 0) OR (order_type = 'MARKET' AND limit_price IS NULL))
);

CREATE INDEX idx_orders_instrument_status_time ON orders (instrument_id, status, accepted_at, created_at);
CREATE INDEX idx_orders_client_order ON orders (client_id, client_order_id);

CREATE TABLE executions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    buy_order_id UUID NOT NULL REFERENCES orders(id),
    sell_order_id UUID NOT NULL REFERENCES orders(id),
    instrument_id UUID NOT NULL REFERENCES instruments(id),
    quantity BIGINT NOT NULL,
    execution_price NUMERIC(20, 8) NOT NULL,
    executed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_executions_distinct_orders CHECK (buy_order_id <> sell_order_id),
    CONSTRAINT chk_executions_quantity CHECK (quantity > 0),
    CONSTRAINT chk_executions_price CHECK (execution_price > 0)
);

CREATE INDEX idx_executions_buy_order ON executions (buy_order_id, executed_at);
CREATE INDEX idx_executions_sell_order ON executions (sell_order_id, executed_at);
CREATE INDEX idx_executions_instrument_time ON executions (instrument_id, executed_at);

CREATE TABLE audit_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID REFERENCES orders(id),
    event_type VARCHAR(32) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    details JSONB NOT NULL DEFAULT '{}'::jsonb
);

CREATE INDEX idx_audit_events_order_time ON audit_events (order_id, occurred_at);
