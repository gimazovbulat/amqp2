package ru.itis.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.Info;
import ru.itis.producers.Producer;

@RestController
public class SecondController {
    private final static String TO_CONFIRM_ROUTING_KEY = "confirm";
    public final static String DIRECT_EXCHANGE = "ex_dir";
    @Autowired
    private Producer producer;

    @PostMapping("/second")
    public void doStuff(@RequestBody Info info) {
        producer.sendMessage(info, TO_CONFIRM_ROUTING_KEY, DIRECT_EXCHANGE);
    }
}
