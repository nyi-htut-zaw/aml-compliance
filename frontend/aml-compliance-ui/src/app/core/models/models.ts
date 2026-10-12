// ── Common API Response ────────────────────────────────────────────────────────
export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: string;
}

// ── Auth Models ───────────────────────────────────────────────────────────────
export interface LoginRequest {
  username: string;
  password: string;
}

export interface UserSummary {
  id: string;
  username: string;
  email: string;
  fullName: string;
  status: string;
  branchId?: string;
  branchCode?: string;
  branchName?: string;
  roles: string[];
  permissions: string[];
}

export interface AuthResponse {
  token: string;
  expiresInMs: number;
  user: UserSummary;
}

// ── Pagination ────────────────────────────────────────────────────────────────
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// ── Customer Models ───────────────────────────────────────────────────────────
export interface CustomerDto {
  id: string;
  customerNumber: string;
  fullName: string;
  fullNameMyanmar?: string;
  tradeName?: string;
  customerType: 'INDIVIDUAL' | 'LEGAL_ENTITY';
  jurisdictionCountry: string;
  riskRating: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  kycStatus: string;
  accountStatus: string;
  pep: boolean;
  sanctionsHit: boolean;
  adverseMediaHit: boolean;
  onboardingDate: string;
  nextKycReviewDate: string;
  occupationOrIndustry?: string;
  createdAt: string;
}

export interface CustomerCreateDto {
  fullName: string;
  fullNameMyanmar?: string;
  customerType: string;
  jurisdictionCountry: string;
  dateOfBirthOrIncorp: string;
  pep?: boolean;
  occupationOrIndustry?: string;
  sourceOfWealth?: string;
  sourceOfFunds?: string;
  taxIdentifier?: string;
  idType?: string;
  idNumber?: string;
  issuingCountry?: string;
}

// ── Alert Models ──────────────────────────────────────────────────────────────
export interface AlertDto {
  id: string;
  alertNumber: string;
  title: string;
  customerId: string;
  customerName: string;
  accountId?: string;
  transactionId?: string;
  ruleCode?: string;
  ruleName?: string;
  triggerEvent: string;
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  score: number;
  status: string;
  assignedToUserId?: string;
  assignedToUsername?: string;
  slaDueAt: string;
  triggerPayloadJson?: string;
  dispositionNotes?: string;
  closedAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface AlertDispositionDto {
  disposition: 'CLOSED_FALSE_POSITIVE' | 'CLOSED_TRUE_POSITIVE' | 'ESCALATED' | 'CONVERT_TO_CASE';
  notes: string;
}

export interface AlertMetrics {
  totalNew: number;
  totalAssigned: number;
  totalUnderReview: number;
  totalEscalated: number;
  overdueSla: number;
  critical: number;
  high: number;
  medium: number;
  low: number;
}

// ── Case Models ───────────────────────────────────────────────────────────────
export interface CaseDto {
  id: string;
  caseNumber: string;
  title: string;
  priority: string;
  status: string;
  customerId: string;
  customerNumber: string;
  customerName: string;
  primaryAnalystName?: string;
  slaDueAt: string;
  slaBreached: boolean;
  alertsCount: number;
  transactionsCount: number;
  evidenceCount: number;
  createdAt: string;
}

export interface CaseCreateDto {
  title: string;
  description: string;
  priority: string;
  customerId: string;
  alertIds?: string[];
  transactionIds?: string[];
}

// ── Transaction Models ────────────────────────────────────────────────────────
export interface TransactionDto {
  id: string;
  referenceNumber: string;
  accountId: string;
  accountNumber: string;
  customerId: string;
  customerName: string;
  transactionType: string;
  channel: string;
  amount: number;
  currency: string;
  baseAmountUsd: number;
  counterpartyName?: string;
  counterpartyAccount?: string;
  counterpartyBank?: string;
  counterpartyCountry: string;
  bookingDate: string;
  purposeCode?: string;
  narration?: string;
  status: string;
}

// ── Rule Models ───────────────────────────────────────────────────────────────
export interface RuleDto {
  id: string;
  ruleCode: string;
  name: string;
  description: string;
  ruleType: string;
  category: string;
  version: number;
  parametersJson: string;
  riskWeight: number;
  severity: string;
  active: boolean;
  effectiveFrom: string;
}

// ── Screening Models ──────────────────────────────────────────────────────────
export interface ScreeningMatchDto {
  id: string;
  runId: string;
  customerId: string;
  customerName: string;
  watchlistEntryName: string;
  watchlistSource: string;
  matchType: string;
  score: number;
  status: string;
  createdAt: string;
}

// ── Report Models ─────────────────────────────────────────────────────────────
export interface ReportDto {
  id: string;
  reportNumber: string;
  reportType: string;
  caseId: string;
  caseNumber: string;
  status: string;
  jurisdictionCountry: string;
  subjectName: string;
  narrative: string;
  totalAmountUsd: number;
  reportJson?: string;
  preparedByUsername?: string;
  approvedByUsername?: string;
  submittedAt?: string;
  createdAt: string;
  updatedAt: string;
}

// ── Dashboard Models ──────────────────────────────────────────────────────────
export interface DashboardKpis {
  alertMetrics: AlertMetrics;
  caseMetrics: {
    totalActive: number;
    escalatedToMlro: number;
    closedSuspicious: number;
    filedStr: number;
    slaBreached: number;
  };
  customerMetrics: {
    totalCustomers: number;
    highRisk: number;
    criticalRisk: number;
    kycExpired: number;
  };
  transactionMetrics: {
    totalTransactions: number;
  };
  reportMetrics: {
    draftReports: number;
    approvedReports: number;
    exportedReports: number;
  };
  generatedAt: string;
}

// ── Audit Models ──────────────────────────────────────────────────────────────
export interface AuditEventDto {
  id: string;
  module: string;
  action: string;
  entityType: string;
  entityId: string;
  actorId?: string;
  actorUsername?: string;
  description: string;
  correlationId?: string;
  ipAddress?: string;
  createdAt: string;
}
