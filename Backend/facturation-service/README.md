# facturation-service

Invoice, tariff, and payment microservice for Clinique+. It runs on port `8086` and owns the PostgreSQL database `facturation_db`. No cross-service database relationships are used.

## Architecture

The service uses Spring Boot 4.1.2 / Java 21, Spring Data JPA, PostgreSQL in Docker and H2 for local development. It registers with Eureka, imports configuration from Config Server, exposes a REST API, and exchanges events over the shared durable topic exchange `clinique.events`.

Packages are separated into `domain`, `repository`, `service`, `controller`, `dto`, `client`, `event`, `messaging`, `config`, and `exception`. Financial values use `BigDecimal` with database precision `NUMERIC(12,2)`.

## Configuration and running

The checked-in local `application.yml` sets port `8086`; the existing Config Server files select H2 locally and PostgreSQL in the Docker profile. Compose already defines the `postgres-facturation` container and wires this service to it, Config Server, Eureka, and RabbitMQ.

```powershell
.\mvnw.cmd spring-boot:run
```

For the full Docker environment, from the repository root:

```powershell
cd Backend\facturation-service
.\mvnw.cmd -DskipTests package
cd ..\..
docker compose up -d --build facturation-service
```

The database defaults are `clinique` / `clinique`. To use a non-default password, pass `DB_PASSWORD` into the service container through its environment. In local development, set `SECURITY_ENABLED=false` (the default) and the service uses its H2 in-memory database.

## REST endpoints

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/factures?patientId=&statut=` | Filter invoices |
| GET | `/api/factures/{id}` | Invoice, lines, and payments |
| POST | `/api/factures/{id}/paiements` | Record a payment (201) |
| PATCH | `/api/factures/{id}/annuler` | Cancel an unpaid invoice |
| GET | `/api/factures/{id}/pdf` | Download a PDF invoice |
| POST | `/api/factures/{id}/recalculer-couverture` | Retry coverage lookup for a draft |
| GET | `/api/tarifs` | List tariffs |
| PUT | `/api/tarifs` | Upsert a list of tariffs by `(typeActe, code)` |

Invoice totals are recomputed from their lines. The last successful coverage rate is stored with the invoice, applied to the total, and rounded to two decimals using `HALF_UP`; the patient share is the remainder. Payments are limited to the unpaid invoice total. A consultation may only have one invoice. A draft can be recalculated through the additional endpoint when patient-service coverage becomes available. If coverage is temporarily unavailable while an already-issued invoice receives medication lines, the last known rate is retained so the invoice is not incorrectly changed back to a draft.

## OpenFeign integration

`patient-service` is called using `GET /api/patients/{id}/couverture`. The response agreed for this integration is `{"tauxPriseEnCharge": 0..100}`. A Resilience4j circuit breaker and Feign fallback use 0% and leave a new invoice in `BROUILLON` while the service is unavailable. The endpoint remains an integration gap: patient-service in this repository has no coverage controller or route, and must expose the agreed response before live coverage can work.

## RabbitMQ events

The consumers declare durable queues `facturation-service.consultation.terminee` and `facturation-service.medicaments.delivres`, bound to the `clinique.events` topic exchange. Listener failures receive up to three attempts with exponential backoff; exhausted or non-retryable failures are routed to the durable service-specific `.dlq` queues through `facturation-service.dlx`, not discarded. Monitor and replay these queues operationally after correcting the underlying issue.

The technical document lists the `consultation.terminee` envelope fields (`consultationId`, `rdvId`, `patientId`, `medecinId`, `specialiteCode`, `actes[]`) and the `medicaments.delivres` envelope fields (`delivranceId`, `consultationId`, `patientId`, `lignes[]`). The repository does not contain consultation-service or pharmacie-service producer implementations. Therefore the exact JSON property names and types inside `actes[]` and `lignes[]`, routing keys from those producers, and medication identifiers cannot be verified here. The consumer records currently express the implementation's expected shape, not a confirmed producer contract; confirm them against the producer code before enabling end-to-end event delivery. Act and medication codes are looked up in the configured tariff table.

Invoice creation is protected by a unique `consultationId` constraint. A concurrent duplicate that loses the insert race reloads and returns the winning invoice after its failed transaction has rolled back. Medication lines use `delivranceId` plus line index as their idempotency key.

The documented `facture.payee` consumer is notification-service, which is absent from this repository. This publisher retains the documented payload fields `factureId`, `patientId`, `montant`, and `mode`; currently, `montant` is the invoice total and `mode` is the final payment's mode. Those semantics and consumer enum compatibility cannot be verified without the consumer implementation and should be confirmed before integration.

When deploying the DLX configuration to an environment where the original durable input queues already exist without dead-letter arguments, RabbitMQ will reject redeclaration with inequivalent queue arguments. Drain and preserve existing messages, then perform a planned queue migration/redeclaration; do not delete non-empty queues as an upgrade shortcut.
