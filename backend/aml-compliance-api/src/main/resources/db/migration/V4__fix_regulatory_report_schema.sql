-- V4: Align the legacy regulatory_reports table with the current JPA model.
-- Some local databases were created from the earlier schema shape and are missing
-- the columns introduced by the reporting model. This migration backfills them
-- without dropping the existing demo data.

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'regulatory_reports'
          AND column_name = 'case_id'
    ) THEN
        RAISE EXCEPTION 'regulatory_reports table is missing the required case_id column';
    END IF;
END $$;

ALTER TABLE regulatory_reports
    ADD COLUMN IF NOT EXISTS jurisdiction_country VARCHAR(3),
    ADD COLUMN IF NOT EXISTS reporting_institution TEXT,
    ADD COLUMN IF NOT EXISTS subject_name TEXT,
    ADD COLUMN IF NOT EXISTS narrative TEXT,
    ADD COLUMN IF NOT EXISTS total_amount_usd NUMERIC(18,2),
    ADD COLUMN IF NOT EXISTS report_json TEXT,
    ADD COLUMN IF NOT EXISTS prepared_by_id UUID,
    ADD COLUMN IF NOT EXISTS approved_by_id UUID,
    ADD COLUMN IF NOT EXISTS submitted_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;

UPDATE regulatory_reports
SET case_id = (
    SELECT id
    FROM cases
    ORDER BY created_at
    LIMIT 1
)
WHERE case_id IS NULL;

UPDATE regulatory_reports
SET jurisdiction_country = CASE
    WHEN jurisdiction LIKE 'US%' THEN 'USA'
    WHEN jurisdiction LIKE 'UK%' THEN 'GBR'
    WHEN jurisdiction = 'EU_FIU' THEN 'EU'
    WHEN jurisdiction = 'FATF_GENERIC' THEN 'XXX'
    ELSE 'USA'
END
WHERE jurisdiction_country IS NULL;

UPDATE regulatory_reports
SET reporting_institution = COALESCE(
    (SELECT name FROM organizations WHERE id = reporting_entity_id),
    'Unknown Institution'
)
WHERE reporting_institution IS NULL;

UPDATE regulatory_reports
SET subject_name = COALESCE(
    (SELECT full_name FROM customers WHERE id = customer_id),
    'Unknown Subject'
)
WHERE subject_name IS NULL;

UPDATE regulatory_reports
SET narrative = COALESCE(narrative, summary_of_suspicion)
WHERE narrative IS NULL;

UPDATE regulatory_reports
SET report_json = COALESCE(report_json, report_payload_json::text)
WHERE report_json IS NULL AND report_payload_json IS NOT NULL;

UPDATE regulatory_reports
SET total_amount_usd = COALESCE(
    total_amount_usd,
    CAST((report_payload_json->>'suspiciousAmountUSD') AS NUMERIC(18,2))
)
WHERE total_amount_usd IS NULL AND report_payload_json IS NOT NULL;

UPDATE regulatory_reports
SET prepared_by_id = COALESCE(prepared_by_id, drafted_by_user_id)
WHERE prepared_by_id IS NULL;

UPDATE regulatory_reports
SET submitted_at = COALESCE(submitted_at, submission_date)
WHERE submitted_at IS NULL;

UPDATE regulatory_reports
SET created_at = COALESCE(created_at, NOW())
WHERE created_at IS NULL;

UPDATE regulatory_reports
SET updated_at = COALESCE(updated_at, NOW())
WHERE updated_at IS NULL;

ALTER TABLE regulatory_reports
    ALTER COLUMN case_id SET NOT NULL,
    ALTER COLUMN jurisdiction_country SET NOT NULL,
    ALTER COLUMN reporting_institution SET NOT NULL,
    ALTER COLUMN subject_name SET NOT NULL,
    ALTER COLUMN narrative SET NOT NULL,
    ALTER COLUMN total_amount_usd SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN updated_at SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_regulatory_reports_status
    ON regulatory_reports(status);
CREATE INDEX IF NOT EXISTS idx_regulatory_reports_report_type
    ON regulatory_reports(report_type);
CREATE INDEX IF NOT EXISTS idx_regulatory_reports_case_id
    ON regulatory_reports(case_id);
