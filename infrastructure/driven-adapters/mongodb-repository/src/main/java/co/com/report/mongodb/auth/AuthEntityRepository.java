package co.com.report.mongodb.auth;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface AuthEntityRepository extends ReactiveMongoRepository<AuthEntity, String> {
    Mono<AuthEntity> findByToken(String token);
}
