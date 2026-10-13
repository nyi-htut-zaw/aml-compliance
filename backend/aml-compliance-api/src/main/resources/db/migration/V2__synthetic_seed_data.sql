-- ============================================================================
-- Enterprise AML & Financial Crime Compliance Platform
-- Combined Synthetic Institutional Seed Data
-- ============================================================================

-- 1. Organizations & Branches
INSERT INTO organizations (id, code, name, country_code, license_number)
VALUES
('00000000-0000-0000-0001-000000000001', 'GTB-GLOBAL', 'Global Trust Bancorp', 'USA', 'FED-LIC-789012');

INSERT INTO branches (id, organization_id, code, name, city, country_code, status)
VALUES
('00000000-0000-0000-0002-000000000001', '00000000-0000-0000-0001-000000000001', 'BR-NYC', 'New York Flagship Branch', 'New York', 'USA', 'ACTIVE'),
('00000000-0000-0000-0002-000000000002', '00000000-0000-0000-0001-000000000001', 'BR-LON', 'London City Branch', 'London', 'GBR', 'ACTIVE'),
('00000000-0000-0000-0002-000000000003', '00000000-0000-0000-0001-000000000001', 'BR-SIN', 'Singapore APAC Hub', 'Singapore', 'SGP', 'ACTIVE');

INSERT INTO departments (id, branch_id, code, name)
VALUES
('00000000-0000-0000-0003-000000000001', '00000000-0000-0000-0002-000000000001', 'FCC', 'Financial Crime Compliance'),
('00000000-0000-0000-0003-000000000002', '00000000-0000-0000-0002-000000000001', 'AML-OPS', 'AML Monitoring Operations'),
('00000000-0000-0000-0003-000000000003', '00000000-0000-0000-0002-000000000001', 'SPEC-INV', 'Special Investigations Unit');

-- 2. Roles & Permissions
INSERT INTO roles (id, name, description, is_system)
VALUES
('00000000-0000-0000-0004-000000000001', 'ROLE_ADMIN', 'Platform Administrator with tenant and configuration rights', TRUE),
('00000000-0000-0000-0004-000000000002', 'ROLE_COMPLIANCE_OFFICER', 'Head of Compliance / Compliance Manager with approval authority', TRUE),
('00000000-0000-0000-0004-000000000003', 'ROLE_MLRO', 'Money Laundering Reporting Officer with regulatory submission rights', TRUE),
('00000000-0000-0000-0004-000000000004', 'ROLE_SENIOR_ANALYST', 'Senior Investigator with escalation and disposition review rights', TRUE),
('00000000-0000-0000-0004-000000000005', 'ROLE_ANALYST', 'First-line AML Analyst performing KYC and alert reviews', TRUE),
('00000000-0000-0000-0004-000000000006', 'ROLE_AUDITOR', 'Read-only compliance and regulatory auditor', TRUE);

INSERT INTO permissions (id, code, module, description)
VALUES
(GEN_RANDOM_UUID(), 'IAM_MANAGE', 'IAM', 'Manage users, branches and permissions'),
(GEN_RANDOM_UUID(), 'CUSTOMER_VIEW', 'KYC', 'View customer profiles and KYC records'),
(GEN_RANDOM_UUID(), 'CUSTOMER_EDIT', 'KYC', 'Create and edit customer records'),
(GEN_RANDOM_UUID(), 'CDD_APPROVE', 'KYC', 'Approve customer due diligence reviews'),
(GEN_RANDOM_UUID(), 'SCREENING_RUN', 'SCREENING', 'Run ad-hoc and batch screening'),
(GEN_RANDOM_UUID(), 'SCREENING_DISPOSE', 'SCREENING', 'Dispose and escalate screening matches'),
(GEN_RANDOM_UUID(), 'TM_VIEW', 'TM', 'View transactions and ingestion jobs'),
(GEN_RANDOM_UUID(), 'TM_INGEST', 'TM', 'Ingest transactions via API or CSV'),
(GEN_RANDOM_UUID(), 'RULES_MANAGE', 'RULES', 'Draft and edit detection rules'),
(GEN_RANDOM_UUID(), 'RULES_APPROVE', 'RULES', 'Checker approval for detection rules'),
(GEN_RANDOM_UUID(), 'ALERT_VIEW', 'ALERTS', 'View alerts and triage queues'),
(GEN_RANDOM_UUID(), 'ALERT_DISPOSE', 'ALERTS', 'Dispose and escalate alerts'),
(GEN_RANDOM_UUID(), 'CASE_VIEW', 'CASES', 'View cases and evidence registers'),
(GEN_RANDOM_UUID(), 'CASE_MANAGE', 'CASES', 'Manage case investigation and tasks'),
(GEN_RANDOM_UUID(), 'CASE_APPROVE', 'CASES', 'Approve case closure or escalation'),
(GEN_RANDOM_UUID(), 'STR_DRAFT', 'REPORTS', 'Draft suspicious activity reports'),
(GEN_RANDOM_UUID(), 'STR_APPROVE', 'REPORTS', 'Approve regulatory STR/SAR filings'),
(GEN_RANDOM_UUID(), 'STR_SUBMIT', 'REPORTS', 'Submit regulatory filings to FIU adapter'),
(GEN_RANDOM_UUID(), 'AUDIT_VIEW', 'AUDIT', 'View system and compliance audit events');

INSERT INTO role_permissions (role_id, permission_id)
SELECT '00000000-0000-0000-0004-000000000001', id FROM permissions;

INSERT INTO role_permissions (role_id, permission_id)
SELECT '00000000-0000-0000-0004-000000000002', id FROM permissions;

INSERT INTO role_permissions (role_id, permission_id)
SELECT '00000000-0000-0000-0004-000000000005', id FROM permissions
WHERE code IN ('CUSTOMER_VIEW', 'CUSTOMER_EDIT', 'SCREENING_RUN', 'SCREENING_DISPOSE', 'TM_VIEW', 'ALERT_VIEW', 'ALERT_DISPOSE', 'CASE_VIEW', 'CASE_MANAGE', 'STR_DRAFT');

INSERT INTO role_permissions (role_id, permission_id)
SELECT '00000000-0000-0000-0004-000000000003', id FROM permissions
WHERE code IN ('CUSTOMER_VIEW', 'CDD_APPROVE', 'ALERT_VIEW', 'CASE_VIEW', 'CASE_APPROVE', 'STR_DRAFT', 'STR_APPROVE', 'STR_SUBMIT', 'AUDIT_VIEW');

-- 3. Users
INSERT INTO users (id, branch_id, username, email, password_hash, full_name, status)
VALUES
('00000000-0000-0000-0005-000000000001', '00000000-0000-0000-0002-000000000001', 'admin', 'admin@globaltrustbank.com', crypt('Admin@123', gen_salt('bf', 10)), 'Chief Compliance Administrator', 'ACTIVE'),
('00000000-0000-0000-0005-000000000002', '00000000-0000-0000-0002-000000000001', 'sarah.compliance', 's.jenkins@globaltrustbank.com', crypt('Sarah@123', gen_salt('bf', 10)), 'Sarah Jenkins (Head of Compliance)', 'ACTIVE'),
('00000000-0000-0000-0005-000000000003', '00000000-0000-0000-0002-000000000001', 'david.mlro', 'd.vance@globaltrustbank.com', crypt('David@123', gen_salt('bf', 10)), 'David Vance (MLRO)', 'ACTIVE'),
('00000000-0000-0000-0005-000000000004', '00000000-0000-0000-0002-000000000001', 'alex.analyst', 'a.chen@globaltrustbank.com', crypt('Alex@123', gen_salt('bf', 10)), 'Alex Chen (Senior AML Analyst)', 'ACTIVE'),
('00000000-0000-0000-0005-000000000005', '00000000-0000-0000-0002-000000000001', 'priya.analyst', 'p.sharma@globaltrustbank.com', crypt('Priya@123', gen_salt('bf', 10)), 'Priya Sharma (AML Analyst)', 'ACTIVE'),
('00000000-0000-0000-0005-000000000006', '00000000-0000-0000-0002-000000000001', 'james.auditor', 'j.miller@globaltrustbank.com', crypt('Auditor@123', gen_salt('bf', 10)), 'James Miller (Internal Auditor)', 'ACTIVE');

INSERT INTO user_roles (user_id, role_id)
VALUES
('00000000-0000-0000-0005-000000000001', '00000000-0000-0000-0004-000000000001'),
('00000000-0000-0000-0005-000000000002', '00000000-0000-0000-0004-000000000002'),
('00000000-0000-0000-0005-000000000003', '00000000-0000-0000-0004-000000000003'),
('00000000-0000-0000-0005-000000000004', '00000000-0000-0000-0004-000000000004'),
('00000000-0000-0000-0005-000000000005', '00000000-0000-0000-0004-000000000005'),
('00000000-0000-0000-0005-000000000006', '00000000-0000-0000-0004-000000000006');

-- 4. Watchlist Sources & Entries
INSERT INTO watchlist_sources (id, name, code, list_type, version, provider, item_count, is_active)
VALUES
('00000000-0000-0000-0006-000000000001', 'US OFAC Specially Designated Nationals (Synthetic)', 'OFAC_SDN_SYNTHETIC', 'SANCTIONS', '2026.1', 'OFAC Synthetic Adapter', 5, TRUE),
('00000000-0000-0000-0006-000000000002', 'United Nations Security Council Sanctions (Synthetic)', 'UN_SANCTIONS_SYNTHETIC', 'SANCTIONS', '2026.2', 'UN Synthetic Adapter', 3, TRUE),
('00000000-0000-0000-0006-000000000003', 'Global Politically Exposed Persons Directory (Synthetic)', 'PEP_GLOBAL_SYNTHETIC', 'PEP', '2026.1', 'PEP WorldCheck Synthetic', 4, TRUE);

INSERT INTO watchlist_entries (id, source_id, entry_uid, entry_type, primary_name, aliases, pep_category, country_codes, date_of_birth, designator_programs, remarks)
VALUES
('00000000-0000-0000-0007-000000000001', '00000000-0000-0000-0006-000000000001', 'OFAC-98124', 'INDIVIDUAL', 'Viktor Petrovic', 'Victor Petrov, Viktor A. Petrovic', 'NONE', 'RUS, CYP', '1968-04-12', 'RUSSIA-EO14024', 'Designated under EO 14024 for strategic sector ownership.'),
('00000000-0000-0000-0007-000000000002', '00000000-0000-0000-0006-000000000001', 'OFAC-77190', 'ENTITY', 'Al-Fajr Maritime Logistics LLC', 'Al Fajr Shipping, Fajr Marine', 'NONE', 'IRN, UAE', NULL, 'SDGT', 'Designated for providing logistical support to proscribed networks.'),
('00000000-0000-0000-0007-000000000003', '00000000-0000-0000-0006-000000000003', 'PEP-44012', 'INDIVIDUAL', 'Carlos Eduardo Mendez', 'Carlos Mendez', 'FOREIGN_PEP', 'VEN', '1959-11-20', NULL, 'Former Minister of Energy and Mining, Bolivarian Republic of Venezuela.'),
('00000000-0000-0000-0007-000000000004', '00000000-0000-0000-0006-000000000003', 'PEP-88219', 'INDIVIDUAL', 'Elena Rostova', 'Yelena Rostova, Elena Rostoff', 'RCA', 'UKR, AUT', '1975-06-30', NULL, 'Close associate and family member of prominent foreign official.');

-- 5. AML Rules
INSERT INTO rules (id, rule_code, name, description, rule_type, category, version, parameters_json, risk_weight, severity, is_active, effective_from)
VALUES
('00000000-0000-0000-0008-000000000001', 'TM-STR-01', 'Structuring / Smurfing Threshold Alert', 'Flags transactions just under mandatory currency reporting threshold ($10,000) within 72 hours.', 'TRANSACTION_MONITORING', 'STRUCTURING', 1, '{"minAmount": 8500, "maxAmount": 9999, "windowHours": 72, "minTransactions": 2}', 80.00, 'HIGH', TRUE, CURRENT_DATE),
('00000000-0000-0000-0008-000000000002', 'TM-VEL-02', 'High-Velocity Rapid Movement of Funds', 'Detects significant incoming funds rapidly transferred out within 24 hours exceeding 80% of inflow.', 'TRANSACTION_MONITORING', 'RAPID_MOVEMENT', 1, '{"inflowThreshold": 50000, "drainPercentage": 80, "windowHours": 24}', 85.00, 'HIGH', TRUE, CURRENT_DATE),
('00000000-0000-0000-0008-000000000003', 'TM-GEO-03', 'High-Risk Jurisdiction Transaction Corridor', 'Identifies transactions to/from jurisdictions identified on FATF high-risk or increased monitoring lists.', 'TRANSACTION_MONITORING', 'HIGH_RISK_JURISDICTION', 1, '{"highRiskCountries": ["PRK", "IRN", "MMR", "RUS", "SYR", "YEM", "CYP"], "thresholdAmount": 10000}', 90.00, 'CRITICAL', TRUE, CURRENT_DATE),
('00000000-0000-0000-0008-000000000004', 'TM-DOR-04', 'Sudden Inflow into Dormant Account', 'Detects sudden substantial credit on an account with no activity for greater than 180 days.', 'TRANSACTION_MONITORING', 'DORMANT_ACTIVITY', 1, '{"dormantDays": 180, "minInflowAmount": 25000}', 70.00, 'MEDIUM', TRUE, CURRENT_DATE),
('00000000-0000-0000-0008-000000000005', 'TM-LGT-05', 'Large Cash Transaction Over Threshold', 'Alerts on cash deposits or withdrawals exceeding $10,000 regulatory reporting threshold.', 'TRANSACTION_MONITORING', 'THRESHOLD', 1, '{"cashThreshold": 10000}', 65.00, 'MEDIUM', TRUE, CURRENT_DATE);

-- 6. Synthetic Customers
INSERT INTO customers (id, branch_id, customer_type, customer_number, full_name, full_name_myanmar, trade_name, jurisdiction_country, date_of_birth_or_incorp, tax_identifier, risk_rating, kyc_status, account_status, onboarding_date, next_kyc_review_date, source_of_wealth, source_of_funds, occupation_or_industry, is_pep, sanctions_hit, adverse_media_hit)
VALUES
('00000000-0000-0000-0010-000000000001', '00000000-0000-0000-0002-000000000001', 'LEGAL_ENTITY', 'CUST-GTB-1001', 'Meridian Commodities Global Ltd', NULL, 'Meridian Commodities', 'GBR', '2018-05-14', 'GB-89214710', 'HIGH', 'UNDER_EDD', 'ACTIVE', '2023-01-15', CURRENT_DATE + INTERVAL '30 days', 'Commodity Trading Revenue & Cross-border Logistics', 'Wholesale Oil & Metal Contracts', 'Wholesale Commodity Brokerage', FALSE, FALSE, TRUE),
('00000000-0000-0000-0010-000000000002', '00000000-0000-0000-0002-000000000001', 'LEGAL_ENTITY', 'CUST-GTB-1002', 'Horizon Logistics SA', NULL, 'Horizon Swiss Express', 'CHE', '2015-09-22', 'CHE-33190284', 'MEDIUM', 'APPROVED', 'ACTIVE', '2022-06-10', CURRENT_DATE + INTERVAL '180 days', 'European Freight Forwarding & Intermodal Transport', 'Client Invoices & Freight Charges', 'Logistics and Transportation', FALSE, FALSE, FALSE),
('00000000-0000-0000-0010-000000000003', '00000000-0000-0000-0002-000000000001', 'INDIVIDUAL', 'CUST-GTB-1003', 'Viktor A. Petrovic', NULL, NULL, 'CYP', '1968-04-12', 'CY-99418290', 'CRITICAL', 'REJECTED', 'BLOCKED', '2021-11-04', CURRENT_DATE - INTERVAL '15 days', 'Private Equity & Real Estate Holdings in Eastern Europe', 'Dividends & Asset Disposals', 'Private Investor & Asset Manager', FALSE, TRUE, TRUE),
('00000000-0000-0000-0010-000000000004', '00000000-0000-0000-0002-000000000001', 'INDIVIDUAL', 'CUST-GTB-1004', 'Elena Rostova', NULL, NULL, 'AUT', '1975-06-30', 'AT-77182934', 'HIGH', 'UNDER_EDD', 'ACTIVE', '2022-03-18', CURRENT_DATE + INTERVAL '60 days', 'Family Trust Assets & Consultancies', 'Retainer Fees & Inheritance', 'Corporate Governance Consultant', TRUE, FALSE, FALSE),
('00000000-0000-0000-0010-000000000005', '00000000-0000-0000-0002-000000000001', 'INDIVIDUAL', 'CUST-GTB-1005', 'Marcus Vance', NULL, NULL, 'USA', '1982-08-19', 'US-SSN-4512', 'LOW', 'APPROVED', 'ACTIVE', '2020-04-10', CURRENT_DATE + INTERVAL '300 days', 'Senior Executive Salary & Equity Grants', 'Employment Direct Deposit', 'Chief Technology Officer', FALSE, FALSE, FALSE);

INSERT INTO customer_identifiers (customer_id, id_type, id_number, issuing_country, issue_date, expiry_date, is_primary)
VALUES
('00000000-0000-0000-0010-000000000001', 'INCORP_CERT', 'GB-CO-0991823', 'GBR', '2018-05-14', '2030-05-14', TRUE),
('00000000-0000-0000-0010-000000000002', 'LEI', '984500A1B2C3D4E5F678', 'CHE', '2015-09-22', '2028-09-22', TRUE),
('00000000-0000-0000-0010-000000000003', 'PASSPORT', 'CYP-P-8871920', 'CYP', '2020-02-10', '2030-02-10', TRUE),
('00000000-0000-0000-0010-000000000004', 'PASSPORT', 'AUT-P-6619028', 'AUT', '2021-07-15', '2031-07-15', TRUE),
('00000000-0000-0000-0010-000000000005', 'DRIVERS_LICENSE', 'NY-DL-5529182', 'USA', '2022-01-10', '2028-01-10', TRUE);

INSERT INTO customer_addresses (customer_id, address_type, street_line1, city, state_province, postal_code, country_code, is_current)
VALUES
('00000000-0000-0000-0010-000000000001', 'REGISTERED', '30 St Mary Axe, Floor 24', 'London', 'Greater London', 'EC3A 8EP', 'GBR', TRUE),
('00000000-0000-0000-0010-000000000002', 'REGISTERED', 'Bahnhofstrasse 45', 'Zurich', 'ZH', '8001', 'CHE', TRUE),
('00000000-0000-0000-0010-000000000003', 'RESIDENTIAL', '28 Archiepiskopou Makariou III', 'Limassol', 'Limassol', '3030', 'CYP', TRUE),
('00000000-0000-0000-0010-000000000004', 'RESIDENTIAL', 'Opernring 12, Top 8', 'Vienna', 'Vienna', '1010', 'AUT', TRUE),
('00000000-0000-0000-0010-000000000005', 'RESIDENTIAL', '140 West 57th Street, Apt 11B', 'New York', 'NY', '10019', 'USA', TRUE);

INSERT INTO beneficial_owners (entity_customer_id, owner_customer_id, owner_name, nationality, ownership_percentage, control_type, is_ubo, verification_status, effective_from)
VALUES
('00000000-0000-0000-0010-000000000001', '00000000-0000-0000-0010-000000000003', 'Viktor A. Petrovic', 'CYP', 60.00, 'DIRECT_SHAREHOLDER', TRUE, 'FLAGGED_SANCTION', '2019-01-01'),
('00000000-0000-0000-0010-000000000001', '00000000-0000-0000-0010-000000000005', 'Marcus Vance', 'USA', 25.00, 'VOTING_RIGHTS', TRUE, 'VERIFIED', '2019-01-01'),
('00000000-0000-0000-0010-000000000001', '00000000-0000-0000-0010-000000000004', 'Elena Rostova', 'AUT', 15.00, 'INDIRECT_SHAREHOLDER', FALSE, 'FLAGGED_PEP', '2021-06-01');

INSERT INTO customer_relationships (from_customer_id, to_customer_id, relationship_type, notes)
VALUES
('00000000-0000-0000-0010-000000000001', '00000000-0000-0000-0010-000000000002', 'BUSINESS_PARTNER', 'Frequent cross-border maritime commodity freight arrangements'),
('00000000-0000-0000-0010-000000000003', '00000000-0000-0000-0010-000000000004', 'SPOUSE', 'Known familial connection identified during 2024 EDD refresh');

INSERT INTO accounts (id, customer_id, branch_id, account_number, account_type, currency, balance, status, opened_at)
VALUES
('00000000-0000-0000-0020-000000000001', '00000000-0000-0000-0010-000000000001', '00000000-0000-0000-0002-000000000001', 'ACT-NYC-1099201', 'CORPORATE_CURRENT', 'USD', 1450000.00, 'ACTIVE', '2023-01-20'),
('00000000-0000-0000-0020-000000000002', '00000000-0000-0000-0010-000000000002', '00000000-0000-0000-0002-000000000002', 'ACT-LON-2088319', 'CORPORATE_CURRENT', 'EUR', 620000.00, 'ACTIVE', '2022-06-15'),
('00000000-0000-0000-0020-000000000003', '00000000-0000-0000-0010-000000000003', '00000000-0000-0000-0002-000000000001', 'ACT-NYC-3077421', 'WEALTH_MANAGEMENT', 'USD', 2850000.00, 'FROZEN', '2021-11-10'),
('00000000-0000-0000-0020-000000000004', '00000000-0000-0000-0010-000000000004', '00000000-0000-0000-0002-000000000002', 'ACT-LON-4011928', 'CHECKING', 'EUR', 185000.00, 'ACTIVE', '2022-03-25'),
('00000000-0000-0000-0020-000000000005', '00000000-0000-0000-0010-000000000005', '00000000-0000-0000-0002-000000000001', 'ACT-NYC-5022839', 'SAVINGS', 'USD', 45000.00, 'ACTIVE', '2020-04-15');

INSERT INTO transactions (id, reference_number, account_id, customer_id, transaction_type, channel, amount, currency, base_amount_usd, counterparty_name, counterparty_account, counterparty_bank, counterparty_country, booking_date, value_date, purpose_code, narration, status)
VALUES
('00000000-0000-0000-0030-000000000001', 'TXN-2026-0001', '00000000-0000-0000-0020-000000000001', '00000000-0000-0000-0010-000000000001', 'WIRE_OUT', 'SWIFT', 9800.00, 'USD', 9800.00, 'Al-Fajr Maritime Logistics LLC', 'IBAN-AE88291029', 'Emirates NBD', 'UAE', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 'FREIGHT_PAYMENT', 'Charter logistics invoice #9921', 'SETTLED'),
('00000000-0000-0000-0030-000000000002', 'TXN-2026-0002', '00000000-0000-0000-0020-000000000001', '00000000-0000-0000-0010-000000000001', 'WIRE_OUT', 'SWIFT', 9850.00, 'USD', 9850.00, 'Al-Fajr Maritime Logistics LLC', 'IBAN-AE88291029', 'Emirates NBD', 'UAE', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 'FREIGHT_PAYMENT', 'Charter logistics invoice #9922 - Split dispatch', 'SETTLED'),
('00000000-0000-0000-0030-000000000003', 'TXN-2026-0003', '00000000-0000-0000-0020-000000000001', '00000000-0000-0000-0010-000000000001', 'WIRE_OUT', 'SWIFT', 9750.00, 'USD', 9750.00, 'Al-Fajr Maritime Logistics LLC', 'IBAN-AE88291029', 'Emirates NBD', 'UAE', CURRENT_TIMESTAMP - INTERVAL '6 hours', CURRENT_TIMESTAMP - INTERVAL '6 hours', 'FREIGHT_PAYMENT', 'Charter logistics invoice #9923 - Remainder balance', 'SETTLED'),
('00000000-0000-0000-0030-000000000004', 'TXN-2026-0004', '00000000-0000-0000-0020-000000000003', '00000000-0000-0000-0010-000000000003', 'WIRE_IN', 'SWIFT', 175000.00, 'USD', 175000.00, 'Veles Capital Management', 'CY4400200128', 'Bank of Cyprus', 'CYP', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 'INVESTMENT_RETURN', 'Disposal proceeds Cyprus Special SPV', 'BLOCKED'),
('00000000-0000-0000-0030-000000000005', 'TXN-2026-0005', '00000000-0000-0000-0020-000000000002', '00000000-0000-0000-0010-000000000002', 'WIRE_IN', 'ONLINE_BANKING', 42000.00, 'EUR', 45780.00, 'Kuehne Freight Europe GmbH', 'DE89370400440532013000', 'Deutsche Bank AG', 'DEU', CURRENT_TIMESTAMP - INTERVAL '12 hours', CURRENT_TIMESTAMP - INTERVAL '12 hours', 'TRADE_RECEIVABLE', 'Payment for rail freight transit March 2026', 'SETTLED'),
('00000000-0000-0000-0030-000000000006', 'TXN-2026-0006', '00000000-0000-0000-0020-000000000005', '00000000-0000-0000-0010-000000000005', 'INTERNAL_TRANSFER', 'MOBILE_APP', 2500.00, 'USD', 2500.00, 'Marcus Vance', 'ACT-NYC-5022839', 'Global Trust Bancorp', 'USA', CURRENT_TIMESTAMP - INTERVAL '4 hours', CURRENT_TIMESTAMP - INTERVAL '4 hours', 'SAVINGS_TRANSFER', 'Monthly personal savings contribution', 'SETTLED');

INSERT INTO screening_matches (id, customer_id, transaction_id, watchlist_entry_id, match_type, match_score, matching_algorithm, matched_term, match_details_json, disposition, disposition_notes)
VALUES
('00000000-0000-0000-0040-000000000001', '00000000-0000-0000-0010-000000000003', NULL, '00000000-0000-0000-0007-000000000001', 'SANCTION', 96.50, 'JARO_WINKLER', 'Viktor A. Petrovic', '{"query": "Viktor A. Petrovic", "matched": "Viktor Petrovic", "dobMatched": true, "countryMatched": true}', 'TRUE_POSITIVE', 'Confirmed exact match against OFAC SDN EO14024. Customer accounts frozen.'),
('00000000-0000-0000-0040-000000000002', '00000000-0000-0000-0010-000000000001', '00000000-0000-0000-0030-000000000001', '00000000-0000-0000-0007-000000000002', 'SANCTION', 92.00, 'JARO_WINKLER', 'Al-Fajr Maritime Logistics LLC', '{"query": "Al-Fajr Maritime Logistics LLC", "matched": "Al-Fajr Maritime Logistics LLC", "country": "UAE"}', 'ESCALATED', 'Counterparty on outgoing SWIFT wire flagged on SDGT designation list.'),
('00000000-0000-0000-0040-000000000003', '00000000-0000-0000-0010-000000000004', NULL, '00000000-0000-0000-0007-000000000004', 'PEP', 98.00, 'EXACT', 'Elena Rostova', '{"query": "Elena Rostova", "matched": "Elena Rostova", "category": "RCA"}', 'PENDING', 'Under EDD review for Politically Exposed Relative status.');

INSERT INTO adverse_media_records (id, customer_id, headline, source_name, article_url, publication_date, risk_category, severity, match_score, relevance_status, review_notes)
VALUES
('00000000-0000-0000-0050-000000000001', '00000000-0000-0000-0010-000000000001', 'Maritime Transshipment Probe Targets Meridian Commodities Over Black Sea Dispatches', 'Financial Investigative Daily', 'https://compliance-news.mock/articles/meridian-probe-2025', CURRENT_DATE - INTERVAL '45 days', 'FRAUD', 'HIGH', 88.50, 'RELEVANT', 'Article alleges use of layered shell entities to obfuscate vessel origins.');

INSERT INTO alerts (id, alert_number, title, customer_id, account_id, transaction_id, rule_id, trigger_event, severity, score, status, assigned_to_user_id, sla_due_at, trigger_payload_json)
VALUES
('00000000-0000-0000-0060-000000000001', 'ALT-2026-0001', 'Potential Structuring: 3 Consecutive Transfers Under $10K in 48 Hours', '00000000-0000-0000-0010-000000000001', '00000000-0000-0000-0020-000000000001', '00000000-0000-0000-0030-000000000003', '00000000-0000-0000-0008-000000000001', 'TRANSACTION', 'HIGH', 84.00, 'ESCALATED', '00000000-0000-0000-0005-000000000004', CURRENT_TIMESTAMP + INTERVAL '12 hours', '{"totalAmount": 29400.00, "count": 3, "counterparty": "Al-Fajr Maritime Logistics LLC"}'),
('00000000-0000-0000-0060-000000000002', 'ALT-2026-0002', 'Sanctions Match on Counterparty: Al-Fajr Maritime Logistics', '00000000-0000-0000-0010-000000000001', '00000000-0000-0000-0020-000000000001', '00000000-0000-0000-0030-000000000001', '00000000-0000-0000-0008-000000000003', 'SCREENING_HIT', 'CRITICAL', 95.00, 'UNDER_REVIEW', '00000000-0000-0000-0005-000000000004', CURRENT_TIMESTAMP + INTERVAL '4 hours', '{"matchType": "SANCTION", "program": "SDGT", "score": 92.0}'),
('00000000-0000-0000-0060-000000000003', 'ALT-2026-0003', 'Direct Hit on OFAC Specially Designated National (Customer)', '00000000-0000-0000-0010-000000000003', '00000000-0000-0000-0020-000000000003', '00000000-0000-0000-0030-000000000004', NULL, 'SCREENING_HIT', 'CRITICAL', 99.00, 'CLOSED_TRUE_POSITIVE', '00000000-0000-0000-0005-000000000002', CURRENT_TIMESTAMP - INTERVAL '1 day', '{"matchType": "SANCTION", "program": "RUSSIA-EO14024", "score": 96.5}');

INSERT INTO cases (id, case_number, title, description, priority, status, customer_id, primary_analyst_id, approver_id, sla_due_at, investigation_summary, rationale)
VALUES
('00000000-0000-0000-0070-000000000001', 'CASE-2026-0001', 'Investigation: Meridian Commodities Structuring & Sanctioned Counterparty Nexus', 'Multi-faceted compliance investigation into Meridian Commodities Global Ltd regarding 3 consecutive sub-threshold wire transfers to designated counterparty Al-Fajr Maritime Logistics, coupled with 60% beneficial ownership by OFAC-designated individual Viktor Petrovic.', 'HIGH', 'IN_INVESTIGATION', '00000000-0000-0000-0010-000000000001', '00000000-0000-0000-0005-000000000004', '00000000-0000-0000-0005-000000000003', CURRENT_TIMESTAMP + INTERVAL '2 days', 'Analysis reveals intentional structuring of wire amounts ($9,800, $9,850, $9,750) within a 48-hour window. Beneficiary Al-Fajr Maritime Logistics is flagged on synthetic SDGT watchlists. Meridian UBO Viktor Petrovic is confirmed SDN.', 'Grounds for suspicious activity report (SAR/STR) filing to FinCEN and UK NCA under Money Laundering Regulations.');

INSERT INTO case_alerts (case_id, alert_id)
VALUES
('00000000-0000-0000-0070-000000000001', '00000000-0000-0000-0060-000000000001'),
('00000000-0000-0000-0070-000000000001', '00000000-0000-0000-0060-000000000002');

INSERT INTO case_transactions (case_id, transaction_id)
VALUES
('00000000-0000-0000-0070-000000000001', '00000000-0000-0000-0030-000000000001'),
('00000000-0000-0000-0070-000000000001', '00000000-0000-0000-0030-000000000002'),
('00000000-0000-0000-0070-000000000001', '00000000-0000-0000-0030-000000000003');

INSERT INTO case_evidence (id, case_id, evidence_type, title, description, source_attribution, recorded_by)
VALUES
('00000000-0000-0000-0071-000000000001', '00000000-0000-0000-0070-000000000001', 'TRANSACTION_FLOW', 'SWIFT MT103 Message Copies', 'Three wire instruction receipts sent via SWIFT network to Emirates NBD beneficiary account.', 'SWIFT Fin Messaging Gateway', '00000000-0000-0000-0005-000000000004'),
('00000000-0000-0000-0071-000000000002', '00000000-0000-0000-0070-000000000001', 'SCREENING_HIT', 'OFAC SDN Match Report: Al-Fajr Maritime Logistics', 'Jaro-Winkler match score 92.0% on entity name and UAE registration address.', 'Automated Screening Engine', '00000000-0000-0000-0005-000000000004');

INSERT INTO case_tasks (id, case_id, title, description, assigned_to_user_id, is_completed, due_date)
VALUES
('00000000-0000-0000-0072-000000000001', '00000000-0000-0000-0070-000000000001', 'Request Corroborating Invoices from Relationship Manager', 'Obtain commercial contracts and bills of lading corresponding to invoices #9921-#9923.', '00000000-0000-0000-0005-000000000004', TRUE, CURRENT_DATE),
('00000000-0000-0000-0072-000000000002', '00000000-0000-0000-0070-000000000001', 'Draft FinCEN SAR Filing Package', 'Prepare narrative describing structuring scheme, UBO nexus, and prohibited transshipment.', '00000000-0000-0000-0005-000000000004', FALSE, CURRENT_DATE + 1),
('00000000-0000-0000-0072-000000000003', '00000000-0000-0000-0070-000000000001', 'Submit SAR for MLRO Approval', 'Review SAR package with MLRO David Vance for electronic sign-off.', '00000000-0000-0000-0005-000000000003', FALSE, CURRENT_DATE + 2);

INSERT INTO case_notes (id, case_id, author_id, note_text, is_internal_only)
VALUES
('00000000-0000-0000-0073-000000000001', '00000000-0000-0000-0070-000000000001', '00000000-0000-0000-0005-000000000004', 'Commercial invoices submitted by RM show identical line items split into 3 tranches under $10,000 on consecutive dates. Clear badge of intentional structuring.', TRUE),
('00000000-0000-0000-0073-000000000002', '00000000-0000-0000-0070-000000000001', '00000000-0000-0000-0005-000000000003', 'Agreed with analyst assessment. Freezing further outward transfers from account ACT-NYC-1099201 pending MLRO review and SAR filing.', TRUE);

-- Regulatory Reports Data (V3 model fields included)
INSERT INTO regulatory_reports (id, report_number, report_type, case_id, status, jurisdiction_country, reporting_institution, subject_name, narrative, total_amount_usd, report_json, prepared_by_id, submitted_at)
VALUES
('00000000-0000-0000-0080-000000000001', 'SAR-US-2026-0042', 'SAR', '00000000-0000-0000-0070-000000000001', 'UNDER_REVIEW', 'USA', 'Global Trust Bancorp', 'Meridian Commodities Global Ltd', 'The subject entity conducted 3 wire transfers totaling $29,400 to a designated UAE maritime entity across 48 hours, in amounts deliberately kept below the $10,000 threshold. The 60% beneficial owner is a designated Russian national under EO 14024.', 29400.00, '{"filingInstitution": "Global Trust Bancorp", "fiuJurisdiction": "US_FINCEN", "suspiciousAmountUSD": 29400.00, "suspicionCategories": ["STRUCTURING", "SANCTIONS_EVASION"], "narrative": "Between April 7 and April 9, 2026, Meridian Commodities initiated three consecutive wires...", "subject": {"name": "Meridian Commodities Global Ltd", "taxId": "GB-89214710", "ubo": "Viktor A. Petrovic"}}', '00000000-0000-0000-0005-000000000004', CURRENT_TIMESTAMP);

INSERT INTO audit_events (correlation_id, actor_username, actor_role, client_ip, module, action, entity_type, entity_id, details)
VALUES
(GEN_RANDOM_UUID()::text, 'admin', 'ROLE_ADMIN', '127.0.0.1', 'IAM', 'LOGIN', 'USER', '00000000-0000-0000-0005-000000000001', 'Successful administrative authentication with MFA verified.'),
(GEN_RANDOM_UUID()::text, 'alex.analyst', 'ROLE_SENIOR_ANALYST', '192.168.1.42', 'ALERTS', 'DISPOSITION', 'ALERT', '00000000-0000-0000-0060-000000000001', 'Alert ALT-2026-0001 escalated to Case CASE-2026-0001.'),
(GEN_RANDOM_UUID()::text, 'david.mlro', 'ROLE_MLRO', '192.168.1.15', 'CASES', 'UPDATE', 'CASE', '00000000-0000-0000-0070-000000000001', 'Account freeze confirmed for Meridian Commodities Global Ltd.');

INSERT INTO notifications (recipient_user_id, title, message, notification_type, severity, is_read, link_url)
VALUES
('00000000-0000-0000-0005-000000000004', 'High Priority Alert Assigned: ALT-2026-0001', 'You have been assigned structuring alert ALT-2026-0001 on Meridian Commodities Global Ltd. SLA due in 12 hours.', 'CASE_ASSIGNMENT', 'WARNING', FALSE, '/alerts/00000000-0000-0000-0060-000000000001'),
('00000000-0000-0000-0005-000000000003', 'SAR Pending Approval: SAR-US-2026-0042', 'Analyst Alex Chen submitted SAR-US-2026-0042 for Meridian Commodities for MLRO sign-off.', 'MAKER_CHECKER_REQUEST', 'CRITICAL', FALSE, '/reports/00000000-0000-0000-0080-000000000001');
