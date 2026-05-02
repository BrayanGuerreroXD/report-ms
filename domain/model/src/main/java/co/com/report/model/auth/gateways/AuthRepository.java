package co.com.report.model.auth.gateways;

import co.com.report.model.auth.Auth;
import reactor.core.publisher.Mono;

public interface AuthRepository {
    Mono<Auth> save(Auth auth);
    Mono<Auth> findByToken(String token);
    Mono<Auth> findByEmail(String email);
    Mono<Void> deleteByEmail(String email);
}