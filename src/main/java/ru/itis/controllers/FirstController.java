package ru.itis.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.Info;
import ru.itis.producers.Producer;

@RestController
public class FirstController {
    private final static String EMPTY_ROUTING_KEY = "";
    public final static String FANOUT_EXCHANGE = "ex_fan";
    @Autowired
    private Producer producer;

    @PostMapping("/first")
    public void doStuff(@RequestBody Info info) {
        producer.sendMessage(info, EMPTY_ROUTING_KEY, FANOUT_EXCHANGE);
    }
}
