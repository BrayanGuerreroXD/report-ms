package co.com.report.usecase.getbootcamp;

import co.com.report.domain.exception.GlobalExceptionEnum;
import co.com.report.domain.exception.NotFoundException;
import co.com.report.domain.model.Bootcamp;
import co.com.report.infrastructure.drivenadapters.mongodb.repository.BootcampEntityRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetBootcampUseCase implements GetBootcampService {

    private final BootcampEntityRepository bootcampRepository;

    @Override
    public Mono<Bootcamp> getBootcampWithMaxTechnologyCount() {
        return bootcampRepository.findAllByOrderByTechnologyCountDesc()
            .next()
            .switchIfEmpty(Mono.error(new NotFoundException(GlobalExceptionEnum.BOOTCAMP_NOT_FOUND)));
    }
}