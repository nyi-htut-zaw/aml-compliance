# AML Compliance & Financial Crime Platform

> **⚠️ DISCLAIMER**: This is a **demonstration platform only**. It does not constitute legal or regulatory compliance advice. Do not use in production without validation by qualified AML compliance professionals and legal counsel. No real external integrations are active.

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    AML Compliance Platform                    │
├──────────────────────┬──────────────────────────────────────┤
│  Angular 17 Frontend │  Spring Boot 3.4 Backend              │
│  Port: 4200          │  Port: 8080                           │
│  Angular Material    │  Java 21 LTS / Spring Security 6      │
│  Reactive Forms      │  JWT Auth + Maker-Checker             │
│  Lazy-loaded modules │  Flyway migrations + PostgreSQL 15    │
└──────────────────────┴──────────────────────────────────────┘
                               │
                    PostgreSQL 18 (local)
                    Database: aml_compliance
```

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.4.3, Java 21 LTS |
| Security | Spring Security 6, JJWT 0.12.6, BCrypt, Maker-Checker |
| Database | PostgreSQL 18 (local) / PostgreSQL 15 (Docker) |
| Migrations | Flyway |
| ORM | Spring Data JPA / Hibernate |
| API Docs | SpringDoc OpenAPI 2.8.5 (Swagger UI) |
| Frontend | Angular 17, Angular Material, SCSS |
| CSV | Apache Commons CSV |
| String Matching | Apache Commons Text (Jaro-Winkler, Levenshtein) |
| Testing | JUnit 5, Spring Boot Test, MockMvc |
| Containers | Docker, Docker Compose |

---

## Prerequisites (Local Run)

- **Java 21 LTS** — `C:\Program Files\Java\jdk-21`  
- **Maven 3.9+**  
- **Node 20 / npm 10**  
- **PostgreSQL 18** running locally on port 5432

---

## Quick Start (Local)

### 1. Database Setup
PostgreSQL `aml_compliance` database already created. Flyway will run migrations automatically on startup.

### 2. Start Backend
```powershell
cd backend
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
mvn spring-boot:run
```
Backend starts on **http://localhost:8080**

### 3. Start Frontend
```powershell
cd frontend
npm install   # if not done
npm run start
```
Frontend on **http://localhost:4200**

### 4. Access Swagger UI
**http://localhost:8080/swagger-ui.html**

---

## Docker Compose (Full Stack)

```bash
docker compose up --build
```

- Backend: **http://localhost:8080**
- Frontend: **http://localhost:4200**
- Swagger: **http://localhost:8080/swagger-ui.html**

---

## Seed Users (from V2 migration)

| Username | Password | Role |
|----------|----------|------|
| `analyst.jones` | `Pass1234!` | ANALYST |
| `officer.smith` | `Pass1234!` | COMPLIANCE_OFFICER |
| `mlro.chen` | `Pass1234!` | MLRO |
| `admin.root` | `Pass1234!` | ADMIN |

---

## End-to-End Demo Workflow

1. **Login** → POST `/api/v1/auth/login` with analyst credentials
2. **Search Customers** → GET `/api/v1/customers`
3. **Import CSV Transactions** → POST `/api/v1/transactions/import` with `synthetic_transactions.csv`
4. **View Generated Alerts** → GET `/api/v1/alerts`
5. **Assign Alert** → POST `/api/v1/alerts/{id}/assign?userId={analystId}`
6. **Dispose Alert** → POST `/api/v1/alerts/{id}/dispose`
7. **Create Case** → POST `/api/v1/cases`
8. **Add Investigation Note** → POST `/api/v1/cases/{id}/notes`
9. **Escalate to MLRO** → POST `/api/v1/cases/{id}/escalate`
10. **Close Case** → POST `/api/v1/cases/{id}/close`
11. **File SAR** → POST `/api/v1/reports`
12. **View Audit Trail** → GET `/api/v1/audit/events`

---

## API Modules

| Module | Base URL | Description |
|--------|----------|-------------|
| Auth / IAM | `/api/v1/auth` | Login, profile, maker-checker |
| Customers | `/api/v1/customers` | Customer 360, KYC, UBO tree |
| Screening | `/api/v1/screening` | Sanctions/PEP/Adverse Media |
| Transactions | `/api/v1/transactions` | Ingest single or CSV batch |
| Alerts | `/api/v1/alerts` | Alert queue, assignment, disposition |
| Cases | `/api/v1/cases` | Investigation case workspace |
| Rules | `/api/v1/rules` | TM rule administration |
| Reports | `/api/v1/reports` | SAR/STR/CTR regulatory reports |
| Dashboard | `/api/v1/dashboard/kpis` | Live KPI metrics |
| Audit | `/api/v1/audit/events` | Append-only audit trail |

---

## Database Schema (Flyway Migrations)

| Migration | Description |
|-----------|-------------|
| `V1__initial_schema.sql` | 37 normalized tables with FK constraints, indexes |
| `V2__synthetic_seed_data.sql` | Org, users, roles, watchlists, rules, customers, transactions, alerts, cases |
| `V3__regulatory_reports_table.sql` | SAR/STR/CTR report lifecycle table |
| `V4__fix_regulatory_report_schema.sql` | Compatibility migration for legacy regulatory-report schemas |
| `V5__add_myanmar_customer_name.sql` | Optional Myanmar-script customer name |

---

## Security Model

- **JWT Bearer** authentication (stateless)
- **RBAC** roles: `ANALYST`, `SENIOR_ANALYST`, `COMPLIANCE_OFFICER`, `MLRO`, `ADMIN`
- **Maker-Checker** segregation: sensitive actions require a different approver than initiator
- **Account lockout**: 5 failed attempts → 30-minute lockout
- **Append-only audit trail**: every state change recorded

---

## Running Tests

```powershell
cd backend
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
mvn test
```

Tests use H2 in-memory PostgreSQL compatibility mode with Flyway migrations.

---

## AML Rules Implemented

> **Myanmar configuration note:** Myanmar-script customer names and optional `NRC_MY` identifiers are supported. Country of nationality/jurisdiction alone does not assign a high-risk rating. Existing seeded transaction rules and values below are synthetic demonstration settings, not Myanmar legal thresholds or approved bank policy; review and configure them with the bank's approved, effective-dated requirements before operational use.

| Rule Code | Name | Description |
|-----------|------|-------------|
| `TM-STR-01` | Structuring / Smurfing | Sub-$10K transfers in 72-hour window |
| `TM-VEL-02` | High Velocity Movement | Single transfer ≥ $50,000 |
| `TM-GEO-03` | High-Risk Jurisdiction | Corridor to sanctioned countries ≥ $10K |
| `TM-DOR-04` | Dormant Account Activity | Inflow to dormant account ≥ $25K |
| `TM-LGT-05` | Large Cash Transaction | Cash deposit/withdrawal ≥ $10K |

Screening: Jaro-Winkler + Levenshtein fuzzy matching against OFAC/UN/PEP watchlists (synthetic demo data).
"# aml_compliance" 
