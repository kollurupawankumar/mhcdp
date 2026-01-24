ADR 005: UI Editing UX for Metadata

- Intent: Define a consistent approach for UI-based editing of source, transformation, and enrichment metadata.
- Approach: UI fetches metadata from API gateway; inline editing for inputPath/outputPath for all three domains; PUT to respective endpoints to persist changes.
- Rationale: UI-driven configuration aligns with metadata-driven design; minimizes YAML/config files and keeps UI in sync with DB.
- Risks: Potential race with concurrent edits; mitigate with optimistic updates and server-side validation.
- Status: Proposed
