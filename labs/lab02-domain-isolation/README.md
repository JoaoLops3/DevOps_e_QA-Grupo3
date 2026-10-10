# Mini-Lab 02: Isolamento do Domínio — Ports & Adapters sem Spring

## Hipótese arquitetural

As regras de gamificação executam como Java puro atrás de `RewardPersistencePort`, com `InMemoryRewardRepository`. Não há `@SpringBootTest` nem JPA — **ASR-03** e **ASR-04** (ADR-002).

## Execução

```bash
cd labs/lab02-domain-isolation
../../mvnw -f pom.xml test
```

Evidência: `docs/evidencias/labs/lab02/`

## Casos cobertos

| Caso | Resultado |
| :--- | :--- |
| Média 6,9 | Não contabiliza; permanece BASICA |
| Média 8,5 | Incrementa progresso |
| 11 válidos + média 9,0 | PREMIUM + 3 moedas |

## Critério de aceite

- `Tests run: 3, Failures: 0`
- `pom.xml` sem dependência `spring-boot` (domínio puro)
- Evidência em `docs/evidencias/labs/lab02/`
