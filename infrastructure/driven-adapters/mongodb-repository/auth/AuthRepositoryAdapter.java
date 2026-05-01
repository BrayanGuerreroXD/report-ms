package co.com.report.infrastructure.drivenadapters.mongodb.repository.auth;

import co.com.report.model.auth.Auth;
import co.com.report.model.auth.gateways.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class AuthRepositoryAdapter implements AuthRepository {

    private final AuthEntityRepository entityRepository;
    private final AuthEntityMapper mapper;

    @Override
    public Mono<Auth> save(Auth auth) {
        return entityRepository.save(mapper.toEntity(auth))
                .map(mapper::toModel);
    }

    @Override
    public Mono<Auth> findByToken(String token) {
        return entityRepository.findByToken(token)
                .map(mapper::toModel);
    }
}
