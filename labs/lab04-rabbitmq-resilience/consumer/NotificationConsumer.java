package com.educont.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * Consumer com Dead Letter Exchange (DLX).
 * Para simular o lab: NÃO subir este processo enquanto o publisher gera 50 eventos.
 * Mensagens ficam em gamificacao.notificacao até o consumer voltar (ou vão à DLQ após Nacks).
 */
public class NotificationConsumer {

    public static final String EXCHANGE = "gamificacao.events";
    public static final String QUEUE = "gamificacao.notificacao";
    public static final String DLX = "gamificacao.dlx";
    public static final String DLQ = "gamificacao.notificacao.dlq";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("gamificacao");
        factory.setPassword("gamificacao");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE, "topic", true);
        channel.exchangeDeclare(DLX, "topic", true);

        channel.queueDeclare(DLQ, true, false, false, null);
        channel.queueBind(DLQ, DLX, "#");

        java.util.Map<String, Object> argsQueue = new java.util.HashMap<>();
        argsQueue.put("x-dead-letter-exchange", DLX);
        channel.queueDeclare(QUEUE, true, false, false, argsQueue);
        channel.queueBind(QUEUE, EXCHANGE, "curso.concluido");

        System.out.println("Consumer ATIVO — aguardando CursoConcluidoEvent (Ctrl+C para parar)...");

        DeliverCallback callback = (tag, delivery) -> {
            String body = new String(delivery.getBody(), StandardCharsets.UTF_8);
            try {
                System.out.println("Processado: " + body);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (Exception ex) {
                // Nack sem requeue → DLQ via DLX
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                System.err.println("Falha → DLQ: " + ex.getMessage());
            }
        };

        channel.basicConsume(QUEUE, false, callback, tag -> {});
    }
}
