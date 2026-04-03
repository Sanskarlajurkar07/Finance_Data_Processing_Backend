-- Create financial_records table
CREATE TABLE financial_records (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(500) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL CHECK (amount >= 0),
    type VARCHAR(20) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    category VARCHAR(100) NOT NULL,
    record_date DATE NOT NULL,
    idempotency_key VARCHAR(100) UNIQUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by_user_id BIGINT NOT NULL,
    CONSTRAINT fk_created_by_user FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);

-- Create indexes for financial_records table
CREATE INDEX idx_financial_records_idempotency_key ON financial_records(idempotency_key);
CREATE INDEX idx_financial_records_record_date ON financial_records(record_date);
CREATE INDEX idx_financial_records_type ON financial_records(type);
CREATE INDEX idx_financial_records_category ON financial_records(category);
CREATE INDEX idx_financial_records_is_active ON financial_records(is_active);
CREATE INDEX idx_financial_records_created_by_user_id ON financial_records(created_by_user_id);

-- Add comment
COMMENT ON TABLE financial_records IS 'Financial transaction records with idempotency support';
COMMENT ON COLUMN financial_records.amount IS 'Amount with NUMERIC(19,4) precision for financial accuracy';
COMMENT ON COLUMN financial_records.idempotency_key IS 'Unique key for duplicate prevention';
