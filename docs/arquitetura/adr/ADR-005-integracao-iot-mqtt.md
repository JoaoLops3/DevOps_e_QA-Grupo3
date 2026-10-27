# ADR-005: Ingestão de Eventos IoT via Broker MQTT e Componente IoT Gateway

## Status
Aceito

## Contexto

Laboratórios da instituição passam a registrar presença e atividade por dispositivos físicos (ESP32 e Arduino): sensores de bancada, leitores de presença e estações com rede instável, pouca RAM e energia limitada. O volume por dispositivo é pequeno e frequente. O formato é telemetria, não um caso de uso HTTP do aluno.

Esses dispositivos não podem falar com o Core. Uma oscilação de Wi-Fi, um firmware que reenvia quadro ou um tópico malformado não pode segurar thread do Spring Boot nem abrir transação de gamificação. O Core já consome eventos pelo RabbitMQ (ADR-004). Falta a borda que traduz o mundo do laboratório para esse contrato.

Requisito funcional impactado:

- **RF06** — registro de presença e de atividades de laboratório a partir de dispositivos IoT, com efeito posterior nas regras de gamificação quando o evento é válido.

Requisitos não funcionais impactados: eficiência de rede e de energia no microcontrolador, e isolamento do Core diante de conectividade intermitente.

## Drivers (ASRs e RPCs)

- **ASR**: ASR-09 — protocolo leve para microcontroladores com pouca CPU, RAM e energia. O dispositivo publica e pode dormir; não mantém sessão HTTP.
- **ASR**: ASR-10 — o Core fica isolado das oscilações da rede IoT. Falha ou rajada de sensores não atravessa a fronteira do monólito.
- **RPC**: RPC-08 — os dispositivos têm baixo poder computacional. Cabeçalho mínimo, sessão barata e biblioteca que roda em firmware restrito.
- **RPC**: RPC-09 — é proibido o acoplamento direto entre o dispositivo IoT e o Core. Nenhum ESP32 chama endpoint do Spring Boot, e o Core não assina tópico MQTT.

## Alternativas Avaliadas

1. **Opção A — REST HTTP direto dos sensores para o Core**: cada leitura faz POST no Spring Boot, com JSON e, no melhor caso, autenticação. **Prós:** o mesmo estilo já usado pelo frontend, depuração com curl e nenhum broker novo. **Contras:** TCP mais TLS mais HTTP é pesado para o RPC-08. A conexão cai no meio do POST e o firmware precisa reimplementar retry. O dispositivo passa a conhecer a URL do Core, o que viola o RPC-09. Rajada de laboratório inteiro entra no pool de requisições do caminho do aluno e fere o ASR-10.

2. **Opção B — Eclipse Mosquitto (MQTT) mais um IoT Gateway Consumer**: o dispositivo publica num tópico (`lab/{instituicao}/{bancada}/presenca` ou `.../atividade`) com QoS 1. O Mosquitto absorve a intermitência. Um componente de borda, o IoT Gateway Consumer, é o único assinante que o Core reconhece: valida o payload, descarta quadro incompleto, normaliza para o evento interno e publica no RabbitMQ. O Core continua só com o contrato AMQP do ADR-004. **Prós:** MQTT é o protocolo que o firmware já sustenta; QoS 1 dá entrega ao menos uma vez sem sessão HTTP; o gateway é o fusível entre a rede do laboratório e o monólito; o Core não muda quando o tópico ou o firmware mudam. **Contras:** dois saltos (MQTT e AMQP) e um tradutor para operar. QoS 1 implica duplicata, então o gateway ou o consumidor precisa de idempotência.

3. **Opção C — WebSockets**: o dispositivo mantém um socket longo contra um endpoint do Core ou de um concentrador. **Prós:** canal full-duplex e payload livre, útil se o laboratório precisasse de comando remoto em tempo real. **Contras:** segurar conexão aberta custa RAM e rádio no ESP32, o oposto do ASR-09. Reconexão fica a cargo do firmware. Se o socket termina no Core, o RPC-09 é violado da mesma forma que no REST. Não há o desacoplamento de sessão que o broker MQTT oferece quando o dispositivo dorme.

## Decisão

Adotamos **Eclipse Mosquitto como broker MQTT** e um **IoT Gateway Consumer** que traduz o evento de laboratório para o RabbitMQ do Core.

O ASR-09 escolhe o protocolo: MQTT sobre TCP, pacote curto, sessão que sobrevive ao sono do rádio, biblioteca compatível com ESP32/Arduino. O ASR-10 e o RPC-09 escolhem a topologia: o dispositivo só enxerga o Mosquitto; o Core só enxerga o RabbitMQ; o gateway é o único processo que fala os dois. QoS 1 atende presença e atividade — perder o quadro é pior do que receber duas vezes — e a idempotência usa identificador do dispositivo mais timestamp ou sequence do firmware. O gateway autentica o dispositivo no broker, rejeita tópico fora do padrão e não abre a porta do Core para a VLAN do laboratório.

Evento inválido morre no gateway, com métrica e log, e não entra na fila de gamificação. Evento válido segue o mesmo trato do ADR-004: o Core aplica a regra de domínio depois, fora do ritmo do sensor.

## Consequências

- **Positivas**: o hardware gasta menos dados e menos energia do que gastaria com HTTP. A sessão MQTT e o QoS 1 cobrem a rede instável do laboratório sem código de retry no firmware além do cliente MQTT. O Core permanece desacoplado: oscilação, firmware defeituoso e pico de publicação param no Mosquitto ou no gateway. A gamificação continua recebendo fato de domínio pelo RabbitMQ, não telemetria crua.
- **Trade-offs / Riscos**: a arquitetura ganha um tradutor de protocolos. Bug de mapeamento no gateway vira presença errada ou evento descartado. QoS 1 entrega duplicata; sem chave idempotente o aluno recebe presença ou atividade em dobro. O Mosquitto e o gateway passam a ser componentes operacionais: broker sem autenticação na borda do laboratório é superfície de injeção de eventos. Credencial de dispositivo, ACL de tópico e limite de tamanho de payload fazem parte desta decisão, não de um endurecimento posterior.
