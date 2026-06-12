# 🗺️ SIGA — Estado Actual

> `lazygit` friendly. Commitéame cuando termines la tanda.

---

## 📊 Métricas Globales

| Métrica | Valor | Target |
|---------|-------|--------|
| Tests totales | **672** | 500+ ✅ |
| Cobertura global | **86%** | 85% ✅ |
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
| entity | 27% | **94%** | 85% | ✅ |

> 🔐 Auditoría de seguridad incluida en tests de controllers (input validation, UUID sanitization, IDOR checks, error handling)

## 🔥 Completado: Cobertura Sales → 94% ✅

| Package | Antes | Ahora | Target | Estado |
|---------|-------|-------|--------|--------|
| controllers | 65% | **100%** | 85% | ✅ |
| usecase | 70% | **100%** | 85% | ✅ |
| domain/model | 80% | **100%** | 85% | ✅ |
| event | 10% | **96%** | 85% | ✅ |
| infrastructure/adapter | 90% | **100%** | 85% | ✅ |
| infrastructure/mapper | 85% | **100%** | 85% | ✅ |
| config | 100% | **100%** | 85% | ✅ |
| entity | 25% | **86%** | 85% | ✅ |

## 🔥 Completado: Cobertura Auth → 89% ✅

| Package | Antes | Ahora | Target | Estado |
|---------|-------|-------|--------|--------|
| controllers | 79% | **89%** | 85% | ✅ |
| usecase | 83% | **85%** | 85% | ✅ |
| domain/model | 98% | **98%** | 85% | ✅ |
| event | 65% | **81%** | 85% | 🚧 |
| infrastructure/adapter | 87% | **87%** | 85% | ✅ |
| infrastructure/mapper | 93% | **93%** | 85% | ✅ |
| config | 100% | **100%** | 85% | ✅ |
| entity | 53% | **87%** | 85% | ✅ |
| security | 97% | **97%** | 85% | ✅ |

---

## 📋 Backlog Priorizado

| # | Tarea | Prioridad | Métrica |
|---|-------|-----------|---------|
| 1 | ✅ **Cobertura billing → 86%** | ✅ Hecho | 98% ctrl / 100% domain / 94% entity |
| 2 | ✅ **Cobertura sales → 94%** | ✅ Hecho | 100% ctrl / 100% usecase / 86% entity |
| 3 | ✅ **Cobertura auth → 89%** | ✅ Hecho | 89% ctrl / 85% usecase / 87% entity |
| 4 | Tests gateway + registry (hoy 0%) | 🔥 Alta | 0% |
| 5 | GitHub Actions CI | 🔥 Alta | 📋 |
| 6 | ✅ **Subir agent.engine y agent.config** | ✅ Hecho | 87% agent total |
| 7 | ConsolidatedStockCacheTest (Docker) | ⚡ Media | ⚠️ Skipea |
| 8 | GODADMIN + SUPER_ADMIN (roles) | 🧱 Base | 4/6 |
| 9 | coverage.html → datos reales de JaCoCo | 🧱 Base | 📋 |

---

> 🎯 **Global: 86%** (antes 85%). Siguiente: agent.engine o gateway.
