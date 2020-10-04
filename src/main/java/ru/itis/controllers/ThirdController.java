package ru.itis.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.Info;
import ru.itis.producers.Producer;

@RestController
public class ThirdController {
    public final static String TOPIC_EXCHANGE = "ex_topic";
    private final static String CONFIRMED_TAXES_ROUTING_KEY = "account.confirmed.taxes";
    private final static String NOT_CONFIRMED_TAXES_ROUTING_KEY = "account.not_confirmed.taxes";
    @Autowired
    private Producer producer;

    @PostMapping("/third")
    public void doStuff(@RequestBody Info info) {
        System.out.println("third " + info);
        String routingKey;
        if (info.getStatus().equals("confirmed")) {
            routingKey = CONFIRMED_TAXES_ROUTING_KEY;
        } else {
            routingKey = NOT_CONFIRMED_TAXES_ROUTING_KEY;
        }
        producer.sendMessage(info, routingKey, TOPIC_EXCHANGE);
    }
}
