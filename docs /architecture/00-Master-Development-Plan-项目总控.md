# AI Performance Engineer
# Master Development Plan (MDP) v1.0

> Status: Frozen
>
> Version: v1.0
>
> This document is the Single Source of Truth (SSOT) for the entire AI Performance Engineer project.
>
> All AI Coding Agents (Rowboat / Claude Code / Codex CLI / Cursor / RooCode) MUST follow this document.
>
> No Work Package may be developed outside this plan.

---

# 1. Vision

AI Performance Engineer is an autonomous performance engineering platform.

Its ultimate goal is to complete the entire performance engineering lifecycle automatically.

Business Flow

Performance Test

↓

Automatic Collection

↓

Automatic Analysis

↓

Automatic Root Cause Detection

↓

Optimization Recommendation

↓

Verification Test

↓

Knowledge Accumulation

---

# 2. Development Milestones

The project is divided into four milestones.

| Milestone | Name | Goal |
|------------|------|------|
| M1 | Agent MVP | Build the complete data collection capability |
| M2 | Performance Test MVP | Build pressure testing capability |
| M3 | AI MVP | Build intelligent analysis capability |
| M4 | Commercial | Productization and enterprise deployment |

---

# 3. Work Package Index

| WP | Module | Milestone | Priority | Goal | Depends On | Required By | Estimated Java Files | Estimated Workload | Deliverable | Owner | Risk | Completion Criteria | Status |
|----|----------|-----------|----------|------|------------|-------------|---------------------|-------------------|-------------|-------|------|---------------------|--------|
| WP001 | Agent Bootstrap | M1 | P0 | Build Agent Runtime | - | WP002 | 25 | 2 Days | Agent Runtime | AI | Low | Compile + Demo | Done |
| WP002 | Connector SDK | M1 | P0 | Plugin Framework | WP001 | WP003~WP006 | 32 | 3 Days | SDK | AI | Medium | Compile + Demo | Done |
| WP003 | JVM Connector | M1 | P0 | JVM Metrics Collection | WP001,WP002 | WP007 | 24 | 3 Days | JVM Connector | AI | Low | Real JMX Collection | Done |
| WP004 | Linux Connector | M1 | P0 | Linux Metrics Collection | WP002 | WP007 | 30 | 4 Days | Linux Connector | AI | Medium | /proc Collection | Not Started |
| WP005 | Redis Connector | M1 | P0 | Redis Cluster Collection | WP002 | WP007 | 35 | 4 Days | Redis Connector | AI | High | INFO Collection | Not Started |
| WP006 | MySQL Connector | M1 | P0 | MySQL Collection | WP002 | WP007 | 40 | 5 Days | MySQL Connector | AI | High | performance_schema | Not Started |
| WP007 | Observation Pipeline | M1 | P0 | Observation Transport | WP003~WP006 | WP013 | 45 | 5 Days | Observation Pipeline | AI | Medium | Pipeline Verified | Not Started |
| WP008 | Pressure Service | M2 | P0 | Pressure Test Service | WP007 | WP009 | 35 | 4 Days | Pressure Service | AI | Medium | API Verified | Not Started |
| WP009 | JMeter Parser | M2 | P0 | Parse JMeter Scripts | WP008 | WP010 | 40 | 5 Days | Business Flow Parser | AI | Medium | Script Parsed | Not Started |
| WP010 | Scenario Manager | M2 | P0 | Scenario Management | WP009 | WP011 | 35 | 4 Days | Scenario | AI | Low | CRUD + Demo | Not Started |
| WP011 | Pressure Executor | M2 | P0 | Execute Pressure Test | WP010 | WP012 | 45 | 5 Days | Executor | AI | High | Pressure Verified | Not Started |
| WP012 | Result Center | M2 | P1 | Test Result Management | WP011 | WP013 | 30 | 3 Days | Result Center | AI | Low | UI Verified | Not Started |
| WP013 | Evidence Engine | M3 | P0 | Generate Evidence | WP007 | WP014 | 40 | 5 Days | Evidence | AI | High | Evidence Generated | Not Started |
| WP014 | Rule Engine | M3 | P0 | Rule Analysis | WP013 | WP015 | 35 | 4 Days | Rule Engine | AI | Medium | Rule Verified | Not Started |
| WP015 | Correlation Engine | M3 | P0 | Metric Correlation | WP014 | WP016 | 40 | 5 Days | Correlation | AI | High | Correlation Verified | Not Started |
| WP016 | Timeline Engine | M3 | P1 | Timeline Analysis | WP015 | WP017 | 30 | 3 Days | Timeline | AI | Low | Timeline Generated | Not Started |
| WP017 | Topology Engine | M3 | P1 | Topology Generation | WP016 | WP018 | 35 | 4 Days | Topology | AI | Medium | Graph Generated | Not Started |
| WP018 | Root Cause Engine | M3 | P0 | Root Cause Analysis | WP017 | WP019 | 45 | 6 Days | RCA | AI | High | RCA Verified | Not Started |
| WP019 | Optimization Engine | M3 | P0 | Optimization Suggestion | WP018 | WP020 | 40 | 5 Days | Suggestion | AI | Medium | Suggestion Verified | Not Started |
| WP020 | Verification Engine | M3 | P0 | Re-Test Verification | WP019 | WP021 | 30 | 3 Days | Verification | AI | Low | Verified | Not Started |
| WP021 | Knowledge Center | M4 | P1 | Knowledge Accumulation | WP020 | WP022 | 35 | 4 Days | Knowledge | AI | Medium | Knowledge Search | Not Started |
| WP022 | REST API | M4 | P1 | Public API | WP021 | WP023 | 25 | 3 Days | REST API | AI | Low | API Verified | Not Started |
| WP023 | Console UI | M4 | P1 | Management Console | WP022 | WP024 | 50 | 6 Days | Console | AI | Medium | UI Verified | Not Started |
| WP024 | Deployment | M4 | P1 | Docker Deployment | WP023 | WP025 | 20 | 2 Days | Docker | AI | Low | Docker Compose | Not Started |
| WP025 | Kubernetes | M4 | P2 | K8S Deployment | WP024 | WP026 | 20 | 3 Days | Helm Chart | AI | Medium | K8S Verified | Not Started |
| WP026 | Security | M4 | P1 | Authentication | WP024 | WP027 | 30 | 4 Days | Auth | AI | Medium | Login Verified | Not Started |
| WP027 | Integration Test | M4 | P1 | End-to-End Test | WP026 | WP028 | 25 | 3 Days | Integration | AI | Medium | All Passed | Not Started |
| WP028 | CI/CD | M4 | P2 | Pipeline | WP027 | WP029 | 20 | 2 Days | CI/CD | AI | Low | Pipeline Passed | Not Started |
| WP029 | Documentation | M4 | P2 | User Guide | WP028 | WP030 | 10 | 2 Days | Docs | AI | Low | Documentation Complete | Not Started |
| WP030 | Release | M4 | P0 | Commercial Release | WP029 | - | 10 | 2 Days | Release v1.0 | AI | Low | GA Release | Not Started |

---

# 4. Global Engineering Rules

Every Work Package MUST satisfy the following rules.

## Rule 1

Only ONE Work Package may be developed at one time.

---

## Rule 2

Cross-WP implementation is forbidden.

---

## Rule 3

Blueprint is the only design source.

---

## Rule 4

All code must compile successfully.

---

## Rule 5

Mock implementation is forbidden.

Real implementation only.

---

## Rule 6

TODO is forbidden.

UnsupportedOperationException is forbidden.

Empty implementation is forbidden.

---

## Rule 7

Every Work Package must pass verification before merge.

---

## Rule 8

Every Work Package must produce:

- Blueprint
- Source Code
- Unit Test (if applicable)
- Demo
- Documentation

---

# 5. Current Progress

| Item | Status |
|------|--------|
| Blueprint Template | Completed |
| WP001 Blueprint | Completed |
| WP002 Blueprint | Completed |
| WP003 Blueprint | Completed |
| Current Development | WP004 |

---

# 6. Next Action

Current Work Package

WP004

Linux Connector

Do NOT start any other Work Package until WP004 is completed.

---

END