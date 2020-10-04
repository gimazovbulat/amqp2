package ru.itis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MainMain {
    public static void main(String[] args) throws JsonProcessingException {
        Info info = Info.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .passportNumber("123")
                .url("https://mykaleidoscope.ru/uploads/posts/2018-10/1539339930_otkrytka_pleykast__ampquotdobroe_utro.jpg")
                .status("not confirmed")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(info));
    }
}
