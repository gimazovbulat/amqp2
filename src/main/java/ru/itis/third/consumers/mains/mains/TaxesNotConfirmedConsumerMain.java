package ru.itis.third.consumers.mains.mains;

import com.rabbitmq.client.ConnectionFactory;
import ru.itis.third.consumers.mains.TaxesNotConfirmedConsumer;

public class TaxesNotConfirmedConsumerMain {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        TaxesNotConfirmedConsumer taxesNotConfirmedConsumer = new TaxesNotConfirmedConsumer();
        taxesNotConfirmedConsumer.doStuff(connectionFactory);
    }
}
