package ru.itis.first.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import ru.itis.FilesService;
import ru.itis.Info;
import ru.itis.producers.Producer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class FileDownloadConsumer {
    private final static String QUEUE_NAME = "files_download_queue";
    private final static String ROUTING_KEY = "user.files.download";
    private final static String TO_PATH = "docs/files/";

    private FilesService filesService;

    public FileDownloadConsumer() {
        filesService = new FilesService();
    }

    public void doStuff(ConnectionFactory factory) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, Producer.FANOUT_EXCHANGE, ROUTING_KEY);
            channel.basicConsume(QUEUE_NAME, false, (consumerTag, message) -> {

                String messageAsString = new String(message.getBody());
                Info info = objectMapper.readValue(messageAsString, Info.class);
                System.out.println("info " + info);
                filesService.downloadFile(info, TO_PATH);
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
}