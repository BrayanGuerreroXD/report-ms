package co.com.report.infrastructure.entrypoints.reactiveweb;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class LoggedUser {
    private String email;
}