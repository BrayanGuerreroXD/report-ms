package co.com.report.model.capacity;

import co.com.report.model.technology.Technology;
import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
public class Capacity {
    private String name;
    private List<Technology> technologies;
}
