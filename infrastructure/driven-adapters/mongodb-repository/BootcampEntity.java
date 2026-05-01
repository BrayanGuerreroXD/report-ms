package co.com.report.infrastructure.drivenadapters.mongodb.repository;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document(collection = "bootcamps")
public class BootcampEntity {
    @Id
    private String id;
    private Long externalId;
    private String name;
    private String description;
    private LocalDateTime initTime;
    private Integer duration;
    private Integer capacityCount;
    private Integer personCount;
    private Integer technologyCount;
    private List<CapacityEntity> capacities;
    private List<PersonEntity> people;
}
