-- V3: Add regulatory_reports table for SAR/STR/CTR filing workflow
-- DEMO PLATFORM: This does NOT create a real FIU submission system.

CREATE TABLE regulatory_reports (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    report_number       VARCHAR(50)   NOT NULL UNIQUE,
    report_type         VARCHAR(20)   NOT NULL,                          -- SAR, STR, CTR
    case_id             UUID          NOT NULL REFERENCES cases(id),
    status              VARCHAR(30)   NOT NULL DEFAULT 'DRAFT',          -- DRAFT, UNDER_REVIEW, APPROVED, EXPORTED, WITHDRAWN
    jurisdiction_country VARCHAR(3)   NOT NULL,
    reporting_institution TEXT        NOT NULL,
    subject_name        TEXT          NOT NULL,
    narrative           TEXT          NOT NULL,
    total_amount_usd    NUMERIC(18,2) NOT NULL,
    report_json         TEXT,                                           -- goAML-compatible JSON payload
    prepared_by_id      UUID          REFERENCES users(id),
    approved_by_id      UUID          REFERENCES users(id),
    submitted_at        TIMESTAMPTZ,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_regulatory_reports_status       ON regulatory_reports(status);
CREATE INDEX idx_regulatory_reports_report_type  ON regulatory_reports(report_type);
CREATE INDEX idx_regulatory_reports_case_id      ON regulatory_reports(case_id);
