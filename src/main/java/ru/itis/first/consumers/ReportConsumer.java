package ru.itis.first.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import ru.itis.Info;
import ru.itis.KeyValue;
import ru.itis.MailService;
import ru.itis.PdfCreator;
import ru.itis.producers.Producer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class ReportConsumer {
    private final static String QUEUE_NAME = "report";
    private final static String ROUTING_KEY = "user.report";

    private final static String TEMPLATE_PATH = "src/main/resources/confirmation.html";
    private final static String TO_PATH = "docs/";

    private final MailService mailService;
    private final PdfCreator pdfCreator;

    public ReportConsumer() {
        mailService = new MailService();
        pdfCreator = new PdfCreator();
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
                String fileName = TO_PATH + info.getEmail() + "_" + "report_" + UUID.randomUUID() + ".pdf";

                List<KeyValue> keyValues = new ArrayList<>();
                keyValues.add(new KeyValue("${first_name}", info.getFirstName()));
                keyValues.add(new KeyValue("${last_name}", info.getLastName()));
                keyValues.add(new KeyValue("${passport_number}", info.getPassportNumber()));

                pdfCreator.formPdf(TEMPLATE_PATH, fileName, keyValues);
                mailService.sendMailWithAttachment(info.getEmail(), fileName);
                System.out.println("message sent");
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
