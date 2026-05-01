package co.com.report.infrastructure.entrypoints.reactiveweb;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BootcampResponse {
    private String id;
    private Long externalId;
    private String name;
    private String description;
    private LocalDateTime initTime;
    private Integer duration;
    private Integer capacityCount;
    private Integer personCount;
    private Integer technologyCount;
    private List<CapacityResponse> capacities;
    private List<PersonResponse> people;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CapacityResponse {
        private String name;
        private List<TechnologyResponse> technologies;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TechnologyResponse {
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonResponse {
        private String name;
        private String email;
    }
}