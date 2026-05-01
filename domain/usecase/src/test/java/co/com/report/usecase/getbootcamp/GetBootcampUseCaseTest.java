package co.com.report.usecase.getbootcamp;

import co.com.report.domain.exception.GlobalExceptionEnum;
import co.com.report.domain.exception.NotFoundException;
import co.com.report.domain.model.Bootcamp;
import co.com.report.usecase.getbootcamp.GetBootcampUseCase;
import co.com.report.infrastructure.drivenadapters.mongodb.repository.BootcampEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetBootcampUseCaseTest {

    @Mock
    private BootcampEntityRepository bootcampRepository;

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
}