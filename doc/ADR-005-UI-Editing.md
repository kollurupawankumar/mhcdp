ADR 005: UI Editing UX for Metadata

- Intent: Define how the UI edits metadata across source, transformation, and enrichment.
- Approach: UI fetches metadata via API gateway; inline editing for inputPath and outputPath; saves via PUT to respective endpoints.
- Rationale: Metadata-driven config via UI reduces YAML/ YAML-y config drift and keeps DB as truth source.
- Risks: Concurrent edits; mitigate with per-field save and optimistic updates in UI and server-side validation.
- Status: Proposed
