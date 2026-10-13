-- ============================================================================
-- Enterprise AML & Financial Crime Compliance Platform
-- Combined Initial Schema: Tables, Constraints, Indexes & Updates
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 1. Identity, Organization & Branches
CREATE TABLE organizations (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    country_code VARCHAR(3) NOT NULL,
    license_number VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE branches (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country_code VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE departments (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    branch_id UUID NOT NULL REFERENCES branches(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_dept_branch_code UNIQUE (branch_id, code)
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    branch_id UUID REFERENCES branches(id) ON DELETE SET NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    failed_attempts INT NOT NULL DEFAULT 0,
    lockout_until TIMESTAMPTZ,
    mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    mfa_secret VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    is_system BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    code VARCHAR(100) NOT NULL UNIQUE,
    module VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE role_permissions (
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE approval_requests (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    maker_user_id UUID NOT NULL REFERENCES users(id),
    checker_user_id UUID REFERENCES users(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    maker_comments TEXT,
    checker_comments TEXT,
    payload_snapshot JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMPTZ
);

-- 2. Customer 360, KYC & Due Diligence (รวม full_name_myanmar จาก V5)
CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    branch_id UUID NOT NULL REFERENCES branches(id),
    customer_type VARCHAR(20) NOT NULL,
    customer_number VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    full_name_myanmar VARCHAR(255),
    trade_name VARCHAR(255),
    jurisdiction_country VARCHAR(3) NOT NULL,
    date_of_birth_or_incorp DATE NOT NULL,
    tax_identifier VARCHAR(100),
    risk_rating VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    kyc_status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
    account_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    onboarding_date DATE NOT NULL,
    last_kyc_review_date DATE,
    next_kyc_review_date DATE NOT NULL,
    source_of_wealth TEXT,
    source_of_funds TEXT,
    occupation_or_industry VARCHAR(255),
    is_pep BOOLEAN NOT NULL DEFAULT FALSE,
    sanctions_hit BOOLEAN NOT NULL DEFAULT FALSE,
    adverse_media_hit BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customer_identifiers (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    id_type VARCHAR(50) NOT NULL,
    id_number VARCHAR(100) NOT NULL,
    issuing_country VARCHAR(3) NOT NULL,
    issue_date DATE,
    expiry_date DATE,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customer_addresses (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    address_type VARCHAR(50) NOT NULL,
    street_line1 VARCHAR(255) NOT NULL,
    street_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state_province VARCHAR(100),
    postal_code VARCHAR(50),
    country_code VARCHAR(3) NOT NULL,
    is_current BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE beneficial_owners (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    entity_customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    owner_customer_id UUID REFERENCES customers(id) ON DELETE SET NULL,
    owner_name VARCHAR(255) NOT NULL,
    nationality VARCHAR(3),
    ownership_percentage NUMERIC(5,2) NOT NULL,
    control_type VARCHAR(50) NOT NULL,
    is_ubo BOOLEAN NOT NULL DEFAULT FALSE,
    verification_status VARCHAR(20) NOT NULL DEFAULT 'VERIFIED',
    effective_from DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customer_relationships (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    from_customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    to_customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    relationship_type VARCHAR(50) NOT NULL,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE kyc_reviews (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    review_type VARCHAR(30) NOT NULL,
    due_diligence_level VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    reviewer_id UUID REFERENCES users(id),
    approver_id UUID REFERENCES users(id),
    risk_score NUMERIC(5,2),
    findings TEXT,
    decision_notes TEXT,
    review_date DATE NOT NULL,
    next_review_date DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE document_metadata (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    storage_path VARCHAR(500) NOT NULL,
    hash_sha256 VARCHAR(64) NOT NULL,
    uploaded_by UUID REFERENCES users(id),
    verification_status VARCHAR(20) NOT NULL DEFAULT 'VALIDATED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Accounts & Financial Transactions
CREATE TABLE accounts (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    branch_id UUID NOT NULL REFERENCES branches(id),
    account_number VARCHAR(50) NOT NULL UNIQUE,
    account_type VARCHAR(50) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    balance NUMERIC(18,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    opened_at DATE NOT NULL,
    last_activity_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    reference_number VARCHAR(100) NOT NULL UNIQUE,
    account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    transaction_type VARCHAR(50) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    amount NUMERIC(18,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    base_amount_usd NUMERIC(18,2) NOT NULL,
    counterparty_name VARCHAR(255) NOT NULL,
    counterparty_account VARCHAR(100),
    counterparty_bank VARCHAR(255),
    counterparty_country VARCHAR(3) NOT NULL,
    booking_date TIMESTAMPTZ NOT NULL,
    value_date TIMESTAMPTZ NOT NULL,
    purpose_code VARCHAR(50),
    narration TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'SETTLED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. Screening, Watchlists & Hits
CREATE TABLE watchlist_sources (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    list_type VARCHAR(50) NOT NULL,
    version VARCHAR(50) NOT NULL,
    provider VARCHAR(100) NOT NULL,
    item_count INT NOT NULL DEFAULT 0,
    last_updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE watchlist_entries (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    source_id UUID NOT NULL REFERENCES watchlist_sources(id) ON DELETE CASCADE,
    entry_uid VARCHAR(100) NOT NULL,
    entry_type VARCHAR(20) NOT NULL,
    primary_name VARCHAR(255) NOT NULL,
    aliases TEXT,
    pep_category VARCHAR(50),
    country_codes VARCHAR(100),
    date_of_birth VARCHAR(50),
    designator_programs VARCHAR(255),
    remarks TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_source_entry UNIQUE (source_id, entry_uid)
);

CREATE TABLE screening_runs (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    run_type VARCHAR(30) NOT NULL,
    total_candidates INT NOT NULL DEFAULT 0,
    matches_found INT NOT NULL DEFAULT 0,
    run_by_user_id UUID REFERENCES users(id),
    completed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE screening_matches (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    screening_run_id UUID REFERENCES screening_runs(id) ON DELETE SET NULL,
    customer_id UUID REFERENCES customers(id) ON DELETE CASCADE,
    transaction_id UUID REFERENCES transactions(id) ON DELETE SET NULL,
    watchlist_entry_id UUID NOT NULL REFERENCES watchlist_entries(id) ON DELETE CASCADE,
    match_type VARCHAR(30) NOT NULL,
    match_score NUMERIC(5,2) NOT NULL,
    matching_algorithm VARCHAR(50) NOT NULL,
    matched_term VARCHAR(255) NOT NULL,
    match_details_json JSONB,
    disposition VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    disposition_by_user_id UUID REFERENCES users(id),
    disposition_notes TEXT,
    disposition_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE adverse_media_records (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    headline VARCHAR(500) NOT NULL,
    source_name VARCHAR(255) NOT NULL,
    article_url VARCHAR(1000),
    publication_date DATE,
    risk_category VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    match_score NUMERIC(5,2) NOT NULL,
    relevance_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    review_notes TEXT,
    reviewed_by UUID REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 5. AML Rules & Detection Scenarios
CREATE TABLE rules (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    rule_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 1,
    parameters_json JSONB NOT NULL,
    risk_weight NUMERIC(5,2) NOT NULL DEFAULT 50.00,
    severity VARCHAR(20) NOT NULL DEFAULT 'HIGH',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    approved_by_checker UUID REFERENCES users(id),
    effective_from DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 6. Risk Scoring Engine
CREATE TABLE risk_assessments (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    assessment_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    overall_score NUMERIC(5,2) NOT NULL,
    risk_band VARCHAR(20) NOT NULL,
    factors_breakdown_json JSONB NOT NULL,
    assessed_by_user_id UUID REFERENCES users(id),
    override_score NUMERIC(5,2),
    override_band VARCHAR(20),
    override_reason TEXT,
    override_by_user_id UUID REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 7. Alert Management
CREATE TABLE alerts (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    alert_number VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    account_id UUID REFERENCES accounts(id) ON DELETE SET NULL,
    transaction_id UUID REFERENCES transactions(id) ON DELETE SET NULL,
    rule_id UUID REFERENCES rules(id) ON DELETE SET NULL,
    trigger_event VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL DEFAULT 'HIGH',
    score NUMERIC(5,2) NOT NULL DEFAULT 75.00,
    status VARCHAR(30) NOT NULL DEFAULT 'NEW',
    assigned_to_user_id UUID REFERENCES users(id),
    sla_due_at TIMESTAMPTZ NOT NULL,
    trigger_payload_json JSONB,
    disposition_notes TEXT,
    closed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 8. Case Management & Investigations
CREATE TABLE cases (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    case_number VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'HIGH',
    status VARCHAR(30) NOT NULL DEFAULT 'IN_INVESTIGATION',
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    primary_analyst_id UUID REFERENCES users(id),
    approver_id UUID REFERENCES users(id),
    sla_due_at TIMESTAMPTZ NOT NULL,
    investigation_summary TEXT,
    rationale TEXT,
    closure_decision VARCHAR(50),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMPTZ
);

CREATE TABLE case_alerts (
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    alert_id UUID NOT NULL REFERENCES alerts(id) ON DELETE CASCADE,
    PRIMARY KEY (case_id, alert_id)
);

CREATE TABLE case_transactions (
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    transaction_id UUID NOT NULL REFERENCES transactions(id) ON DELETE CASCADE,
    PRIMARY KEY (case_id, transaction_id)
);

CREATE TABLE case_evidence (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    evidence_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    source_attribution VARCHAR(255) NOT NULL,
    document_metadata_id UUID REFERENCES document_metadata(id),
    recorded_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE case_tasks (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    assigned_to_user_id UUID REFERENCES users(id),
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMPTZ,
    due_date DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE case_notes (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    author_id UUID NOT NULL REFERENCES users(id),
    note_text TEXT NOT NULL,
    is_internal_only BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 9. Regulatory Reporting (รวม schema ဇယားသစ် အပြည့်အစုံ - V3)
CREATE TABLE regulatory_reports (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    report_number VARCHAR(50) NOT NULL UNIQUE,
    report_type VARCHAR(20) NOT NULL,
    case_id UUID NOT NULL REFERENCES cases(id),
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    jurisdiction_country VARCHAR(3) NOT NULL,
    reporting_institution TEXT NOT NULL,
    subject_name TEXT NOT NULL,
    narrative TEXT NOT NULL,
    total_amount_usd NUMERIC(18,2) NOT NULL,
    report_json TEXT,
    prepared_by_id UUID REFERENCES users(id),
    approved_by_id UUID REFERENCES users(id),
    submitted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 10. Audit, Notifications & Batch Imports
CREATE TABLE audit_events (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    correlation_id VARCHAR(100) NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actor_username VARCHAR(100) NOT NULL,
    actor_role VARCHAR(100),
    client_ip VARCHAR(50),
    module VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    before_state_json JSONB,
    after_state_json JSONB,
    details TEXT
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    recipient_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL DEFAULT 'INFO',
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMPTZ,
    link_url VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE batch_import_jobs (
    id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    job_type VARCHAR(50) NOT NULL,
    filename VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_rows INT NOT NULL DEFAULT 0,
    successful_rows INT NOT NULL DEFAULT 0,
    failed_rows INT NOT NULL DEFAULT 0,
    error_log_json JSONB,
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMPTZ
);

-- Indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_branch ON users(branch_id);
CREATE INDEX idx_customers_customer_number ON customers(customer_number);
CREATE INDEX idx_customers_branch ON customers(branch_id);
CREATE INDEX idx_customers_risk_rating ON customers(risk_rating);
CREATE INDEX idx_customers_kyc_status ON customers(kyc_status);
CREATE INDEX idx_customers_next_review ON customers(next_kyc_review_date);
CREATE INDEX idx_accounts_customer ON accounts(customer_id);
CREATE INDEX idx_accounts_number ON accounts(account_number);
CREATE INDEX idx_transactions_customer ON transactions(customer_id);
CREATE INDEX idx_transactions_account ON transactions(account_id);
CREATE INDEX idx_transactions_booking_date ON transactions(booking_date);
CREATE INDEX idx_transactions_reference ON transactions(reference_number);
CREATE INDEX idx_watchlist_entries_name ON watchlist_entries(primary_name);
CREATE INDEX idx_watchlist_entries_source ON watchlist_entries(source_id);
CREATE INDEX idx_screening_matches_customer ON screening_matches(customer_id);
CREATE INDEX idx_screening_matches_disposition ON screening_matches(disposition);
CREATE INDEX idx_alerts_customer ON alerts(customer_id);
CREATE INDEX idx_alerts_status ON alerts(status);
CREATE INDEX idx_alerts_severity ON alerts(severity);
CREATE INDEX idx_alerts_sla ON alerts(sla_due_at);
CREATE INDEX idx_alerts_assigned ON alerts(assigned_to_user_id);
CREATE INDEX idx_cases_customer ON cases(customer_id);
CREATE INDEX idx_cases_status ON cases(status);
CREATE INDEX idx_cases_priority ON cases(priority);
CREATE INDEX idx_cases_analyst ON cases(primary_analyst_id);
CREATE INDEX idx_regulatory_reports_status ON regulatory_reports(status);
CREATE INDEX idx_regulatory_reports_report_type ON regulatory_reports(report_type);
CREATE INDEX idx_regulatory_reports_case_id ON regulatory_reports(case_id);
CREATE INDEX idx_audit_timestamp ON audit_events(timestamp);
CREATE INDEX idx_audit_module ON audit_events(module);
CREATE INDEX idx_audit_actor ON audit_events(actor_username);
CREATE INDEX idx_notifications_recipient ON notifications(recipient_user_id, is_read);
