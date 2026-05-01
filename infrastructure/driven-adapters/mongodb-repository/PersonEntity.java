package co.com.report.infrastructure.drivenadapters.mongodb.repository;

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
