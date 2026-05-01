package co.com.report.model.bootcamp;

import co.com.report.model.capacity.Capacity;
import co.com.report.model.person.Person;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
public class Bootcamp {
    private String id;
    private Long externalId;
    private String name;
    private String description;
    private LocalDateTime initTime;
    private Integer duration;
    private Integer capacityCount;
    private Integer personCount;
    private Integer technologyCount;
    private List<Capacity> capacities;
    private List<Person> people;
}
