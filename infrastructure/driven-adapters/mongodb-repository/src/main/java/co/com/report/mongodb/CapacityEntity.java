package co.com.report.mongodb;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CapacityEntity {
    private String name;
    private List<TechnologyEntity> technologies;
}
