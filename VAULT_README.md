# Vault (local) — Quick reference for Nimbus

A short, practical guide for using a local HashiCorp Vault with this project.

---

**What is HashiCorp Vault**
- Vault is a secrets management tool that centrally stores, versions, and controls access to sensitive data (API keys, DB credentials, certificates).

**Config**

| Item | Value |
|------|-------|
| Vault server | http://localhost:8200 |
| Vault UI | http://localhost:8200/ui |
| Vault port (host) | 8200 |
| Dev root token (local only) | root-token |
| Vault KV backend (this app) | secret (KV v2) |
| Vault secret path used | secret/nimbus (logical) |

---

**Maven dependency**
- Add this to `pom.xml` to enable Spring Cloud Vault config support:

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-vault-config</artifactId>
</dependency>
```

**Docker: local vault server**
- `docker/docker-compose.yaml` contains a `vault` service running Vault in dev mode (root token `root-token`) and exposes port 8200.
- Start: `docker compose -f docker/docker-compose.yaml up -d`

**application.yaml**
- The app imports Vault at bootstrap so secrets are available for placeholders:

```yaml
spring:
  config:
    import: vault://

  cloud:
    vault:
      uri: ${VAULT_URI:http://localhost:8200}
      token: ${VAULT_TOKEN:root-token}
      authentication: TOKEN
      kv:
        backend: secret
        default-context: ${spring.application.name}
```

**Local Vault UI**
- URL: http://localhost:8200/ui

**How to add a secret via the Vault UI (KV v2, concise)**
1. Open UI → Secrets → select `secret/` (Key/Value).
2. Click Create secret. Path: `nimbus` (logical path).
3. Add key/value entries (e.g. `DB_URL`, `DB_USER`, `DB_PASS`) and Save.

**How to add secret via Docker (vault CLI)**
```powershell
# write multiple values to secret/nimbus
docker exec -it nimbus-vault vault kv put secret/nimbus \
  DB_URL=jdbc:postgresql://localhost:5432/nimbus \
  DB_USER=nimbus_user \
  DB_PASS=nimbus_pass

# add GitHub credentials example
docker exec -it nimbus-vault vault kv put secret/nimbus \
  GITHUB_CLIENT_ID=... GITHUB_CLIENT_SECRET=... GITHUB_SCOPE=read:user,user:email
```

**How to read secret via Docker (vault CLI) or HTTP**
```powershell
# read full secret (CLI)
docker exec -it nimbus-vault vault kv get secret/nimbus

# read single field (CLI)
docker exec -it nimbus-vault vault kv get -field=DB_PASS secret/nimbus

# HTTP (KV v2 path)
curl -s -H "X-Vault-Token: root-token" http://localhost:8200/v1/secret/data/nimbus | jq .
```

**KV v2 (Key/Value version 2) — differences & quick notes**
- KV v2 introduces versioning: each write creates a new version of the secret.
- HTTP API paths (KV v2) differ from logical paths used in UI/CLI:
  - Logical path (UI/CLI): `secret/nimbus`
  - HTTP read path: `/v1/secret/data/nimbus`
  - HTTP metadata path: `/v1/secret/metadata/nimbus`
- CLI and UI are KV-aware and hide the `/data/` prefix; direct HTTP/curl must include `/data/` for reads and writes.
- KV v2 response JSON places user data under `.data.data` (e.g. `.data.data.DB_PASS`).
- KV v2 supports metadata operations (list, history, delete versions).
- Quick check which KV version is mounted:
  ```powershell
  docker exec -it nimbus-vault vault secrets list -detailed
  # look for the `secret/` mount and its 'options' (version: 2)
  ```

**How Spring Boot loads secrets at startup**
- With `spring.config.import: vault://`, Spring Boot imports Vault as a Config Data source during bootstrap.
- Spring Cloud Vault reads keys from `secret/data/<context>` (KV v2) and adds them to the Spring Environment.
- Placeholders in `application.yaml` like `${DB_URL}` resolve from Vault-provided properties before beans (e.g., DataSource) are created.

**Security note**
- Dev-mode Vault with a static root token is for local development only. Do not commit real tokens or production secrets to source control. Use short-lived, least-privilege tokens or auth methods (AppRole, Kubernetes) for production.

---

*This document provides a concise overview of using a local HashiCorp Vault with the Nimbus project. For more detailed information, refer to the official HashiCorp Vault documentation.*

