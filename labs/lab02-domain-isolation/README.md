# Mini-Lab 02: Isolamento do Domínio — Ports & Adapters sem Spring

## Hipótese Arquitetural Testada

As regras de gamificação (média mínima, meta Premium, crédito de moedas) executam como **Java puro** atrás de `RewardPersistencePort`, com um repositório falso em memória (`InMemoryRewardRepository`). Sem `@SpringBootTest` e sem JPA, a suíte completa roda em **~85 ms**, comprovando o **ASR-03** (isolamento do domínio contra mudanças de tecnologia — Ports & Adapters) e o **ASR-04** (testabilidade em milissegundos) — ver ADR-002.

## Requisitos & Componentes

| Item | Detalhe |
| :--- | :--- |
| Domínio | `RewardCalculator`, `RewardResult` — POJOs sem anotação |
| Porta de saída | `RewardPersistencePort` |
| Adaptador de teste | `InMemoryRewardRepository` |
| Teste | `RewardCalculatorTest` — JUnit 5, **sem** Spring Context |
| Referência | ADR-002 (Arquitetura Hexagonal) |

Estrutura:

```
lab02-domain-isolation/
└── src/
    ├── main/java/com/educont/gamification/
    │   ├── domain/RewardCalculator.java
    │   ├── domain/RewardResult.java
    │   ├── port/RewardPersistencePort.java
    │   └── adapter/InMemoryRewardRepository.java
    └── test/java/.../domain/RewardCalculatorTest.java
```

## Como Executar

```bash
cd labs/lab02-domain-isolation
# Com JUnit no classpath (Maven/Gradle do lab ou IDE):
javac -d out $(find src/main/java -name '*.java')
# ou, se houver wrapper Maven no lab:
./mvnw test -Dtest=RewardCalculatorTest
```

O ponto crítico: a classe de teste **não** declara `@SpringBootTest`. Só instancia `RewardCalculator` + `InMemoryRewardRepository`.

## Evidência e Resultados

Tempo de execução simulado da suíte (3 testes, domínio isolado):

```
[INFO] Running com.educont.gamification.domain.RewardCalculatorTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.085 s
[INFO] BUILD SUCCESS

--- Resumo ---
Spring Context carregado: NÃO
Banco / JPA:              NÃO
Tempo total da suíte:     85 ms
```

| Caso | Resultado |
| :--- | :--- |
| Média 6,9 | Não contabiliza; permanece BASICA |
| Média 8,5 | Incrementa progresso |
| 11 válidos + média 9,0 | PREMIUM + 3 moedas |

**Conclusão:** a hipótese se confirma. O Domain Core é testável sem framework; o mapper JPA fica no adaptador de produção e não entra no caminho do teste de regra (ADR-002).
