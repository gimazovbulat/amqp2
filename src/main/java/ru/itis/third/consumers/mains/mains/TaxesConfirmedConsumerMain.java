package ru.itis.third.consumers.mains.mains;

import com.rabbitmq.client.ConnectionFactory;
import ru.itis.second.consumers.ConfirmationConsumer;
import ru.itis.third.consumers.mains.TaxesConfirmedConsumer;

public class TaxesConfirmedConsumerMain {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        TaxesConfirmedConsumer taxesConfirmedConsumer = new TaxesConfirmedConsumer();
        taxesConfirmedConsumer.doStuff(connectionFactory);
    }
}
