package ru.itis.first.consumers.mains;

import com.rabbitmq.client.ConnectionFactory;
import ru.itis.first.consumers.AnalyticsConsumer;

public class AnalyticsConsumerMain {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        AnalyticsConsumer analyticsConsumer = new AnalyticsConsumer();
        analyticsConsumer.doStuff(connectionFactory);
    }
}
