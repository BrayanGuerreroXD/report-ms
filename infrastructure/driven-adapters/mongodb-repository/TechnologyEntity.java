package co.com.report.infrastructure.drivenadapters.mongodb.repository;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TechnologyEntity {
    private String name;
}
