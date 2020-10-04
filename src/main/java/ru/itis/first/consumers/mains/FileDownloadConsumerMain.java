package ru.itis.first.consumers.mains;

import com.rabbitmq.client.ConnectionFactory;
import ru.itis.first.consumers.FileDownloadConsumer;

public class FileDownloadConsumerMain {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        FileDownloadConsumer fileDownloadConsumer = new FileDownloadConsumer();
        fileDownloadConsumer.doStuff(connectionFactory);
    }
}
