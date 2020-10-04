package ru.itis.third.consumers.mains;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import ru.itis.Info;
import ru.itis.KeyValue;
import ru.itis.MailService;
import ru.itis.PdfCreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static ru.itis.producers.Producer.TOPIC_EXCHANGE;

public class TaxesConfirmedConsumer {
    private final static String QUEUE_NAME = "taxes_confirmed";
    private final static String ROUTING_KEY = "account.confirmed.taxes";

    private final static String TEMPLATE_PATH = "src/main/resources/taxes.html";
    private final static String TO_PATH = "docs/taxes/";

    private final MailService mailService;
    private final PdfCreator pdfCreator;

    public TaxesConfirmedConsumer() {
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

                String fileName = TO_PATH + info.getEmail() + "_" + "taxes_" + UUID.randomUUID() + ".pdf";
                List<KeyValue> keyValues = new ArrayList<>();
                keyValues.add(new KeyValue("${sum}", String.valueOf(new Random().nextInt(1000))));
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
