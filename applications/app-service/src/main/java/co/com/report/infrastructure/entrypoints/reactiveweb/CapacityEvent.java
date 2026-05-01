package co.com.report.infrastructure.entrypoints.reactiveweb;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacityEvent {
    private String name;
    private List<TechnologyEvent> technologies;
}
