package ru.itis.second.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import ru.itis.*;
import ru.itis.producers.Producer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class ConfirmationConsumer {
    private final static String QUEUE_NAME = "confirmation";
    private final static String ROUTING_KEY = "confirm";
    private final static String TO_PATH = "docs/";

    private final static String TEMPLATE_PATH = "src/main/resources/confirmation.html";

    private FilesService filesService;
    private MailService mailService;
    private PdfCreator pdfCreator;

    public ConfirmationConsumer() {
        filesService = new FilesService();
        pdfCreator = new PdfCreator();
        mailService = new MailService();
    }

    public void doStuff(ConnectionFactory factory) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, Producer.DIRECT_EXCHANGE, ROUTING_KEY);
            channel.basicConsume(QUEUE_NAME, false, (consumerTag, message) -> {
                String messageAsString = new String(message.getBody());
                System.out.println(messageAsString);
                Info info = objectMapper.readValue(messageAsString, Info.class);
                String downloadFileName = filesService.downloadFile(info, "docs/to_confirm/");

                System.out.println("first name: " + info.getFirstName() + "\n");
                System.out.println("last name: " + info.getLastName() + "\n");
                System.out.println("passport number: " + info.getPassportNumber() + "\n");
                System.out.println("file name " + downloadFileName);

                String fileName = TO_PATH + info.getEmail() + "_" + "confirmation_" + UUID.randomUUID() + ".pdf";
                List<KeyValue> keyValues = new ArrayList<>();
                keyValues.add(new KeyValue("${status}", "status " + UUID.randomUUID()));
                pdfCreator.formPdf(TEMPLATE_PATH, fileName, keyValues);
                mailService.sendMailWithAttachment(info.getEmail(), fileName);
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
