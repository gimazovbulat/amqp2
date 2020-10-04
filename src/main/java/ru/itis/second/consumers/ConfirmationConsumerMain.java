package ru.itis.second.consumers;

import com.rabbitmq.client.ConnectionFactory;

public class ConfirmationConsumerMain {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        ConfirmationConsumer confirmationConsumer = new ConfirmationConsumer();
        confirmationConsumer.doStuff(connectionFactory);
    }
}
