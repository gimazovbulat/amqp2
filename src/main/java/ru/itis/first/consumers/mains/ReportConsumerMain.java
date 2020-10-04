package ru.itis.first.consumers.mains;

import com.rabbitmq.client.ConnectionFactory;
import ru.itis.first.consumers.ReportConsumer;

public class ReportConsumerMain {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        ReportConsumer reportConsumer = new ReportConsumer();
        reportConsumer.doStuff(connectionFactory);
    }
}
