package co.com.report.infrastructure.entrypoints.reactiveweb;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonEvent {
    private String name;
    private String email;
}
