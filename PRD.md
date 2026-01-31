Metadata-Driven Healthcare Platform (MDHP)

Version: 1.1 (Final)
Status: Final / Implementation-ready
Primary Objective: Build a local-first, metadata-driven healthcare data platform with Java microservices, Kafka orchestration, and PySpark compute jobs (EMR in prod, Docker Spark in local).

1) Executive Summary

Healthcare organizations ingest data from diverse source systems (Claims, Eligibility, Provider, Auth, EHR). Traditional pipelines are hard-coded, costly to maintain, and slow to onboard new datasets.

MDHP is a metadata-driven and event-driven platform that:

stores dataset/pipeline configurations as metadata in a DB (UI-managed; no YAML)

orchestrates pipeline stages asynchronously with Kafka

triggers PySpark jobs for ingestion/transformation/enrichment

stores outputs into a Medallion architecture (raw, silver, gold) within a Docker-runnable database (Postgres default; Cockroach optional)

Behavior changes via metadata, not code.

2) Goals, Outcomes & Non-Goals
   2.1 Goals

Onboard new healthcare datasets/entities via UI + metadata without code changes

Support multiple source types:

FILE, DB (JDBC), API, Kafka

Use microservices for control plane + orchestration

Run compute only through PySpark jobs

Use Kafka to coordinate stage transitions:

ingestion → transformation → enrichment

Persist datasets in DB with medallion schemas:

raw → silver → gold

Provide strong reliability:

retries, exponential backoff, circuit breakers, DLQ

Provide operational safety:

run state tracking, job tracking, reconciler

Full local execution using Docker Compose

2.2 Success Metrics (KPIs)
KPI	Target
New entity onboarding time	< 30 minutes
Pipeline run visibility in UI	100%
Stable pipeline success rate	≥ 98%
Mean time to diagnose failure	< 15 minutes
DLQ and retry correctness	100% supported
Spark job terminal event publishing	100% (mandatory design constraint)
2.3 Non-Goals (Phase 1)

Hive / Lakehouse (S3+Iceberg/Delta)

Multi-tenant platform

Automated PHI masking enforcement

Schema registry integration (optional later)

3) Key Principles

Metadata-driven

Event-driven

Stage-based pipeline

Compute via PySpark only

Local-first

Observable by default

Resilient by default

4) High-Level Architecture
   React UI (Vite)
   │
   ▼
   API Gateway (Spring Cloud Gateway)
   │
   ▼
   Metadata Service  ──► Control Plane DB (Docker DB)
   │
   ▼
   Orchestrator Service
   │
   ▼ Kafka events
   Ingestion Service  ──► triggers PySpark job (Local Spark / EMR)
   │                            │
   │                            ▼
   │                      writes raw.*
   ▼
   Transformation Service ─► triggers PySpark job
   │                            ▼
   │                      writes silver.*
   ▼
   Enrichment Service ─────► triggers PySpark job OR executes SQL files
   ▼
   writes gold.*

5) Runtime Environments
   5.1 Local Mode (Developer)

Docker Compose runs:

Kafka + UI

DB (Postgres or Cockroach)

microservices

Spark standalone cluster

observability stack (Prometheus/Grafana/Loki/Tempo)

5.2 Production Mode

Kafka (MSK or equivalent)

DB (enterprise DB or managed)

Spark on AWS EMR

Observability using OTel + Grafana stack

6) Technology Stack
   UI

React

Vite

Microservices

Java 17

Spring Boot 3

Spring Kafka

Spring Cloud Gateway

Resilience4j

Flyway

Compute (MANDATORY)

PySpark jobs only

ingestion PySpark job

transformation PySpark job

enrichment PySpark job

Production run: EMR

Local run: Docker Spark

Data & Messaging

Kafka

Docker-runnable DB:

PostgreSQL (default)

CockroachDB optional

other JDBC docker DB allowed

Observability

OpenTelemetry

Prometheus + Grafana

Loki (logs)

Tempo (traces)

7) Pipeline Stages and Rules
   Pipeline stages

Ingestion

Transformation

Enrichment

✅ Kafka topic naming must reflect only these 3 stages
❌ Kafka topics must not include raw/silver/gold.

8) Medallion Architecture (DB-based)
   Physical schemas

raw schema

silver schema

gold schema

Required behavior

Ingestion produces raw tables

Transformation produces silver tables

Enrichment produces gold tables

9) Metadata Model (DB as Source of Truth)
   Key rule

There is no YAML.
All metadata lives in DB and is managed via UI.

10) Metadata Tables (Mandatory)
    10.1 source_metadata

Defines ingestion behavior.

Fields (minimum):

entity_id

subject_area

entity_name

source_type (FILE/DB/API/KAFKA)

source_config (JSONB)

version

active_flag

audit fields

10.2 transformation_metadata

Defines raw → silver processing.

Fields:

entity_id

input_table (raw.*)

output_table (silver.*)

transform_config (JSONB)

version

active_flag

10.3 enrichment_metadata

Defines silver → gold processing.

Fields:

entity_id

enrichment_mode (PYSPARK / SQL_FILES)

sql_path

sql_files (ordered)

enrich_config (JSONB)

version

active_flag

11) UI Requirements (React + Vite)
    Screens (MVP)

Subject areas

Entities

Source metadata editor (JSON editor)

Transformation metadata editor

Enrichment metadata editor

Versions + activation

Manual trigger

Runs list

Run details:

stage status

errors

job IDs

DLQ view

12) Microservices (Spring Boot)
    12.1 API Gateway

routes UI calls

authentication and authorization

throttling (optional)

12.2 Metadata Service

CRUD and validation for metadata

version activation

audit logging

12.3 Orchestrator Service

schedules pipeline runs

creates pipeline_run

emits ingestion.request

12.4 Ingestion Service

consumes ingestion.request

triggers PySpark ingestion job

tracks job_execution

consumes ingestion.completed and emits transformation.request

12.5 Transformation Service

consumes transformation.request

triggers PySpark transformation job

consumes transformation.completed and emits enrichment.request

12.6 Enrichment Service

consumes enrichment.request

executes:

PySpark enrichment job OR

SQL files execution from path

emits enrichment.completed

13) Kafka Requirements
    Recommended Topics

mdhp.pipeline.ingestion.request

mdhp.pipeline.ingestion.completed

mdhp.pipeline.transformation.request

mdhp.pipeline.transformation.completed

mdhp.pipeline.enrichment.request

mdhp.pipeline.enrichment.completed

mdhp.pipeline.failed

mdhp.pipeline.dlq

Event contract (mandatory fields)

runId

correlationId

subjectArea

entityName / entityId

entityVersion (metadata versions)

stage (INGESTION/TRANSFORMATION/ENRICHMENT)

status (REQUESTED/RUNNING/COMPLETED/FAILED)

timestamp

jobIdentifier (emr stepId/jobRunId)

errorDetails (optional)

14) DB Requirements (Pluggable & Docker)
    Requirement

Warehouse DB must be configurable at runtime.
Supported:

Postgres (default)

CockroachDB (optional)

Any docker JDBC DB

Flyway

All schemas must be created and evolved using Flyway.

15) Runtime State Tables (Mandatory)
    15.1 pipeline_run

Tracks pipeline lifecycle per runId.

15.2 pipeline_stage_run

Tracks each stage status per run.

15.3 job_execution

Tracks fire-and-forget PySpark job submission and lifecycle:

jobPlatform (LOCAL_SPARK / EMR)

jobIdentifier

15.4 pipeline_event_log

Stores produced/consumed Kafka event metadata.

15.5 sql_execution_log

Stores SQL file execution results for enrichment SQL mode.

16) Reliability & Resilience
    Circuit breakers

Resilience4j must guard:

Metadata service calls

Spark runner submission (EMR API)

Kafka producer operations

Retry with exponential backoff

configurable

with jitter

stage-specific retries

DLQ

poison events published to DLQ

UI can show DLQ events with retry option

17) Fire-and-Forget Safety Requirements (Mandatory)

The platform must implement two-layer safety:

Layer 1 (Primary): Spark job guarantees completion
Mandatory Spark job requirement

Every PySpark job must guarantee terminal event publishing:

publish COMPLETED event on success

publish FAILED event on exception

must run publishing logic in a driver-side finally block

Kafka publish reliability

PySpark job must retry Kafka publish with exponential backoff

max attempts configurable

Outbox pattern (recommended, treated as must-have)

Spark job must write a durable completion record before Kafka publish:

Table: job_completion_outbox (or use job_execution)

unique key (runId, stage)

terminal state stored in DB

Kafka publish updates state to SENT

Layer 2 (Secondary): Platform reconciler

A scheduled reconciler:

checks job_execution for stuck jobs

queries EMR/local spark for truth

republishes missing events from outbox if needed

marks failures if SLA exceeded

Consumer idempotency

All stage completion consumers must be idempotent:

natural key: (runId, stage)

ignore duplicates

never trigger next stage twice

18) Observability Requirements
    Metrics

stage duration histogram

success/failure counters

kafka lag

DLQ count

spark submission latency

Logs

Structured JSON logs must include:

runId

entityName

stage

jobIdentifier

Traces

OpenTelemetry propagation:
UI → gateway → orchestrator → stage service submission

19) Docker Compose Requirements (Mandatory)

Platform must provide docker-compose.yml running:

admin-ui (React+Vite)

api-gateway

metadata-service

orchestrator-service

ingestion-service

transformation-service

enrichment-service

Kafka + Zookeeper

Kafka UI

DB profile:

Postgres profile

Cockroach profile

Prometheus, Grafana, Loki, Tempo

Spark master + workers (local simulation)

20) Risks & Mitigation
    Risk: DB scalability

Mitigation: DB is pluggable locally; can move to lakehouse later.

Risk: Missing completion event

Mitigation: Spark job guaranteed terminal publish + outbox + reconciler + idempotency.

Risk: Metadata misconfiguration

Mitigation: validation + activation workflow + rollback versioning.

21) MVP Scope
    Included

Claims subject area (example)

File ingestion

raw/silver/gold tables

transformation rules

enrichment SQL execution

stage microservices

Kafka orchestration

docker-compose local setup

observability dashboards baseline