# Documentação de Decisões Arquiteturais (Architecture Lab — AC2)

*Baseado no enunciado disponível em: `docs/Arquitetura/ExB1_Desafio_1_Decisoes_Arquiteturais_Tech_Lead.docx`*

## DECISÃO 1 — Devemos migrar agora para microsserviços?
**Posição:** (C) Ainda não há informação suficiente (mas fortemente inclinado para Não).

**Análise do Tech Lead:**
Com 1.000 utilizadores e uma equipa de 6 programadores a operar um MVP funcional, introduzir microsserviços neste momento geraria uma complexidade acidental desnecessária.
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

| Parâmetro | Detalhe |
| :--- | :--- |
| **Contexto** | O sistema atingiu 100.000 utilizadores e o módulo de ranking sofre com carga desproporcional. A inclusão da IA traz picos de 10x de acesso. |
| **ASRs** | Escalabilidade independente, Desempenho, Tolerância a falhas, Deploy independente. |
| **Alternativas consideradas** | 1. Escalar o monólito inteiro. 2. Monólito Modular. 3. Extrair Ranking e IA para serviços independentes (escolhido). |
| **Decisão** | Isolar estritamente o módulo de Ranking e o modelo de IA em serviços independentes, mantendo o domínio principal (cursos, recompensas, moedas) no Core. |
| **Consequências / trade-offs** | **Prós:** Protege o Core contra picos de acesso e falhas externas. **Contras:** Aumento do esforço em automação de CI/CD, monitorização de rede e gestão de dados distribuídos. |

### C4 — Representar a Decisão
No nível de Container (C4), o "Monólito Spring Boot" atual perde os componentes de Ranking e Recomendação. Surgem novos contêineres no diagrama: "Serviço de Ranking" (com a sua própria base de dados otimizada) e "Serviço de Recomendação IA", comunicando de forma síncrona (leituras de ranking) e assíncrona (mensageria para atualizações de pontuação/notificações) com o Core.


## FECHAMENTO

1. **Qual foi o requisito que mais influenciou a decisão arquitetural da equipe?**
   O requisito de escalabilidade assimétrica do ranking e o isolamento dos picos de carga (10x) do novo modelo de recomendação de IA.
2. **O sistema precisa realmente de microsserviços neste momento? Justifique com ASRs.**
   Não precisa de microsserviços granulares em larga escala, mas sim de uma Arquitetura Orientada a Serviços (SOA/Híbrida). A extração cirúrgica justifica-se estritamente pelos ASRs de **Escalabilidade Independente** (Ranking/IA) e **Tolerância a Falhas** (notificações e IA).
3. **Qual decisão mudaria se o requisito de escala independente desaparecesse?**
   A equipa manteria toda a aplicação sob a forma de um **Monólito Modular**. Sem a necessidade de escalar componentes de forma independente, a complexidade de infraestrutura e rede não se justificaria.
4. **Qual trade-off sua equipe aceitou conscientemente?**
   O aumento substancial da **complexidade operacional** (múltiplos processos de deploy, gestão de rede e monitorização) em prol de garantir a disponibilidade e estabilidade do fluxo principal do aluno.
5. **Que evidência futura poderia comprovar que a decisão foi adequada?**
   Métricas de telemetria demonstrando que, durante picos de utilização da IA ou consultas intensas ao ranking, o consumo de recursos (CPU e Memória) do Core permaneceu estável e a taxa de sucesso nas conclusões de curso não sofreu interrupções nem degradação de desempenho.