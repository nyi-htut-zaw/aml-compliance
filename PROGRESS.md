# AML Compliance & Financial Crime Platform Implementation Tracker

## Project Architecture Summary
- **Backend**: Spring Boot 3.4 / Java 21 LTS / Spring Security 6 / Spring Data JPA / Flyway / OpenAPI Swagger
- **Frontend**: Angular 20+ Enterprise Architecture / Angular Material / CDK / Reactive Forms
- **Database**: PostgreSQL 18 (`aml_compliance`) with comprehensive relational schema & synthetic seeds
- **Security**: JWT Stateless Auth, Role-Based Access Control, Maker-Checker Segregation of Duties, Append-only Audit Trail

## Milestone Status

- [x] **Milestone 0: Workspace Setup & Verification**
  - PostgreSQL 18 local database created: `aml_compliance`
  - Java 21 & Maven 3.9 toolchain verified
  - Architecture plan & ERD documented in artifacts

- [x] **Milestone 1: Backend Scaffolding & Flyway Migrations**
  - Spring Boot 3.4 `pom.xml` with verified pinned dependencies
  - Flyway V1 schema migration: 37 normalized tables, foreign keys, constraints, and indexes
  - Flyway V2 seed data: Synthetic organizations, users, roles, watchlists, rules, customers, accounts, transactions, alerts, cases
  - PostgreSQL connectivity & Flyway migration execution tested and verified successfully

- [ ] **Milestone 2: Security, IAM & Maker-Checker Engine**
  - JWT Authentication filter, Token provider, UserDetailsService
  - Password hashing (BCrypt), account lockout logic
  - Maker-Checker approval service for sensitive compliance actions
  - Audit event interceptor & repository

- [ ] **Milestone 3: Customer 360, KYC & Beneficial Ownership**
  - Customer API (Individual & Legal Entity CRUD)
  - KYC Review API (CDD / EDD reviews, schedule, decisions)
  - Beneficial Ownership (UBO) calculations & relationship tree
  - Customer Risk Scoring calculation

- [ ] **Milestone 4: Screening Engines (Sanctions, PEP, Adverse Media)**
  - Jaro-Winkler & Levenshtein fuzzy string matching utility
  - Screening Service with explainable hit calculation
  - Watchlist management & synthetic OFAC/UN/PEP data
  - Screening Match disposition & escalation workflow

- [ ] **Milestone 5: Transaction Monitoring (TM) Engine**
  - Transaction ingestion API & CSV importer with deduplication
  - TM Rules engine (Structuring, Velocity, Rapid Movement, High-Risk Country, Dormant Account)
  - Real-time rule evaluation & automated alert generation

- [ ] **Milestone 6: Alert & Case Management**
  - Alert queue, SLA timer, assignment, disposition
  - Alert-to-Case promotion workflow
  - Case workspace: Investigation summary, timeline, tasks, evidence register, investigator notes
  - Case closure with Maker-Checker sign-off

- [ ] **Milestone 7: Regulatory Reporting (STR/SAR) & Rules Administration**
  - Suspicious Activity / Transaction Report generator (FinCEN/goAML/FATF compliant JSON & XML)
  - Report lifecycle (Draft -> Review -> Approval -> Exported)
  - Rules Admin API: Parameter adjustment, simulation mode, versioning

- [ ] **Milestone 8: Angular 20+ Enterprise Frontend**
  - Enterprise responsive UI shell (Header, Sidebar, User Menu, Branch Switcher)
  - Dashboard: Real KPI metrics (Alert backlog, Case SLA, Risk distribution, Hit rates)
  - Customer 360 & UBO Tree view
  - Screening Console (Sanctions/PEP search & hit review)
  - Transaction Monitoring & Batch CSV Ingestion
  - Alert & Case Management Workspaces
  - Maker-Checker Approvals Queue
  - Audit Trail Viewer & System Health

- [ ] **Milestone 9: Integration Tests, Docker Deployment & Documentation**
  - Spring Boot JUnit 5 integration tests
  - Dockerfile & `docker-compose.yml` for containerized runs
  - Sample CSV data files (`synthetic_transactions.csv`, `synthetic_customers.csv`)
  - Enterprise `README.md` with complete operation guide
