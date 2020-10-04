package ru.itis.first.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import ru.itis.Info;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static ru.itis.producers.Producer.FANOUT_EXCHANGE;

public class AnalyticsConsumer {
    private final static String QUEUE_NAME = "logs_queue";
    private final static String ROUTING_KEY = "logs";
    private final static String TO_PATH = "docs/logs/";

    private final static String TXT_EXTENSION = ".txt";

    private final File txtBasicLogsFile;

    public AnalyticsConsumer() {
        txtBasicLogsFile = new File(TO_PATH + "logs" + TXT_EXTENSION);
        System.out.println("log file name: " + txtBasicLogsFile);
    }

    public void doStuff(ConnectionFactory factory) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, FANOUT_EXCHANGE, ROUTING_KEY);
            channel.basicConsume(QUEUE_NAME, false, (consumerTag, message) -> {

                String messageAsString = new String(message.getBody());
                Info info = objectMapper.readValue(messageAsString, Info.class);
                writeLogs(info);
                System.out.println(messageAsString);
                try {
                    channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
                } catch (IOException e) {
                    channel.basicReject(message.getEnvelope().getDeliveryTag(), false);
                    throw new IllegalStateException(e);
                }
            }, consumerTag -> {
            });
        } catch (TimeoutException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void writeLogs(Info info) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(txtBasicLogsFile, true));
        String toWrite = info.getLastName() + " " +
                info.getFirstName() + " " +
                info.getPassportNumber() + " " +
                info.getEmail() + "\n";
        System.out.println("toWrite string " + toWrite);
        bos.write(toWrite.getBytes());
        bos.flush();
        bos.close();
    }
}