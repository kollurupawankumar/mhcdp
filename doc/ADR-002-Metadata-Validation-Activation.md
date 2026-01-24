ADR 002: Metadata Validation & Activation

Status: Proposed

Context
- Metadata is edited via the UI and stored in the database. Activating a version should enforce basic data integrity.
- Currently, there are version fields and an active_flag on metadata tables; no formal activation workflow exists yet.

Decision
- Introduce a lightweight activation workflow:
  - Each metadata type (source, transformation, enrichment) exposes an activate endpoint (or an activation action) that validates required fields before setting active_flag = true and bumping version if applicable.
  - Basic validations are enforced in the service layer: required fields depending on metadata type (e.g., entity_id, subject_area, entity_name, source_type for source_metadata; input_table/output_table for transformation_metadata; enrichment_mode/sql_path for enrichment_metadata).
  - Activation increments the version and records the activation timestamp; deactivation is allowed by setting active_flag to false.
  - Validation errors are surfaced to the UI with clear messages to prevent activation of invalid metadata.

Consequences
- Improves data integrity and governance for metadata activation.
- Encourages UI-driven version control and safer rollouts of pipeline configurations.
- Adds a small surface area to implement with minimal API changes today; more can be added later if needed.

Rationale
- Phase 6+ requires a safe activation path and rollback strategy to align with the stated goals and KPIs.
- A simple Activation workflow keeps changes auditable and mitigates misconfigurations.

Status: Proposed
