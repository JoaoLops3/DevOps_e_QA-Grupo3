# Documentação de Decisões Arquiteturais (Architecture Lab — AC2)

*Baseado no enunciado disponível em: `ExB1_Desafio_1_Decisoes_Arquiteturais_Tech_Lead.docx`*

## DECISÃO 1 — Devemos migrar agora para microsserviços?
**Posição:** (C) Ainda não há informação suficiente (mas fortemente inclinado para Não).

**Análise (papel de Tech Lead):**
Com 1.000 utilizadores e uma equipe de 6 desenvolvedores a operar um MVP funcional, introduzir microsserviços neste momento geraria complexidade acidental desnecessária.
*   **RFs (Requisitos Funcionais):** A aplicação atende atualmente às regras de negócio de ranking, cursos, moedas, badges e recompensas.
*   **RNFs (Requisitos Não Funcionais) faltantes:** Faltam dados sobre projeções exatas de acesso, requisitos de alta disponibilidade (SLAs), limites de latência aceitáveis, maturidade da equipa em práticas de DevOps e orçamento para infraestrutura.
*   **De RNF a ASR:** Um RNF só se transforma num Requisito Arquiteturalmente Significativo (ASR) quando as suas exigências não podem ser satisfeitas pela estrutura atual (monólito único com base de dados relacional), obrigando a uma mudança estrutural profunda na aplicação.

## MUDANÇA DE CENÁRIO 1 — O PRODUTO CRESCEU
**DECISÃO 2 — A arquitetura atual continua adequada?**
Não. O aumento para 100.000 utilizadores e a carga concentrada e desproporcional no módulo de ranking tornam o modelo atual ineficiente.

### Comparação de Alternativas

| Alternativa | Benefício | Custo/risco | ASR que justificaria |
| :--- | :--- | :--- | :--- |
| **Monólito em camadas** | Baixa complexidade operacional e um único processo de deploy. | Escala ineficiente (desperdício de recursos ao escalar partes ociosas) e gargalo na base de dados única. | Time-to-market rápido, Manutenibilidade básica. |
| **Monólito modular** | Forte separação lógica do código sem impacto na infraestrutura de rede. | Partilha dos mesmos limites físicos de CPU/Memória; risco de acoplamento oculto. | Manutenibilidade, Preparação para evolução. |
| **Serviço(s) independente(s)** | Escala cirúrgica apenas das partes exigidas (ranking) e liberdade tecnológica. | Alta complexidade operacional (monitorização, CI/CD, consistência eventual). | **Escalabilidade** independente, **Desempenho**. |

### Questões para Discussão
*   **Se somente o ranking exige mais capacidade, precisamos escalar todo o sistema?** Não precisamos (nem devemos) escalar todo o sistema junto.
*   **Qual alternativa possui menor complexidade operacional?** O monólito em camadas e o monólito modular.
*   **Qual alternativa permite maior independência de evolução?** A extração para serviço(s) independente(s).
*   **A equipa atual possui maturidade para operar uma solução distribuída?** Provavelmente não; uma equipa de 6 programadores sofrerá com a sobrecarga de gerir múltiplos serviços.
*   **Qual custo estamos aceitando ao escolher maior autonomia?** O drástico aumento da complexidade de infraestrutura, gestão de rede e tratamento de falhas distribuídas.


## MUDANÇA DE CENÁRIO 2 — CHEGOU A IA
**DECISÃO 3 — Onde deve ficar a capacidade de recomendação?**
Deve ficar como um **serviço independente em Python**.

**Justificativa (ASRs):**
A decisão sustenta-se nos ASRs de **Tolerância a falhas** (a queda da recomendação não pode impedir a conclusão de cursos no Core), **Escalabilidade** (o modelo recebe carga 10 vezes maior em determinados períodos) e **Deploy independente** (o modelo evolui num ciclo próprio de atualização).

**Provocação — Foi o Python que criou o microsserviço?**
Não. A restrição tecnológica de usar Python não justifica, isoladamente, a criação de um microsserviço (poderiam ser executados scripts locais ou tarefas em segundo plano). O que obriga a arquitetura distribuída é a combinação da tecnologia com os ASRs de tolerância a falhas e picos de carga independentes.


## MUDANÇA DE CENÁRIO 3 — COMUNICAÇÃO E FALHA
**DECISÃO 4 — O Core deve esperar a notificação responder?**
**Não.** O registo de conclusão, o cálculo da recompensa e a atualização de dados exigem consistência imediata (comunicação **síncrona** interna). O envio da notificação deve ser **assíncrono** (mensageria), garantindo que a sua indisponibilidade temporária não impeça a conclusão do curso.

### Questões para Discussão
*   **REST significa necessariamente microsserviços?** Não. REST é um estilo arquitetural de comunicação de rede que pode perfeitamente ser utilizado num monólito.
*   **Quando a comunicação síncrona é suficiente?** Quando a resposta é imediatamente obrigatória para a progressão do negócio e o serviço de destino possui altíssima fiabilidade.
*   **Qual ASR poderia justificar mensageria?** Tolerância a falhas e Desempenho (desacoplamento temporal).
*   **O que muda quando o consumidor está temporariamente indisponível?** A mensagem fica retida de forma segura na fila até à recuperação do serviço, sem impactar o produtor.
*   **RabbitMQ ou Kafka devem ser adotados apenas porque estão disponíveis?** Não. Em cenários mais simples, a implementação de filas virtuais na própria base de dados relacional (*Outbox Pattern*) resolve o problema sem onerar a equipa com complexidade infraestrutural desnecessária.


## REGISTRO DA PRIMEIRA DECISÃO — ADR

> **Decisão vigente do B1:** ver [ADR-001](../adr/ADR-001-monolito-modular.md) (monólito modular) e [ADR-003](../adr/ADR-003-servico-python-ia.md) (IA como serviço). O texto abaixo foi alinhado a essas ADRs — Ranking permanece no Core; só a IA sai para processo próprio.

| Parâmetro | Detalhe |
| :--- | :--- |
| **Contexto** | O sistema atingiu 100.000 utilizadores e o módulo de ranking sofre com carga desproporcional. A inclusão da IA traz picos de 10x de acesso. |
| **ASRs** | Isolamento lógico do Ranking (ASR-01), baixa complexidade operacional para 6 devs (ASR-02); depois, deploy/tolerância da IA (ASR-05/06). |
| **Alternativas consideradas** | 1. Monólito em camadas. 2. Monólito Modular (escolhido para o Core). 3. Extrair Ranking+IA agora (adiado para Ranking; adotado só para IA no Desafio 3). |
| **Decisão** | Manter Ranking, cursos e recompensas no **Core monólito modular** (Spring Modulith). Extrair apenas o motor de IA para serviço Python quando os ASRs de falha/ciclo de vida exigirem (ADR-003). |
| **Consequências / trade-offs** | **Prós:** fronteira de Ranking testável sem rede; equipe não opera microsserviços prematuros. **Contras:** Ranking ainda compartilha CPU/heap do Core até telemetria justificar extração. |

### C4 — Representar a Decisão
No nível de Container (C4), o Core Spring Boot permanece monólito modular (Ranking interno com API de módulo). Surge o contêiner **AI Service** (FastAPI) para recomendação/RAG, com Circuit Breaker no Core — ver diagramas em [`../diagramas/`](../diagramas/).


## FECHAMENTO

1. **Qual foi o requisito que mais influenciou a decisão arquitetural da equipe?**
   O requisito de escalabilidade assimétrica do ranking e o isolamento dos picos de carga (10x) do novo modelo de recomendação de IA.
2. **O sistema precisa realmente de microsserviços neste momento? Justifique com ASRs.**
   Não precisa de microsserviços granulares. O Core fica em **monólito modular** (ASR-01/02). A única fronteira de processo justificada cedo é a **IA em Python** (ASR-05/06) — não o Ranking.
3. **Qual decisão mudaria se o requisito de escala independente desaparecesse?**
   A equipe manteria o **Monólito Modular** sem sequer extrair a IA; scripts/batch Python bastariam até a tolerância a falhas virar ASR.
4. **Qual trade-off sua equipe aceitou conscientemente?**
   Aceitar CPU/memória compartilhadas no Core (Ranking ainda no mesmo processo) para **não** multiplicar deploys e rede com uma equipe de 6 pessoas.
5. **Que evidência futura poderia comprovar que a decisão foi adequada?**
   Métricas de telemetria demonstrando que, durante picos de utilização da IA ou consultas intensas ao ranking, o consumo de recursos (CPU e Memória) do Core permaneceu estável e a taxa de sucesso nas conclusões de curso não sofreu interrupções nem degradação de desempenho.