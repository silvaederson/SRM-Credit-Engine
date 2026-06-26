-- Tabela de moedas
CREATE TABLE IF NOT EXISTS currencies (
    code CHAR(3) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

-- Tabela de taxas de câmbio
CREATE TABLE IF NOT EXISTS exchange_rates (
    id BIGSERIAL PRIMARY KEY,
    from_currency CHAR(3) NOT NULL REFERENCES currencies(code),
    to_currency CHAR(3) NOT NULL REFERENCES currencies(code),
    rate DECIMAL(19,6) NOT NULL,
    effective_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (from_currency, to_currency, effective_date)
);

-- Tabela de tipos de recebíveis (sem a coluna category para compatibilidade H2)
CREATE TABLE IF NOT EXISTS receivable_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    spread DECIMAL(5,4) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

-- Tabela de cedentes
CREATE TABLE IF NOT EXISTS creditors (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    document VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Tabela de transações
CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    creditor_id BIGINT NOT NULL REFERENCES creditors(id),
    receivable_type_id BIGINT NOT NULL REFERENCES receivable_types(id),
    face_value DECIMAL(19,2) NOT NULL,
    present_value DECIMAL(19,2) NOT NULL,
    currency_code CHAR(3) NOT NULL REFERENCES currencies(code),
    due_date DATE NOT NULL,
    settlement_date DATE NOT NULL,
    base_rate DECIMAL(5,4) NOT NULL,
    applied_spread DECIMAL(5,4) NOT NULL,
    exchange_rate_used DECIMAL(19,6),
    payment_currency CHAR(3),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    external_reference VARCHAR(100),
    notes VARCHAR(500)
);

-- Índices para performance
CREATE INDEX idx_transactions_creditor ON transactions(creditor_id);
CREATE INDEX idx_transactions_currency ON transactions(currency_code);
CREATE INDEX idx_transactions_settlement_date ON transactions(settlement_date);
CREATE INDEX idx_transactions_due_date ON transactions(due_date);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_transactions_external_reference ON transactions(external_reference);

-- Índice composto para relatórios
CREATE INDEX idx_transactions_report ON transactions(settlement_date, creditor_id, currency_code, status);