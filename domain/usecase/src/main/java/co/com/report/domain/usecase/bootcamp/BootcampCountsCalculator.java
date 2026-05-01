package co.com.report.domain.usecase.bootcamp;

import co.com.report.domain.model.*;
import java.util.List;

public class BootcampCountsCalculator {

    public Bootcamp calculate(Bootcamp bootcamp) {
        int capacityCount = bootcamp.getCapacities() != null ? bootcamp.getCapacities().size() : 0;
        int personCount = bootcamp.getPeople() != null ? bootcamp.getPeople().size() : 0;
        int technologyCount = bootcamp.getCapacities() != null ?
            bootcamp.getCapacities().stream()
                .mapToInt(c -> c.getTechnologies() != null ? c.getTechnologies().size() : 0)
                .sum() : 0;

        return bootcamp.toBuilder()
            .capacityCount(capacityCount)
            .personCount(personCount)
            .technologyCount(technologyCount)
            .build();
    }
}