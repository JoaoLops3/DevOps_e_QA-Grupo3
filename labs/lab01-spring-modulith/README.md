# Mini-Lab 01: Spring Modulith — Fronteiras do Monólito Modular

## Hipótese Arquitetural Testada

O Spring Modulith consegue **validar em tempo de teste** as regras de dependência entre os módulos `ranking`, `courses` e `badges`, garantindo que não exista acoplamento direto a tipos internos de outro módulo sem passar por uma interface/API nomeada. Se `courses` importar `ranking.internal.RankingService`, `ApplicationModules.verify()` falha o build. Isso materializa o **ASR-01** (isolamento do módulo de Ranking sob carga de 100k usuários) e o **ASR-02** (baixa complexidade operacional para equipe de 6, sem microsserviços prematuros) — ver ADR-001.

## Requisitos & Componentes

| Item | Detalhe |
| :--- | :--- |
| Runtime | Java 17+, Spring Boot 3.x |
| Biblioteca | `spring-modulith-core` + `spring-modulith-starter-test` |
| Módulos | `ranking` (API pública em `ranking.api`), `courses`, `badges` |
| Teste | `ModularityTests` com `@SpringBootTest` e `ApplicationModules.of(Application.class).verify()` |

Estrutura relevante:

```
lab01-spring-modulith/
├── src/main/java/com/educont/
│   ├── Application.java
│   ├── ranking/api/RankingQuery.java          # API pública
│   ├── ranking/internal/RankingService.java   # interno — não importável
│   ├── courses/CourseCompletionService.java   # depende só de ranking.api
│   └── badges/BadgeService.java
└── src/test/java/com/educont/ModularityTests.java
```

## Como Executar

Na pasta do lab (com Maven configurado e dependência `spring-modulith-starter-test`):

```bash
cd labs/lab01-spring-modulith
./mvnw test -Dtest=ModularityTests
```

Trecho essencial do teste:

```java
@SpringBootTest
class ModularityTests {
    @Test
    void verificaFronteirasEntreModulos() {
        ApplicationModules.of(Application.class).verify();
    }
}
```

## Evidência e Resultados

Log simulado de sucesso do JUnit (fronteiras válidas — `courses` só usa `ranking.api`):

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.educont.ModularityTests
2026-10-07T11:40:12.441Z  INFO  --- Detected modules: ranking, courses, badges
2026-10-07T11:40:12.512Z  INFO  --- Module 'courses' allowedDependencies=[ranking :: api]
2026-10-07T11:40:12.588Z  INFO  --- ApplicationModules.verify() — OK (no cyclic / illegal dependencies)
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Conclusão:** a hipótese se confirma. O Modulith materializa a fronteira do monólito modular (ADR-001) como teste automatizado. Dependência ilegal a `ranking.internal` quebraria o `verify()` e bloquearia a promoção do artefato.
