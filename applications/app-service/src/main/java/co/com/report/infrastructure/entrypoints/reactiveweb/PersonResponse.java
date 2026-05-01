package co.com.report.infrastructure.entrypoints.reactiveweb;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonResponse {
    private String name;
    private String email;
}
