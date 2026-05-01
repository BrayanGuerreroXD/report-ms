package co.com.report.usecase.bootcamp;

import co.com.report.model.bootcamp.Bootcamp;
import co.com.report.model.bootcamp.gateways.BootcampRepository;
import co.com.report.model.exception.GlobalExceptionEnum;
import co.com.report.model.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBootcampUseCaseTest {

    @Mock
    private BootcampRepository bootcampRepository;

    @InjectMocks
    private GetBootcampUseCase useCase;

    @Test
    void getMaxTechnologyCount_whenNoBootcamps_shouldReturnNotFound() {
        when(bootcampRepository.findAllByOrderByTechnologyCountDesc()).thenReturn(Flux.empty());

        StepVerifier.create(useCase.getBootcampWithMaxTechnologyCount())
            .expectErrorMatches(e -> e instanceof NotFoundException &&
                ((NotFoundException) e).getError() == GlobalExceptionEnum.BOOTCAMP_NOT_FOUND)
            .verify();
    }

    @Test
    void getMaxTechnologyCount_whenBootcampsExist_shouldReturnFirst() {
        Bootcamp bootcamp1 = Bootcamp.builder().externalId(1L).technologyCount(5).build();
        Bootcamp bootcamp2 = Bootcamp.builder().externalId(2L).technologyCount(3).build();

        when(bootcampRepository.findAllByOrderByTechnologyCountDesc()).thenReturn(Flux.just(bootcamp1, bootcamp2));

        StepVerifier.create(useCase.getBootcampWithMaxTechnologyCount())
            .expectNext(bootcamp1)
            .verifyComplete();
    }
}
