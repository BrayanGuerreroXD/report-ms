package co.com.report.domain.usecase.bootcamp;

import co.com.report.domain.model.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BootcampCountsCalculatorTest {

    private final BootcampCountsCalculator calculator = new BootcampCountsCalculator();

    @Test
    void calculate_withAllData_shouldComputeCorrectCounts() {
        Technology t1 = Technology.builder().name("Java").build();
        Technology t2 = Technology.builder().name("Python").build();
        Capacity c1 = Capacity.builder().name("Backend").technologies(List.of(t1, t2)).build();
        Person p1 = Person.builder().name("John").email("john@test.com").build();

        Bootcamp bootcamp = Bootcamp.builder()
            .externalId(1L)
            .name("Test Bootcamp")
            .capacities(List.of(c1))
            .people(List.of(p1))
            .build();

        Bootcamp result = calculator.calculate(bootcamp);

        assertEquals(1, result.getCapacityCount());
        assertEquals(1, result.getPersonCount());
        assertEquals(2, result.getTechnologyCount());
    }

    @Test
    void calculate_withNullCapacities_shouldReturnZeros() {
        Bootcamp bootcamp = Bootcamp.builder()
            .externalId(1L)
            .name("Test Bootcamp")
            .capacities(null)
            .people(List.of())
            .build();

        Bootcamp result = calculator.calculate(bootcamp);

        assertEquals(0, result.getCapacityCount());
        assertEquals(0, result.getPersonCount());
        assertEquals(0, result.getTechnologyCount());
    }

    @Test
    void calculate_withMultipleCapacities_shouldSumTechnologies() {
        Technology t1 = Technology.builder().name("Java").build();
        Technology t2 = Technology.builder().name("Python").build();
        Technology t3 = Technology.builder().name("React").build();
        Capacity c1 = Capacity.builder().name("Backend").technologies(List.of(t1, t2)).build();
        Capacity c2 = Capacity.builder().name("Frontend").technologies(List.of(t3)).build();

        Bootcamp bootcamp = Bootcamp.builder()
            .externalId(1L)
            .name("Test Bootcamp")
            .capacities(List.of(c1, c2))
            .people(List.of())
            .build();

        Bootcamp result = calculator.calculate(bootcamp);

        assertEquals(2, result.getCapacityCount());
        assertEquals(0, result.getPersonCount());
        assertEquals(3, result.getTechnologyCount());
    }
}