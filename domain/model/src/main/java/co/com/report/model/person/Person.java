package co.com.report.model.person;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
public class Person {
    private String name;
    private String email;
}
