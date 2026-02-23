# SICI (Starburst Infrastructure & Control Interface)

## Product Roadmap, Architecture & Milestone Plan

This document defines the architecture, identity, and incremental build plan for the SICI Control Plane service.

**Guiding Principle:**

> Build vertically in thin slices.
> Every milestone must result in a runnable, testable system.

---

## What SICI Is

SICI is **not**:

- A deployment script
- A Terraform wrapper
- A thin proxy to AWS
- A Starburst installer

SICI **is**:

A centralized **Control Plane** that manages the lifecycle, governance, and policy of Starburst clusters across teams and accounts.

It turns _"Starburst per team on EKS"_ into _"Starburst as an internal managed SaaS platform."_

That distinction changes everything.

---

## Architectural Layers

SICI has 5 fundamental layers. We build from inside-out.

### 1. API Layer (Control Plane Surface)

What users see: REST endpoints, async job model, idempotent lifecycle operations, and clear error contracts.

This layer:

- Never blocks on AWS
- Never performs long infra operations inline
- Always returns quickly
- Everything async

### 2. Domain Layer (Business Logic Core)

This is where the real value lives.

**Key domain objects:**

- `Cluster`
- `ProvisioningJob`
- `ClusterState`
- `Team`
- `Policy`
- `Quota`
- `AuditEvent`

This layer defines what states exist, what transitions are legal, what is allowed or forbidden, and what conditions cause failure.

> The domain layer must stay **persistence-agnostic** and **AWS-agnostic**.

### 3. Persistence Layer

Storage for clusters, jobs, idempotency keys, audit trail, and policy assignments.

| Stage   | Backend                                     |
|---------|---------------------------------------------|
| Initial | In-memory                                   |
| Target  | DynamoDB (control-plane-friendly) or RDS (if governance demands relational) |

This layer must support multi-instance control plane, restart safety, and consistent reads for status checks.

### 4. Execution Layer (Async Engine)

This is what makes SICI real.

**Options:** Step Functions (recommended), SQS + worker pods, EventBridge + worker, Kubernetes job controllers.

**Responsibilities:** Transition job states, retry logic, backoff strategy, failure isolation.

This layer turns _"Requested provisioning"_ into _"Infra actually created."_

### 5. Infrastructure Integration Layer

This is where AWS happens.

**Possible integrations:** STS AssumeRole, IAM role creation, VPC provisioning, EKS cluster creation, Helm deployment of Starburst, security group setup, network isolation enforcement, CADS / S3 cross-account policies.

This layer must be:

- **Idempotent**
- **Retry-safe**
- **Timeout-aware**
- Must **never** corrupt cluster state

---

## High-Level Roadmap

| Phase   | Focus                        | Outcome                              |
|---------|------------------------------|--------------------------------------|
| Phase 0 | Local runnable service       | Bootable Spring Boot app             |
| Phase 1 | Async provisioning contract  | Real control-plane API with job model |
| Phase 2 | Read APIs + idempotency      | Production-safe API semantics        |
| Phase 3 | Persistent storage           | Multi-instance safe                  |
| Phase 4 | Async execution engine       | Jobs transition automatically        |
| Phase 5 | AWS orchestration            | Real infra provisioning              |
| Phase 6 | Full lifecycle               | Create + Delete workflows            |
| Phase 7 | Governance                   | Multi-tenant SaaS behavior           |
| Phase 8 | Operability                  | Production readiness                 |

---

## Milestone M0 — Bootstrapped Service

**Goal:** Establish the project skeleton. Validate packaging, structure, and scanning. Confirm logging and actuator working. No domain complexity yet.

### Endpoints

- `GET /healthz` → `200`
- `GET /actuator/health` → `200`

### Done When

- [ ] App starts successfully
- [ ] Health endpoints respond correctly
- [ ] No errors in startup logs

---

## Milestone M1 — Async Provisioning Contract (Control Plane v1)

**Goal:** Implement core async job-based provisioning API without real AWS integration. This is the critical phase — we define the control plane contract and domain model. No AWS, no persistence, no orchestration. Just contract + model.

> This phase ensures the control plane **behaves** like a control plane.

### Endpoints

#### `POST /clusters`

**Request:**

```json
{
  "teamId": "team-abc",
  "environment": "dev"
}
```

**Response:** `202 Accepted`

```json
{
  "clusterId": "clu-123",
  "jobId": "job-456",
  "status": "PROVISIONING"
}
```

**Errors:**

- `400` → validation failure
- `500` → internal error

#### `GET /jobs/{jobId}`

**Response:**

```json
{
  "jobId": "job-456",
  "clusterId": "clu-123",
  "status": "PROVISIONING",
  "message": "Cluster provisioning requested"
}
```

**Errors:**

- `404` → job not found

### Internal Design

- In-memory repositories
- Domain models: `Cluster`, `ProvisioningJob`, `ProvisioningJobStatus`
- Service layer handles orchestration
- Consistent JSON error format

### Done When

- [ ] Happy path works
- [ ] 404 + validation covered
- [ ] Basic unit tests exist

---

## Milestone M2 — Cluster Read APIs + Idempotency

**Goal:** Make API production-safe and client-friendly. This is where we build production-grade API semantics.

Without idempotency: retries create duplicates, clients break, infra explodes.

### New Endpoints

- `GET /clusters/{clusterId}`
- `GET /clusters?teamId=...&environment=...`
    - Support pagination
    - Support filtering

### Idempotency

`POST /clusters` must support:

- `Idempotency-Key` header
- Same request + same key → same result

### Done When

- [ ] Duplicate creates are prevented
- [ ] List endpoint implemented
- [ ] Idempotency behavior tested

---

## Milestone M3 — Persistent Storage

**Goal:** Replace in-memory repositories with durable storage.

**Why not earlier?** The domain contract must stabilize first. Interfaces must be clean. Repositories must be abstract.

### Implementation

- DynamoDB tables (preferred) or RDS
- Partition key strategy
- Indexing for listing
- Consistent read patterns

### Requirements

- Survive restarts
- Multi-instance safe
- Repository interfaces unchanged

### Done When

- [ ] Data persists across restarts
- [ ] Local dev environment supported
- [ ] Tests updated

---

## Milestone M4 — Async Execution Engine

**Goal:** Provisioning jobs transition automatically. This is the pivot — jobs actually change status and cluster states evolve.

### Additions

#### Provisioner Interface

```java
interface Provisioner {
    void startProvisioning(String jobId);
}
```

#### Progression

1. **Fake provisioner** → simulate success
2. **Real async worker** → background state transition

#### Behavior

- `PROVISIONING` → `SUCCEEDED`
- `PROVISIONING` → `FAILED`
- Retry skeleton defined

### Done When

- [ ] Jobs update status automatically
- [ ] Background processing abstraction exists

---

## Milestone M5 — AWS Orchestration Integration

**Goal:** Start managing real infrastructure. Now SICI becomes real.

### Integrations

- Step Functions (preferred) **OR** SQS worker pool
- STS AssumeRole model

### Actions (incremental)

1. IAM role provisioning
2. Networking guardrails
3. EKS cluster management
4. Starburst deployment abstraction

### Architectural Constraint

Maintain strict separation between:

- **Domain state** — what the control plane knows
- **Execution state** — what the async engine tracks
- **AWS state** — what actually exists in the cloud

> This separation prevents corruption.

### Done When

- [ ] `POST /clusters` triggers real workflow
- [ ] Job state reflects workflow execution

---

## Milestone M6 — Full Lifecycle Management

**Goal:** Support deprovisioning. Delete must also be async.

### New Endpoint

- `DELETE /clusters/{clusterId}` → `202` + `jobId`

### Cluster States

```
REQUESTED → PROVISIONING → READY → DELETING → DELETED
                          ↘ FAILED
```

### Done When

- [ ] Create + Delete are async jobs
- [ ] State transitions consistent
- [ ] Fully tested lifecycle

---

## Milestone M7 — Governance & Multi-Tenancy

**Goal:** Transform into SaaS-style managed platform. This is where platform value appears.

> Without governance, it's just automation.

### Features

- AuthN/AuthZ (JWT/OIDC)
- RBAC / team roles
- Quotas
- Policy enforcement
- Audit trail

### Done When

- [ ] Unauthorized calls rejected
- [ ] Audit fields captured
- [ ] Per-team isolation enforced

---

## Milestone M8 — Operability & Production Readiness

**Goal:** Make the service production-ready and observable.

### Additions

- Correlation IDs
- Structured logging
- Metrics
- Error classification
- Circuit breakers
- OpenAPI / Swagger
- CI pipeline
- Container build
- Helm chart
- Example HTTP files
- Runbooks

### Done When

- [ ] Service is observable
- [ ] CI builds pass
- [ ] Documentation complete

---

## Development Strategy

Each milestone must:

1. Compile cleanly
2. Be runnable locally
3. Have at least minimal tests
4. Preserve clean layering:

```
API → Service → Domain → Repository
```

> No milestone introduces irreversible architecture debt.