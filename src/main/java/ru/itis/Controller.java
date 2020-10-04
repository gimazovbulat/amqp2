package ru.itis;

import ru.itis.producers.Producer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Controller {
    private BufferedReader br;
    private final Producer producer;
    //routing keys
    private final static String EMPTY_ROUTING_KEY = "";
    private final static String TO_CONFIRM_ROUTING_KEY = "confirm";
    private final static String CONFIRMED_TAXES_ROUTING_KEY = "account.confirmed.taxes";
    private final static String NOT_CONFIRMED_TAXES_ROUTING_KEY = "account.not_confirmed.taxes";
    //exchanges
    public final static String FANOUT_EXCHANGE = "ex_fan";
    public final static String DIRECT_EXCHANGE = "ex_dir";
    public final static String TOPIC_EXCHANGE = "ex_topic";

    public Controller() {
        br = new BufferedReader(new InputStreamReader(System.in));
        producer = new Producer();
    }

    public void start() {
        String infoString;
        while (true) {
            try {
                String taskNumber = br.readLine().trim();
                infoString = br.readLine();
                Info info = parseInfo(infoString, taskNumber);

                String routingKey;
                String exchange;
                switch (taskNumber) {
                    case "/first": {
                        routingKey = EMPTY_ROUTING_KEY;
                        exchange = FANOUT_EXCHANGE;
                        break;
                    }
                    case "/second": {
                        routingKey = TO_CONFIRM_ROUTING_KEY;
                        exchange = DIRECT_EXCHANGE;
                        break;
                    }
                    case "/third": {
                        exchange = TOPIC_EXCHANGE;
                        if (info.getStatus().equals("confirmed")){
                            routingKey = CONFIRMED_TAXES_ROUTING_KEY;
                        } else {
                            routingKey = NOT_CONFIRMED_TAXES_ROUTING_KEY;
                        }
                        break;
                    }
                    default:
                        throw new IllegalStateException("Unexpected value: " + taskNumber);
                }
                producer.sendMessage(info, routingKey, exchange);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private Info parseInfo(String infoStr, String taskNumber) {
        String[] infoArr = infoStr.split(",");
        Info info = new Info();
        switch (taskNumber) {
            case "/first":
            case "/second": {
                String firstName = infoArr[0].trim();
                String lastName = infoArr[1].trim();
                String passportNumber = infoArr[2].trim();
                String email = infoArr[3].trim();
                String url = infoArr[4].trim();

                info = Info.builder()
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .passportNumber(passportNumber)
                        .url(url)
                        .build();
                break;
            }
            case "/third": {
                String firstName = infoArr[0].trim();
                String lastName = infoArr[1].trim();
                String passportNumber = infoArr[2].trim();
                String email = infoArr[3].trim();
                String url = infoArr[4].trim();
                String status = infoArr[5].trim();

                info = Info.builder()
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .passportNumber(passportNumber)
                        .url(url)
                        .status(status)
                        .build();
                break;
            }
        }
        return info;
    }
}
