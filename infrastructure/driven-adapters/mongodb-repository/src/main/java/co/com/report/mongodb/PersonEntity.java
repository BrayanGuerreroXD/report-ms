package co.com.report.mongodb;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PersonEntity {
    private String name;
    private String email;
}
