package ru.itis.third.consumers.mains;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import ru.itis.Info;
import ru.itis.KeyValue;
import ru.itis.MailService;
import ru.itis.PdfCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static ru.itis.producers.Producer.TOPIC_EXCHANGE;

public class TaxesNotConfirmedConsumer {
    private final static String QUEUE_NAME = "taxes_not_confirmed";
    private final static String ROUTING_KEY = "account.not_confirmed.taxes";

    private final static String TEMPLATE_PATH = "src/main/resources/taxes.html";
    private final static String TO_PATH = "docs/taxes";

    private final MailService mailService;
    private final PdfCreator pdfCreator;

    public TaxesNotConfirmedConsumer() {
        mailService = new MailService();
        pdfCreator = new PdfCreator();
    }

    public void doStuff(ConnectionFactory factory) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, TOPIC_EXCHANGE, ROUTING_KEY);
            channel.basicConsume(QUEUE_NAME, false, (consumerTag, message) -> {
                String messageAsString = new String(message.getBody());
                Info info = objectMapper.readValue(messageAsString, Info.class);
                System.out.println("first name: " + info.getFirstName() + "\n");
                System.out.println("last name: " + info.getLastName() + "\n");
                System.out.println("passport number: " + info.getPassportNumber() + "\n");

                String status = "aaa";
                if (status.equals("confirmed")) {
                    String fileName = TO_PATH + info.getEmail() + "_" + "taxes_" + UUID.randomUUID() + ".pdf";
                    List<KeyValue> keyValues = new ArrayList<>();
                    keyValues.add(new KeyValue("${sum}", String.valueOf(new Random().nextInt(1000))));
                    pdfCreator.formPdf(TEMPLATE_PATH, fileName, keyValues);
                    mailService.sendMailWithAttachment(info.getEmail(), fileName);
                } else {
                    mailService.sendSimpleMail(info.getEmail(), "sorry, we couldn't send you your taxes summary," +
                            "    check your credentials");
                }
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
