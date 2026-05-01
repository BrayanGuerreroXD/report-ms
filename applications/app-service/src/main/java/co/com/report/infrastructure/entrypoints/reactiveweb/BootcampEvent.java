package co.com.report.infrastructure.entrypoints.reactiveweb;

import lombok.*;
import java.util.List;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BootcampEvent {
    private Long externalId;
    private String name;
    private String description;
    private LocalDateTime initTime;
    private Integer duration;
    private List<CapacityEvent> capacities;
    private List<PersonEvent> people;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CapacityEvent {
        private String name;
        private List<TechnologyEvent> technologies;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TechnologyEvent {
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonEvent {
        private String name;
        private String email;
    }
}