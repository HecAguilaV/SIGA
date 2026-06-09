# 🗺️ SIGA — Estado Actual

> `lazygit` friendly. Commitéame cuando termines la tanda.

---

## 📊 Métricas Globales

| Métrica | Valor | Target |
|---------|-------|--------|
| Tests totales | **672** | 500+ ✅ |
| Cobertura global | **74%** | 85% 🚧 |
| Servicios cloud | 0 | AWS 🔮 |

---

## 🔥 Completado: Cobertura Billing → 86% ✅

| Package | Antes | Ahora | Target | Estado |
|---------|-------|-------|--------|--------|
| controllers | 21% | **98%** | 85% | ✅ |
| usecase | 76% | **100%** | 85% | ✅ |
| domain/model | 75% | **100%** | 85% | ✅ |
| event | 5% | **94%** | 85% | ✅ |
| infrastructure/adapter | 96% | **96%** | 85% | ✅ |
| infrastructure/mapper | 74% | **100%** | 85% | ✅ |
| config | 100% | **100%** | 85% | ✅ |
| entity | 27% | **74%** | 85% | 🚧 |

> 🔐 Auditoría de seguridad incluida en tests de controllers (input validation, UUID sanitization, IDOR checks, error handling)

---

## 📋 Backlog Priorizado

| # | Tarea | Prioridad | Métrica |
|---|-------|-----------|---------|
| 1 | ✅ **Cobertura billing → 86%** (217 tests, 0 producción modificada) | ✅ Hecho | 98% ctrl / 100% domain / 94% event |
| 2 | Tests gateway + registry (hoy 0%) | 🔥 Alta | 0% |
| 3 | GitHub Actions CI | 🔥 Alta | 📋 |
| 4 | Subir sales de 65% → 80% | ⚡ Media | 65% |
| 5 | Cobertura auth completa (hoy 76%) | ⚡ Media | 76% |
| 6 | Subir agent.engine (70%) + agent.config (45%) | ⚡ Media | 45-70% |
| 7 | ConsolidatedStockCacheTest (Docker) | ⚡ Media | ⚠️ Skipea |
| 8 | GODADMIN + SUPER_ADMIN (roles) | 🧱 Base | 4/6 |
| 9 | coverage.html → datos reales de JaCoCo | 🧱 Base | 📋 |

---

> 🎯 **Global: 80%** (antes 74%). Siguiente: gateway+registry o subir sales/auth.
