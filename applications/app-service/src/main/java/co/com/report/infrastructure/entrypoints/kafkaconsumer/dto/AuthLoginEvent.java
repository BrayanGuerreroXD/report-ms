package co.com.report.infrastructure.entrypoints.kafkaconsumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginEvent {
    private String name;
    private String email;
    private String token;
    private Integer expiresIn;
}