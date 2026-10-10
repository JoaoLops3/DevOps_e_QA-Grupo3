package com.educont.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Publisher do Core — publica CursoConcluidoEvent sem esperar o consumidor.
 * Latência do request = persistência + publish ACK (~42 ms no cenário do lab).
 */
public class CursoConcluidoPublisher {

    public static final String EXCHANGE = "gamificacao.events";
    public static final String ROUTING_KEY = "curso.concluido";

    public static void main(String[] args) throws Exception {
        int total = args.length > 0 ? Integer.parseInt(args[0]) : 50;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("gamificacao");
        factory.setPassword("gamificacao");

        long inicio = System.nanoTime();
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE, "topic", true);

            for (int i = 1; i <= total; i++) {
                String payload = """
                        {"event":"CursoConcluidoEvent","alunoId":"aluno-%d","cursoId":"curso-%d","media":8.5,"ts":"%s"}
                        """.formatted(i, i, Instant.now()).trim();

                channel.basicPublish(
                        EXCHANGE,
                        ROUTING_KEY,
                        null,
                        payload.getBytes(StandardCharsets.UTF_8)
                );
            }
        }

        long elapsedMs = (System.nanoTime() - inicio) / 1_000_000;
        System.out.printf("Publicados %d eventos CursoConcluidoEvent em %d ms (média ~%d ms/req)%n",
                total, elapsedMs, Math.max(1, elapsedMs / total));
        System.out.println("Core respondeu sem esperar consumidor. Mensagens acumulam na fila se o consumer estiver OFF.");
    }
}
