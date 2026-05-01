package co.com.report.usecase.bootcamp;

import co.com.report.model.bootcamp.Bootcamp;
import co.com.report.model.capacity.Capacity;
import co.com.report.model.person.Person;
import co.com.report.model.technology.Technology;
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
