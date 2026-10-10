# Mini-Lab 01: Spring Modulith — Fronteiras do Monólito Modular

## Hipótese arquitetural

O Spring Modulith valida, em tempo de teste, as regras de dependência entre os módulos `ranking`, `courses` e `badges`. Dependência de `courses` para `ranking.internal.RankingService` faz `ApplicationModules.verify()` falhar o build. Materializa **ASR-01** e **ASR-02** (ADR-001).

## Execução

```bash
cd labs/lab01-spring-modulith
../../mvnw -f pom.xml test -Dtest=ModularityTests
```

Evidência: `docs/evidencias/labs/lab01/`

## Estrutura

```
lab01-spring-modulith/
├── pom.xml
├── src/main/java/com/educont/
│   ├── Application.java
│   ├── ranking/api/RankingQuery.java
│   ├── ranking/internal/RankingService.java
│   ├── courses/CourseCompletionService.java
│   └── badges/BadgeService.java
└── src/test/java/com/educont/ModularityTests.java
```

## Critério de aceite

- `BUILD SUCCESS`
- Surefire com `verificaFronteirasEntreModulos` OK
- Evidência em `docs/evidencias/labs/lab01/`
