```mermaid
usecaseDiagram
  actor User as UI_User
  actor System as Backend
  UI_User --> (Onboard new dataset)
  UI_User --> (Edit metadata)
  UI_User --> (Trigger pipeline run)
  System --> (Persist metadata)
  System --> (Execute ingestion)
  System --> (Execute transformation)
  System --> (Execute enrichment)
```
