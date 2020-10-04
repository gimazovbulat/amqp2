package ru.itis.producers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Component;
import ru.itis.Info;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class Producer {
    //exchanges
    public final static String FANOUT_EXCHANGE = "ex_fan";
    public final static String DIRECT_EXCHANGE = "ex_dir";
    public final static String TOPIC_EXCHANGE = "ex_topic";

    private Channel channel;

    public Producer() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try {
            Connection connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(FANOUT_EXCHANGE, BuiltinExchangeType.FANOUT, true);
            channel.exchangeDeclare(DIRECT_EXCHANGE, BuiltinExchangeType.DIRECT, true);
            channel.exchangeDeclare(TOPIC_EXCHANGE, BuiltinExchangeType.TOPIC, true);
        } catch (IOException | TimeoutException e) {
            throw new IllegalStateException(e);
        }
    }

    public void sendMessage(Info info, String routingKey, String exchange) {
        System.out.println(info);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            channel.basicPublish(exchange, routingKey, null, objectMapper.writeValueAsString(info).getBytes());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}

