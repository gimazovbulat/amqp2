package ru.itis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Info {
    private String firstName;
    private String lastName;
    private String passportNumber;
    private String email;
    private String url;
    private String status;
}